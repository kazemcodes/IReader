package org.ireader.presentation.feature_reader.presentation.reader.components


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.ireader.core_ui.theme.fonts
import org.ireader.domain.view_models.reader.ReaderEvent
import org.ireader.domain.view_models.reader.ReaderScreenViewModel
import org.ireader.presentation.presentation.reusable_composable.CaptionTextComposable


@Composable
fun FontChip(
    modifier: Modifier = Modifier,
    viewModel: ReaderScreenViewModel,
) {
    var expanded by remember { mutableStateOf(false) }

    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
        Text(
            modifier = Modifier.width(100.dp),
            text = "Font",
            fontSize = 12.sp,
            style = TextStyle(fontWeight = FontWeight.W400)
        )
        LazyRow {
            items(fonts) { font ->
                Spacer(modifier = modifier.width(10.dp))
                Box(modifier = modifier
                    .height(30.dp)
                    .clip(RectangleShape)
                    .background(MaterialTheme.colors.background)
                    .border(2.dp,
                        if (font == viewModel.font) MaterialTheme.colors.primary else MaterialTheme.colors.onBackground,
                        CircleShape)
                    .clickable { viewModel.onEvent(ReaderEvent.ChangeFont(font)) },
                    contentAlignment = Alignment.Center
                ) {
                    CaptionTextComposable(text = font.fontName,
                        maxLine = 1,
                        align = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp))
                }
            }
        }
//        Box(
//            modifier = modifier
//                .fillMaxWidth(.8f)
//                .clickable {
//                    expanded = !expanded
//                }
//                .border(.8.dp, color = MaterialTheme.colors.onBackground.copy(.5f))
//                .padding(8.dp),
//            contentAlignment = Alignment.Center
//        ) {
//            Text(text = viewModel.font.fontName)
//            DropdownMenu(
//                modifier = Modifier.background(MaterialTheme.colors.background
//                ),
//                expanded = expanded,
//                onDismissRequest = { expanded = false },
//            ) {
//                fonts.forEach { font ->
//                    DropdownMenuItem(onClick = {
//                        viewModel.onEvent(ReaderEvent.ChangeFont(font))
//                    }) {
//                        Text(text = font.fontName, color = MaterialTheme.colors.onBackground)
//                    }
//                }
//            }
//        }
    }


}


//    ExposedDropdownMenuBox(
//        expanded = expanded,
//        onExpandedChange = {
//            expanded = !expanded
//        },
//        modifier.fillMaxWidth()
//    ) {
//        TextField(
//            readOnly = true,
//            value = selectedOptionText,
//            onValueChange = { },
//            label = { Text("Font") },
//            trailingIcon = {
//                ExposedDropdownMenuDefaults.TrailingIcon(
//                    expanded = expanded
//                )
//            },
//            colors = ExposedDropdownMenuDefaults.textFieldColors()
//        )
//        ExposedDropdownMenu(
//            expanded = expanded,
//            onDismissRequest = {
//                expanded = false
//            }
//        ) {
//            options.forEach { selectionOption ->
//                DropdownMenuItem(
//                    onClick = {
//                        onClick(selectedFont[options.indexOf(selectedOptionText)])
//                        selectedOptionText = selectionOption
//                        expanded = false
//                    }
//                ) {
//                    Text(text = selectionOption)
//                }
//            }
//        }
//    }
//}
//@ExperimentalComposeUiApi
//@Composable
//fun ComposeMenu(
//    menuItems: List<String>,
//    menuExpandedState: Boolean,
//    seletedIndex : Int,
//    updateMenuExpandStatus : () -> Unit,
//    onDismissMenuView : () -> Unit,
//    onMenuItemclick : (Int) -> Unit,
//) {
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .wrapContentSize(Alignment.TopStart)
//            .padding(top = 10.dp)
//            .border(0.5.dp, MaterialTheme.colors.onSurface.copy(alpha = 0.5f))
//            .clickable(
//                onClick = {
//                    updateMenuExpandStatus()
//                },
//            ),
//
//        ) {
//
//        BoxWithConstraints(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp)
//        ) {
//
//            val (lable, iconView) = createRefs()
//
//            Text(
//                text = menuItems[seletedIndex],
//                color = seletedIndex.dropdownDisableColor(),
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .constrainAs(lable) {
//                        top.linkTo(parent.top)
//                        bottom.linkTo(parent.bottom)
//                        start.linkTo(parent.start)
//                        end.linkTo(iconView.start)
//                        width = Dimension.fillToConstraints
//                    }
//            )
//
//            val displayIcon: Painter = painterResource(
//                id = R.drawable.ic_drop_down
//            )
//
//            Icon(
//                painter = displayIcon,
//                contentDescription = null,
//                modifier = Modifier
//                    .size(20.dp, 20.dp)
//                    .constrainAs(iconView) {
//                        end.linkTo(parent.end)
//                        top.linkTo(parent.top)
//                        bottom.linkTo(parent.bottom)
//                    },
//                tint = MaterialTheme.colors.onSurface
//            )
//
//            DropdownMenu(
//                expanded = menuExpandedState,
//                onDismissRequest = { onDismissMenuView() },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .background(MaterialTheme.colors.surface)
//            ) {
//                menuItems.forEachIndexed { index, title ->
//                    DropdownMenuItem(
//                        onClick = {
//                            if (index != 0) {
//                                onMenuItemclick(index)
//                            }
//                        }) {
//                        Text(text = title)
//                    }
//                }
//            }
//        }
//    }
//}
//
