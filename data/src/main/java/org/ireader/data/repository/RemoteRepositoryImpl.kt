package org.ireader.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.PagingSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.ireader.core.utils.Constants
import org.ireader.core.utils.UiText
import org.ireader.data.R
import org.ireader.data.local.AppDatabase
import org.ireader.data.local.dao.RemoteKeysDao
import org.ireader.data.repository.mediator.GetRemoteBooksByRemoteMediator
import org.ireader.domain.models.entities.Book
import org.ireader.domain.models.entities.Book.Companion.toBookInfo
import org.ireader.domain.models.entities.Chapter
import org.ireader.domain.models.entities.toChapterInfo
import org.ireader.domain.repository.RemoteRepository
import org.ireader.domain.utils.Resource
import retrofit2.HttpException
import tachiyomi.source.CatalogSource
import tachiyomi.source.Source
import tachiyomi.source.model.Filter
import tachiyomi.source.model.Listing
import tachiyomi.source.model.MangaInfo
import tachiyomi.source.model.Text
import timber.log.Timber
import java.io.IOException

class RemoteRepositoryImpl(
    private val remoteKeysDao: RemoteKeysDao,
    private val database: AppDatabase,
) : RemoteRepository {


    override suspend fun getRemoteBookDetail(book: Book, source: Source): MangaInfo {
        return source.getMangaDetails(book.toBookInfo(source.id))
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getAllExploreBookByPaging(
        source: CatalogSource,
        listing: Listing?,
        filters: List<Filter<*>>?,
        query: String?,
    ): PagingSource<Int, Book> {
        return remoteKeysDao.getAllExploreBookByPaging()
    }


    override fun getRemoteReadingContentUseCase(
        chapter: Chapter,
        source: CatalogSource,
    ): Flow<Resource<List<String>>> = flow<Resource<List<String>>> {
        try {
            Timber.d("Timber: GetRemoteReadingContentUseCase was Called")
            val page = source.getPageList(chapter.toChapterInfo())
            val content = mutableListOf<String>()
            page.forEach {
                when (it) {
                    is Text -> {
                        content.add(it.text)
                    }
                    else -> {}
                }
            }

            if (content.joinToString()
                    .isBlank() || content.contains(Constants.CLOUDFLARE_LOG)
            ) {
                emit(Resource.Error<List<String>>(uiText = UiText.StringResource(R.string.cant_get_content)))
            } else {
                Timber.d("Timber: GetRemoteReadingContentUseCase was Finished Successfully")
                emit(Resource.Success<List<String>>(content))

            }

        } catch (e: HttpException) {
            emit(Resource.Error<List<String>>(uiText = UiText.ExceptionString(e)))
        } catch (e: IOException) {
            emit(Resource.Error<List<String>>(uiText = UiText.StringResource(R.string.noInternetError)))
        } catch (e: Exception) {
            emit(Resource.Error<List<String>>(uiText = UiText.ExceptionString(e)))
        }
    }

//    @OptIn(ExperimentalPagingApi::class)
//    override fun getExploreMediator(
//        source: Source,
//        exploreType: ExploreType,
//        query: String?,
//    ): RemoteMediator<Int, Book> {
//        return ExploreRemoteMediator(source = source,
//            query = query,
//            exploreType = exploreType,
//            database = database)
//    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getRemoteBooksByRemoteMediator(
        source: CatalogSource,
        listing: Listing?,
        filters: List<Filter<*>>?,
        query: String?,
        pageSize: Int,
        maxSize: Int,
    ): Flow<PagingData<Book>> {
        return GetRemoteBooksByRemoteMediator(database = database,
            remoteRepository = this@RemoteRepositoryImpl).invoke(source, listing, filters, query)
    }


}


