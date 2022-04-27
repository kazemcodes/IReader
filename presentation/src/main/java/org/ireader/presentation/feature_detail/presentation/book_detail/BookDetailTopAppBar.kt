package org.ireader.presentation.feature_detail.presentation.book_detail

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Public
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.ireader.core_ui.ui_components.reusable_composable.TopAppBarBackButton
import org.ireader.presentation.presentation.Toolbar

@Composable
fun BookDetailTopAppBar(
    modifier: Modifier = Modifier,
    navController: NavController,
    onWebView: () -> Unit,
    onRefresh: () -> Unit,
) {
    Toolbar(
        title = {},
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 0.dp),
        backgroundColor = Color.Transparent,
        contentColor = MaterialTheme.colors.onBackground,
        elevation = 0.dp,
        actions = {
            IconButton(onClick = {
                onRefresh()
            }) {
                Icon(
                    imageVector = Icons.Default.Autorenew,
                    contentDescription = "Refresh",
                    tint = MaterialTheme.colors.onBackground,
                )
            }
            IconButton(onClick = {
                onWebView()
            }) {
                Icon(
                    imageVector = Icons.Default.Public,
                    contentDescription = "WebView",
                    tint = MaterialTheme.colors.onBackground,
                )
            }
        },
        navigationIcon = {
            TopAppBarBackButton(navController = navController)
        }
    )
}