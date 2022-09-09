package ireader.ui.sources.extension

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ireader.common.extensions.launchIO
import ireader.common.models.entities.Catalog
import ireader.common.models.entities.CatalogInstalled
import ireader.common.models.entities.CatalogLocal
import ireader.common.models.entities.CatalogRemote
import ireader.common.models.entities.SourceState
import ireader.common.resources.UiText
import ireader.core.api.os.InstallStep
import ireader.core.catalogs.interactor.GetCatalogsByType
import ireader.core.catalogs.interactor.InstallCatalog
import ireader.core.catalogs.interactor.SyncRemoteCatalogs
import ireader.core.catalogs.interactor.TogglePinnedCatalog
import ireader.core.catalogs.interactor.UninstallCatalog
import ireader.core.catalogs.interactor.UpdateCatalog
import ireader.core.ui.exceptionHandler
import ireader.core.ui.preferences.UiPreferences
import ireader.core.ui.viewmodel.BaseViewModel
import ireader.core.ui.viewmodel.showSnackBar
import ireader.ui.sources.extension.composables.SourceUiModel
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class ExtensionViewModel(
    private val state: CatalogsStateImpl,
    private val getCatalogsByType: GetCatalogsByType,
    private val updateCatalog: UpdateCatalog,
    private val installCatalog: InstallCatalog,
    private val uninstallCatalog: UninstallCatalog,
    private val togglePinnedCatalog: TogglePinnedCatalog,
    private val syncRemoteCatalogs: SyncRemoteCatalogs,
    val uiPreferences: UiPreferences,
) : BaseViewModel(), CatalogsState by state {

    val incognito = uiPreferences.incognitoMode().asState()
    val lastUsedSource = uiPreferences.lastUsedSource().asState()
    val userSources: List<SourceUiModel> by derivedStateOf {

        val list = mutableListOf<SourceUiModel>()
        if (lastUsedSource.value != -1L) {

            (pinnedCatalogs + unpinnedCatalogs).firstOrNull {
                it.sourceId == lastUsedSource.value
            }?.let { c ->
                list.addAll(
                    listOf<SourceUiModel>(
                        SourceUiModel.Header(SourceKeys.LAST_USED_KEY),
                        SourceUiModel.Item(c, SourceState.LastUsed)

                    )
                )
            }
        }

        if (pinnedCatalogs.isNotEmpty()) {
            list.addAll(
                listOf<SourceUiModel>(
                    SourceUiModel.Header(SourceKeys.PINNED_KEY),
                    *pinnedCatalogs.map { source ->
                        SourceUiModel.Item(source, SourceState.Pinned)
                    }.toTypedArray()
                )
            )
        }
        if (unpinnedCatalogs.isNotEmpty()) {
            list.addAll(
                unpinnedCatalogs.groupBy {
                    it.source?.lang ?: "others"
                }.flatMap {
                    listOf<SourceUiModel>(
                        SourceUiModel.Header(it.key),
                        *it.value.map { source ->
                            SourceUiModel.Item(source, SourceState.UnPinned)
                        }.toTypedArray()
                    )
                }
            )
        }
        list
    }

    var getCatalogJob: Job? = null

    var installerJobs: MutableMap<Long, Job> = mutableMapOf()

    override fun showSnackBar(message: UiText?) {
        viewModelScope.launch {
            message?.let {
                _eventFlow.showSnackBar(it)
            }
        }
    }

    init {
        scope.launch {
            getCatalogsByType.subscribe(excludeRemoteInstalled = true)
                .onEach { (pinned, unpinned, remote) ->
                    state.allPinnedCatalogs = pinned
                    state.allUnpinnedCatalogs = unpinned
                    state.allRemoteCatalogs = remote

                    state.languageChoices = getLanguageChoices(remote, pinned + unpinned)
                }.launchIn(scope)
        }

        // Update catalogs whenever the query changes or there's a new update from the backend

        snapshotFlow { state.allPinnedCatalogs.filteredByQuery(searchQuery) }
            .onEach { state.pinnedCatalogs = it }.launchIn(viewModelScope)

        snapshotFlow { state.allUnpinnedCatalogs.filteredByQuery(searchQuery) }
            .onEach { state.unpinnedCatalogs = it }.launchIn(viewModelScope)

        snapshotFlow {
            state.allRemoteCatalogs.filteredByQuery(searchQuery)
                .filteredByChoice(selectedLanguage)
        }
            .onEach { state.remoteCatalogs = it }.launchIn(viewModelScope)
    }

    fun installCatalog(catalog: Catalog) {
        installerJobs.putIfAbsent(catalog.sourceId, Job())
        installerJobs[catalog.sourceId] =
            scope.launch {
                val isUpdate = catalog is CatalogInstalled
                val (pkgName, flow) = if (isUpdate) {
                    catalog as CatalogInstalled
                    catalog.pkgName to updateCatalog.await(catalog)
                } else {
                    catalog as CatalogRemote
                    catalog.pkgName to installCatalog.await(catalog)
                }
                flow.collect { step ->
                    if (step is InstallStep.Error) {
                        showSnackBar(UiText.DynamicString(step.error))
                    }
                    state.installSteps = if (step != InstallStep.Success) {
                        if (step is InstallStep.Error) {
                            showSnackBar(UiText.DynamicString(step.error))
                        }
                        installSteps + (pkgName to step)
                    } else {
                        installSteps - pkgName
                    }
                }
            }
    }

    fun togglePinnedCatalog(catalog: Catalog) {
        scope.launch {
            togglePinnedCatalog.await(catalog)
        }
    }

    fun uninstallCatalog(catalog: Catalog) {
        scope.launch {
            if (catalog is CatalogInstalled) {
                uninstallCatalog.await(catalog)
            }
        }
    }

    fun cancelCatalogJob(catalog: Catalog) {

        installerJobs[catalog.sourceId]?.cancel()
        installerJobs.remove(catalog.sourceId)
        if (catalog is CatalogRemote) {
            state.installSteps = installSteps + (catalog.pkgName to InstallStep.Idle)
        }
        if (catalog is CatalogInstalled) {
            state.installSteps = installSteps + (catalog.pkgName to InstallStep.Idle)
        }
    }

    fun refreshCatalogs() {
        scope.launch(Dispatchers.IO) {
            state.isRefreshing = true
            syncRemoteCatalogs.await(true, onError = { error ->
                showSnackBar(exceptionHandler(error))
            })
            state.isRefreshing = false
        }
    }

    private fun getLanguageChoices(
        remote: List<CatalogRemote>,
        local: List<CatalogLocal>,
    ): List<LanguageChoice> {
        val knownLanguages = mutableListOf<LanguageChoice.One>()
        val unknownLanguages = mutableListOf<Language>()

        val languageComparators = UserLanguagesComparator()
            .then(InstalledLanguagesComparator(local))
            .thenBy { it.code }

        remote.asSequence()
            .map { Language(it.lang) }
            .distinct()
            .sortedWith(languageComparators)
            .forEach { code ->
                if (code.toEmoji() != null) {
                    knownLanguages.add(LanguageChoice.One(code))
                } else {
                    unknownLanguages.add(code)
                }
            }

        val languages = mutableListOf<LanguageChoice>()
        languages.add(LanguageChoice.All)
        languages.addAll(knownLanguages)
        if (unknownLanguages.isNotEmpty()) {
            languages.add(LanguageChoice.Others(unknownLanguages))
        }

        return languages
    }

    private fun <T : Catalog> List<T>.filteredByQuery(query: String?): List<T> {
        return if (query == null) {
            this
        } else {
            filter { it.name.contains(query, true) }
        }
    }

    private fun List<CatalogRemote>.filteredByChoice(choice: LanguageChoice): List<CatalogRemote> {
        return when (choice) {
            LanguageChoice.All -> this
            is LanguageChoice.One -> filter { choice.language.code == it.lang }
            is LanguageChoice.Others -> {
                val codes = choice.languages.map { it.code }
                filter { it.lang in codes }
            }
        }
    }

}
