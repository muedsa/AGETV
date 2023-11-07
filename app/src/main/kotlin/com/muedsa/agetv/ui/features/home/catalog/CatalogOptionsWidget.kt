package com.muedsa.agetv.ui.features.home.catalog

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.FilterChip
import androidx.tv.material3.FilterChipDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.muedsa.agetv.model.age.AgeCatalogOption

@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CatalogOptionsWidget(
    title: String,
    selectedIndex: Int,
    options: List<AgeCatalogOption>,
    onClick: (Int, AgeCatalogOption) -> Unit = { _, _ -> }
) {
    Text(
        text = title,
        color = MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.labelLarge
    )
    Spacer(modifier = Modifier.height(4.dp))
    FlowRow {
        options.forEachIndexed { index, option ->
            FilterChip(
                modifier = Modifier.padding(8.dp),
                selected = index == selectedIndex,
                leadingIcon = if (index == selectedIndex) {
                    {
                        Icon(
                            modifier = Modifier.size(FilterChipDefaults.IconSize),
                            imageVector = Icons.Outlined.Check,
                            contentDescription = "选择${option.text}"
                        )
                    }
                } else null,
                onClick = {
                    onClick(index, option)
                }
            ) {
                Text(text = option.text)
            }
        }
    }
    Divider(modifier = Modifier.padding(bottom = 10.dp))
}