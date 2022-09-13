package ireader.data.chapter

import ireader.common.models.entities.Chapter
import ireader.common.models.entities.toChapter
import ireader.core.api.source.model.Page

val chapterMapper = {_id: Long, book_id: Long, url: String, name: String, scanlator: String?, read: Boolean, bookmark: Boolean, last_page_read: Long, chapter_number: Float, source_order: Long, date_fetch: Long, date_upload: Long, content: List<Page> ->
    Chapter(
        name = name,
        key = url,
        bookId = book_id,
        number = chapter_number,
        dateUpload = date_upload,
        translator = scanlator?:"",
        bookmark = bookmark,
        dateFetch = date_fetch,
        read = read,
        id = _id,
        lastPageRead = last_page_read,
        content = content,
        sourceOrder =  source_order
    )
}