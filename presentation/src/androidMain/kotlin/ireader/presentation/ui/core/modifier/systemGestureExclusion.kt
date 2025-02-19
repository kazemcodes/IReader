package ireader.presentation.ui.core.modifier

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope

actual fun Modifier.systemGestureExclusion(): Modifier = this.systemGestureExclusion()
actual fun Modifier.navigationBarsPadding(): Modifier = this.navigationBarsPadding()
actual fun Modifier.systemBarsPadding(): Modifier = this.systemBarsPadding()


actual fun Modifier.supportDesktopScroll(
    scrollState: ScrollState,
    scope: CoroutineScope,enable:Boolean
): Modifier = this


actual fun Modifier.supportDesktopScroll(
    scrollState: LazyListState,
    scope: CoroutineScope,enable:Boolean
): Modifier = this