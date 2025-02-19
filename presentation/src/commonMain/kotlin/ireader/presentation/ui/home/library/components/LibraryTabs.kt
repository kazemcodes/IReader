package ireader.presentation.ui.home.library.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.material3.TabRowDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.pagerTabIndicatorOffset
import ireader.domain.models.entities.CategoryWithCount
import ireader.presentation.ui.core.theme.AppColors

@OptIn(ExperimentalPagerApi::class)
@Composable
fun LibraryTabs(
    state: PagerState,
    visible: Boolean,
    categories: List<CategoryWithCount>,
    showCount: Boolean,
    onClickTab: (Int) -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        androidx.compose.material.ScrollableTabRow(
            selectedTabIndex = state.currentPage,
            backgroundColor = AppColors.current.bars,
            contentColor = AppColors.current.onBars,
            edgePadding = 0.dp,
            indicator = { TabRowDefaults.Indicator(Modifier.pagerTabIndicatorOffset(state, it)) }
        ) {
            categories.forEachIndexed { i, category ->
                Tab(
                    selected = state.currentPage == i,
                    onClick = { onClickTab(i) },
                    text = {
                        Text(
                            category.visibleName + if (!showCount) {
                                ""
                            } else {
                                " (${category.bookCount})"
                            }
                        )
                    }
                )
            }
        }
    }
}
