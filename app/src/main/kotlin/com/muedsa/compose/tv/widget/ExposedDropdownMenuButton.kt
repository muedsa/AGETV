package com.muedsa.compose.tv.widget

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.OutlinedButton
import androidx.tv.material3.OutlinedButtonDefaults
import androidx.tv.material3.Text

@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun <T> ExposedDropdownMenuButton(
    expandedState: MutableState<Boolean> = remember { mutableStateOf(false) },
    itemList: List<T>,
    textFn: (Int, T) -> String,
    onSelected: (Int, T) -> Unit = { _, _ -> }
) {

    var selectedIndex by remember { mutableIntStateOf(0) }

    ExposedDropdownMenuBox(
        expanded = expandedState.value,
        onExpandedChange = { expandedState.value = !expandedState.value },
    ) {
        val buttonContainerShape = RoundedCornerShape(15)
        val buttonShape = ButtonDefaults.shape(buttonContainerShape)
        OutlinedButton(
            modifier = Modifier.menuAnchor(),
            onClick = { expandedState.value = true },
            shape = buttonShape,
            border = OutlinedButtonDefaults.border(
                border = Border(
                    border = BorderStroke(
                        width = 1.5.dp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                            alpha = 0.4f
                        )
                    ),
                    shape = buttonContainerShape
                ),
                focusedBorder = Border(
                    border = BorderStroke(
                        width = 1.65.dp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    shape = buttonContainerShape
                ),
                pressedBorder = Border(
                    border = BorderStroke(
                        width = 1.5.dp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    shape = buttonContainerShape
                ),
                disabledBorder = Border(
                    border = BorderStroke(
                        width = 1.5.dp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                            alpha = 0.2f
                        )
                    ),
                    shape = buttonContainerShape
                ),
                focusedDisabledBorder = Border(
                    border = BorderStroke(
                        width = 1.5.dp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                            alpha = 0.2f
                        )
                    ),
                    shape = buttonContainerShape
                )
            ),
            contentPadding = OutlinedButtonDefaults.ButtonWithIconContentPadding
        ) {
            Text(
                text = textFn(selectedIndex, itemList[selectedIndex])
            )
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Icon(
                Icons.Outlined.ArrowDropDown,
                null,
                Modifier.rotate(if (expandedState.value) 180f else 0f)
            )
        }
        ExposedDropdownMenu(
            expanded = expandedState.value,
            onDismissRequest = { expandedState.value = false },
        ) {
            itemList.forEachIndexed { index, item ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = textFn(index, item)
                        )
                    },
                    onClick = {
                        selectedIndex = index
                        expandedState.value = false
                        onSelected(selectedIndex, item)
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}