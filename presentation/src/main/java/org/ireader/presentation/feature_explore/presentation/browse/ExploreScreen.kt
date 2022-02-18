package org.ireader.presentation.feature_explore.presentation.browse

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.ExperimentalPagingApi
import androidx.paging.compose.collectAsLazyPagingItems
import org.ireader.core.R
import org.ireader.domain.models.ExploreType
import org.ireader.domain.models.layouts
import org.ireader.domain.models.source.FetchType
import org.ireader.domain.view_models.explore.ExploreScreenEvents
import org.ireader.domain.view_models.explore.ExploreViewModel
import org.ireader.presentation.feature_library.presentation.components.LayoutComposable
import org.ireader.presentation.feature_library.presentation.components.RadioButtonWithTitleComposable
import org.ireader.presentation.presentation.EmptyScreenComposable
import org.ireader.presentation.presentation.components.handlePagingResult
import org.ireader.presentation.presentation.reusable_composable.*
import org.ireader.presentation.ui.BookDetailScreenSpec
import org.ireader.presentation.ui.WebViewScreenSpec


@OptIn(ExperimentalFoundationApi::class)
@ExperimentalPagingApi
@Composable
fun ExploreScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: ExploreViewModel = hiltViewModel(),
) {
    val scrollState = rememberLazyListState()
    val state = viewModel.state.value

    val source = viewModel.state.value.source
    val focusManager = LocalFocusManager.current

    val books = viewModel.books.collectAsLazyPagingItems()

    val gridState = rememberLazyGridState()
    val lazyListState = rememberLazyListState()

    if (source != null) {

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        if (!state.isSearchModeEnable) {
                            TopAppBarTitle(title = source.name)
                        } else {
                            TopAppBarSearch(query = state.searchQuery,
                                onValueChange = {
                                    viewModel.onEvent(ExploreScreenEvents.OnQueryChange(it))
                                },
                                onSearch = {
                                    viewModel.getBooks(
                                        query = state.searchQuery,
                                        type = ExploreType.Search,
                                        source = source
                                    )
                                    focusManager.clearFocus()
                                },
                                isSearchModeEnable = state.searchQuery.isNotBlank())
                        }


                    },
                    backgroundColor = MaterialTheme.colors.background,
                    actions = {
                        if (state.isSearchModeEnable) {
                            TopAppBarActionButton(
                                imageVector = Icons.Default.Close,
                                title = "Close",
                                onClick = {
                                    viewModel.onEvent(ExploreScreenEvents.ToggleSearchMode(false))
                                },
                            )
                        } else if (source.supportSearch) {
                            TopAppBarActionButton(
                                imageVector = Icons.Default.Search,
                                title = "Search",
                                onClick = {
                                    viewModel.onEvent(ExploreScreenEvents.ToggleSearchMode(true))
                                },
                            )
                        }
                        TopAppBarActionButton(
                            imageVector = Icons.Default.Public,
                            title = "WebView",
                            onClick = {
                                navController.navigate(WebViewScreenSpec.buildRoute(
                                    sourceId = source.sourceId,
                                    fetchType = FetchType.LatestFetchType.index,
                                    url = source.baseUrl
                                )
                                )
                            },
                        )
                        TopAppBarActionButton(
                            imageVector = Icons.Default.GridView,
                            title = "Menu",
                            onClick = {
                                viewModel.onEvent(ExploreScreenEvents.ToggleMenuDropDown(true))
                            },
                        )
                        DropdownMenu(
                            modifier = Modifier.background(MaterialTheme.colors.background),
                            expanded = viewModel.state.value.isMenuDropDownShown,
                            onDismissRequest = {
                                viewModel.onEvent(ExploreScreenEvents.ToggleMenuDropDown(false))
                            }
                        ) {
                            layouts.forEach { layout ->
                                DropdownMenuItem(onClick = {
                                    viewModel.onEvent(ExploreScreenEvents.OnLayoutTypeChnage(
                                        layoutType = layout))
                                    viewModel.onEvent(ExploreScreenEvents.ToggleMenuDropDown(false))
                                }) {
                                    RadioButtonWithTitleComposable(
                                        text = layout.title,
                                        selected = viewModel.state.value.layout == layout.layout,
                                        onClick = {
                                            viewModel.onEvent(ExploreScreenEvents.OnLayoutTypeChnage(
                                                layoutType = layout))
                                            viewModel.onEvent(ExploreScreenEvents.ToggleMenuDropDown(
                                                false))
                                        }
                                    )
                                }
                            }
                        }


                    },
                    navigationIcon = {
                        TopAppBarBackButton(navController = navController)

                    }
                )
            }
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                val result = handlePagingResult(books = books, onEmptyResult = {
                    Column(
                        modifier = modifier
                            .fillMaxSize()
                            .align(Alignment.Center)
                            .padding(bottom = 30.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        ErrorTextWithEmojis(error = "the Source didn't get anything.",
                            modifier = Modifier
                                .padding(20.dp))
                        Row(Modifier
                            .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier
                                .weight(.5f)
                                .wrapContentSize(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                TopAppBarActionButton(imageVector = Icons.Default.Refresh,
                                    title = "Retry",
                                    onClick = { viewModel.getBooks(source = source) })
                                SmallTextComposable(text = "Retry")
                            }
                            Column(Modifier
                                .weight(.5f)
                                .wrapContentSize(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                TopAppBarActionButton(imageVector = Icons.Default.Public,
                                    title = "Open in WebView",
                                    onClick = {
                                        navController.navigate(WebViewScreenSpec.buildRoute(
                                            sourceId = source.sourceId,
                                            fetchType = FetchType.LatestFetchType.index,
                                            url = source.baseUrl
                                        )
                                        )
                                    })
                                SmallTextComposable(text = "Open in WebView")
                            }

                        }

                    }

                }, onErrorResult = { error ->
                    Column(
                        modifier = modifier
                            .fillMaxSize()
                            .align(Alignment.Center)
                            .padding(bottom = 30.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        ErrorTextWithEmojis(
                            error = error.toString(),
                            modifier = Modifier
                                .padding(20.dp)
                        )
                        Row(Modifier
                            .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier
                                .weight(.5f)
                                .wrapContentSize(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                TopAppBarActionButton(imageVector = Icons.Default.Refresh,
                                    title = "Retry",
                                    onClick = { viewModel.getBooks(source = source) })
                                SmallTextComposable(text = "Retry")
                            }
                            Column(Modifier
                                .weight(.5f)
                                .wrapContentSize(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                TopAppBarActionButton(imageVector = Icons.Default.Public,
                                    title = "Open in WebView",
                                    onClick = {
                                        navController.navigate(WebViewScreenSpec.buildRoute(
                                            sourceId = source.sourceId,
                                            fetchType = FetchType.LatestFetchType.index,
                                            url = source.baseUrl
                                        )
                                        )
                                    })
                                SmallTextComposable(text = "Open in WebView")
                            }

                        }

                    }

                })
                if (result) {
                    LayoutComposable(
                        books = books,
                        layout = state.layout,
                        scrollState = scrollState,
                        source = source,
                        navController = navController,
                        isLocal = false,
                        gridState = gridState,
                        onBookTap = { book ->
                            navController.navigate(
                                route = BookDetailScreenSpec.buildRoute(sourceId = book.sourceId,
                                    bookId = book.id)
                            )
                        }
                    )
                }
            }


        }
    } else {
        EmptyScreenComposable(navController, R.string.something_is_wrong_with_this_book)
    }
}

