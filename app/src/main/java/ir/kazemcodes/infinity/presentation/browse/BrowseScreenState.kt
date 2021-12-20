package ir.kazemcodes.infinity.presentation.browse

import ir.kazemcodes.infinity.domain.models.Book

data class BrowseScreenState(
    val isLoading : Boolean = false,
    val books: List<Book> = emptyList(),
    val error: String = "",
    val page: Int = 1,
)
