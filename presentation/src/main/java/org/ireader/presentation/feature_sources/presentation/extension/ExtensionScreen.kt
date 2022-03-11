package org.ireader.presentation.feature_sources.presentation.extension


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.ireader.core.utils.Constants.DEFAULT_ELEVATION
import org.ireader.core.utils.UiEvent
import org.ireader.domain.models.entities.Catalog
import org.ireader.domain.models.entities.CatalogLocal
import org.ireader.presentation.feature_sources.presentation.extension.composables.RemoteSourcesScreen
import org.ireader.presentation.feature_sources.presentation.extension.composables.UserSourcesScreen
import org.ireader.presentation.presentation.components.ISnackBarHost
import org.ireader.presentation.presentation.reusable_composable.MidSizeTextComposable
import org.ireader.presentation.presentation.reusable_composable.TopAppBarActionButton
import org.ireader.presentation.presentation.reusable_composable.TopAppBarSearch
import org.ireader.presentation.presentation.reusable_composable.TopAppBarTitle


@ExperimentalMaterialApi
@OptIn(ExperimentalPagerApi::class)
@Composable
fun ExtensionScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: ExtensionViewModel,
    onRefreshCatalogs: () -> Unit,
    onClickCatalog: (Catalog) -> Unit,
    onClickInstall: (Catalog) -> Unit,
    onClickUninstall: (Catalog) -> Unit,
    onClickTogglePinned: (CatalogLocal) -> Unit,
) {
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    val context = LocalContext.current


    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        event.uiText.asString(context)
                    )
                }
                else -> {}
            }
        }
    }

    val pages = listOf<String>(
        "Sources",
        "Extensions"
    )

    var searchMode by remember {
        mutableStateOf(false)
    }
    val focusManager = LocalFocusManager.current
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.systemBarsPadding(),
                title = {
                    if (!searchMode) {
                        TopAppBarTitle(title = "Extensions")
                    } else {
                        TopAppBarSearch(
                            query = viewModel.searchQuery ?: "",
                            onValueChange = {
                                viewModel.searchQuery = it
                            },
                            onSearch = {

                                focusManager.clearFocus()
                            },
                            isSearchModeEnable = searchMode)
                    }
                },
                backgroundColor = MaterialTheme.colors.background,
                contentColor = MaterialTheme.colors.onBackground,
                elevation = DEFAULT_ELEVATION,
                actions = {
                    if (pagerState.currentPage == 1) {
                        if (searchMode) {
                            TopAppBarActionButton(
                                imageVector = Icons.Default.Close,
                                title = "Close",
                                onClick = {
                                    searchMode = false
                                    viewModel.searchQuery = ""
                                },
                            )
                        } else {
                            TopAppBarActionButton(
                                imageVector = Icons.Default.Search,
                                title = "Search",
                                onClick = {
                                    searchMode = true
                                },
                            )
                        }
                        TopAppBarActionButton(
                            imageVector = Icons.Default.Refresh,
                            title = "Refresh",
                            onClick = {
                                viewModel.refreshCatalogs()
                            },
                        )
                    } else {
                        if (searchMode) {
                            TopAppBarActionButton(
                                imageVector = Icons.Default.Close,
                                title = "Close",
                                onClick = {
                                    searchMode = false
                                    viewModel.searchQuery = ""
                                },
                            )
                        } else {
                            TopAppBarActionButton(
                                imageVector = Icons.Default.TravelExplore,
                                title = "Search",
                                onClick = {
                                    searchMode = true
                                },
                            )
                        }
                    }
                },
                navigationIcon = if (searchMode) {
                    {

                        TopAppBarActionButton(imageVector = Icons.Default.ArrowBack,
                            title = "Disable Search",
                            onClick = {
                                searchMode = false
                                viewModel.searchQuery = ""
                            })

                    }
                } else null
            )
        },
        scaffoldState = scaffoldState,
        snackbarHost = { ISnackBarHost(snackBarHostState = it) },

        ) {
        // UserSourcesScreen(viewModel, navController)
        Column(modifier = Modifier.fillMaxSize()) {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                backgroundColor = MaterialTheme.colors.background,
                contentColor = MaterialTheme.colors.primary,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                    )
                },

                ) {
                // Add tabs for all of our pages
                pages.forEachIndexed { index, title ->
                    Tab(
                        text = {
                            MidSizeTextComposable(text = title, color = Color.Unspecified)
                        },
                        selected = pagerState.currentPage == index,
                        onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                        selectedContentColor = MaterialTheme.colors.primary,
                        unselectedContentColor = MaterialTheme.colors.onBackground,
                    )
                }
            }

            HorizontalPager(
                count = pages.size,
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> {
                        UserSourcesScreen(
                            onClickCatalog = onClickCatalog,
                            onClickTogglePinned = onClickTogglePinned,
                            state = viewModel
                        )
                    }
                    else -> {
                        RemoteSourcesScreen(
                            viewModel = viewModel,
                            state = viewModel,
                            onRefreshCatalogs = onRefreshCatalogs,
                            onClickInstall = onClickInstall,
                            onClickUninstall = onClickUninstall,
                        )
                    }
                }
            }
        }
    }
}


