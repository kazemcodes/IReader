package ireader.presentation.core

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ireader.presentation.ui.component.components.statusBarsPadding

@OptIn(ExperimentalMaterialApi::class)
@Composable
actual fun PlatformModalSheets(
    modifier: Modifier,
    state: Any,
    sheetContent: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    val bottomSheetState = state as ModalBottomSheetState
    ModalBottomSheetLayout(
        modifier = if (bottomSheetState.currentValue == ModalBottomSheetValue.Expanded) Modifier.statusBarsPadding() else Modifier,
        sheetContent = {
            Box(modifier.defaultMinSize(minHeight = 1.dp)) {
                sheetContent()
            }
        },
        sheetState = bottomSheetState,
        sheetBackgroundColor = MaterialTheme.colorScheme.background,
        sheetContentColor = MaterialTheme.colorScheme.onBackground,
        content = content
    )
}