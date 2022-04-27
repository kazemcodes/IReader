package org.ireader.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import org.ireader.common_models.entities.RemoteKeys
import org.ireader.common_models.entities.Book
import org.ireader.common_models.entities.BookItem

@Dao
interface RemoteKeysDao : BaseDao<org.ireader.common_models.entities.Book> {

    @Query("SELECT * FROM library WHERE id =:id")
    fun getExploreBookById(id: Int): Flow<org.ireader.common_models.entities.Book?>


    @Query("""
SELECT DISTINCT library.* FROM library 
        LEFT  JOIN history ON history.bookId == library.id
        JOIN  page ON library.title = page.title AND library.sourceId = page.sourceId AND tableId != 2
        GROUP BY  page.id
        ORDER BY page.id
    """)
    suspend fun findPagedExploreBooks(): List<org.ireader.common_models.entities.Book>



    @Query("""
SELECT DISTINCT library.*,0 as totalDownload 
FROM library
        LEFT  JOIN history ON history.bookId == library.id
        JOIN  page ON library.title = page.title AND library.sourceId = page.sourceId AND tableId != 2
        GROUP BY  page.id
        ORDER BY page.id
    """)
    fun subscribePagedExploreBooks(): Flow<List<org.ireader.common_models.entities.BookItem>>


    @Query("""
        SELECT DISTINCT library.* FROM library
        JOIN  page ON library.title = page.id AND library.sourceId = page.sourceId  OR tableId = 1 
        GROUP BY  library.title ORDER BY page.id
    """)
    fun getAllExploreBook(): List<org.ireader.common_models.entities.Book>?

    @Query("SELECT * FROM page WHERE title = :title")
    suspend fun getRemoteKeys(title: String): RemoteKeys

    @Transaction
    suspend fun prepareExploreMode(reset: Boolean, list: List<org.ireader.common_models.entities.Book>, keys: List<RemoteKeys>) {
        if (reset) {
            deleteUnUsedChapters()
            deleteAllRemoteKeys()
            deleteAllExploredBook()
        }
        insertBooks(list)
        insertAllRemoteKeys(keys)

    }
    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = org.ireader.common_models.entities.Book::class)
    suspend fun insertBooks(books:List<org.ireader.common_models.entities.Book>)

    @Transaction
    suspend fun clearExploreMode() {
        convertExploredTOLibraryBooks()
        deleteUnusedBooks()
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = RemoteKeys::class)
    fun insertAllRemoteKeys(remoteKeys: List<RemoteKeys>)

    @Query("DELETE FROM page")
    fun deleteAllRemoteKeys()


    @Query("""
        DELETE FROM library
        WHERE favorite = 0 
        AND tableId = 1
        AND library.id NOT IN (
        SELECT history.bookId  FROM history
        )
    """)
    fun deleteAllExploredBook()


    @Query("""
        DELETE FROM library WHERE
        favorite = 0 AND tableId = 2 AND library.id NOT IN (
        SELECT history.bookId  FROM history
        ) 
    """)
    suspend fun deleteAllSearchedBook()


    @Query("""
        DELETE FROM chapter
        WHERE chapter.bookId IN (
        SELECT library.id  FROM library
                WHERE library.favorite = 0
        ) AND bookId NOT IN (
         SELECT history.bookId  FROM history
        )
    """)
    suspend fun deleteUnUsedChapters()

    @Query("UPDATE library SET tableId = 0 WHERE tableId != 0 AND favorite = 1")
    suspend fun convertExploredTOLibraryBooks()

    @Query("""
        DELETE  FROM library 
        WHERE favorite = 0 AND id  NOT IN (
        SELECT history.bookId  FROM history
        )
    """)
    suspend fun deleteUnusedBooks()
}

