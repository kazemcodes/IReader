package org.ireader.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Dao
interface chapterDao : BaseDao<org.ireader.common_models.entities.Chapter> {

    @Query("SELECT * FROM chapter WHERE id = :chapterId Limit 1")
    fun subscribeChapterById(
        chapterId: Long,
    ): Flow<org.ireader.common_models.entities.Chapter?>

    @Query("SELECT * FROM chapter WHERE id = :chapterId Limit 1")
    suspend fun findChapterById(
        chapterId: Long,
    ): org.ireader.common_models.entities.Chapter?

    @Query("SELECT * FROM chapter")
    suspend fun findAllChapters(): List<org.ireader.common_models.entities.Chapter>

    @Query("SELECT * FROM chapter WHERE id in (:chapterId)")
    suspend fun findChapterByIdByBatch(
        chapterId: List<Long>,
    ): List<org.ireader.common_models.entities.Chapter>

    @Query("""
        SELECT *
        FROM chapter 
        WHERE bookId IN (
        SELECT library.id FROM library
        WHERE favorite = 1)
    """)
    suspend fun findAllInLibraryChapters(): List<org.ireader.common_models.entities.Chapter>


    @Query("""SELECT * FROM chapter WHERE bookId= :bookId ORDER BY
        CASE WHEN :isAsc = 1 THEN id END ASC,
        CASE WHEN :isAsc = 0 THEN  id END DESC
    """)
    fun subscribeChaptersByBookId(bookId: Long, isAsc: Boolean): Flow<List<org.ireader.common_models.entities.Chapter>>

    @Query("""SELECT * FROM chapter WHERE bookId= :bookId ORDER BY
        CASE WHEN :isAsc = 1 THEN id END ASC,
        CASE WHEN :isAsc = 0 THEN  id END DESC
    """)
    suspend fun findChaptersByBookId(bookId: Long, isAsc: Boolean): List<org.ireader.common_models.entities.Chapter>

    @Query("""SELECT * FROM chapter WHERE bookId in (:bookIds)""")
    suspend fun findChaptersByBookIds(bookIds: List<Long>): List<org.ireader.common_models.entities.Chapter>

    @Query("""SELECT * FROM chapter WHERE link= :key
    """)
    suspend fun findChaptersByKey(key: String): List<org.ireader.common_models.entities.Chapter>

    @Query("""SELECT * FROM chapter WHERE link= :key LIMIT 1
    """)
    suspend fun findChapterByKey(key: String): org.ireader.common_models.entities.Chapter?

    @Query("""SELECT * FROM chapter WHERE bookId = :bookId AND title LIKE '%' || :query || '%' ORDER BY 
        CASE WHEN :isAsc = 1 THEN id END ASC,
        CASE WHEN :isAsc = 0 THEN  id END DESC""")
    fun getChaptersForPaging(
        bookId: Long,
        isAsc: Boolean,
        query: String,
    ): Flow<org.ireader.common_models.entities.Chapter>


    @Query("SELECT * FROM chapter WHERE id = :chapterId AND bookId = :bookId Limit 1")
    fun getOneChapterForPaging(
        chapterId: Int,
        bookId: Int,
    ): Flow<org.ireader.common_models.entities.Chapter>

    @Query("""
        SELECT *
        from chapter
        GROUP BY id
        HAVING chapter.bookId == :bookId
        ORDER BY readAt DESC
        LIMIT 1
    """)
    fun subscribeLastReadChapter(bookId: Long): Flow<org.ireader.common_models.entities.Chapter?>

    @Query("""
        SELECT *
        from chapter
        GROUP BY id
        HAVING chapter.bookId == :bookId AND chapter.readAt != 0 
        ORDER BY readAt DESC
        LIMIT 1
    """)
    suspend fun findLastReadChapter(bookId: Long): org.ireader.common_models.entities.Chapter?

    @Query("SELECT * from chapter WHERE bookId = :bookId LIMIT 1")
    fun subscribeFirstChapter(bookId: Long): Flow<org.ireader.common_models.entities.Chapter?>

    @Query("SELECT * from chapter WHERE bookId = :bookId LIMIT 1")
    suspend fun findFirstChapter(bookId: Long): org.ireader.common_models.entities.Chapter?


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapters(chapters: List<org.ireader.common_models.entities.Chapter>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapter(chapter: org.ireader.common_models.entities.Chapter): Long


    @Query("DELETE FROM chapter WHERE bookId = :bookId")
    suspend fun deleteChaptersByBookId(bookId: Long)


    @Delete
    suspend fun deleteChaptersByBookId(chapters: List<org.ireader.common_models.entities.Chapter>)

    @Delete
    suspend fun deleteChapter(chapter: org.ireader.common_models.entities.Chapter)

    @Query("DELETE FROM chapter ")
    suspend fun deleteAllChapters()

    @Transaction
    suspend fun updateChapters(bookId: Long, chapters: List<org.ireader.common_models.entities.Chapter>) {
        deleteChaptersByBookId(bookId)
        insertChapters(chapters)
    }

}
