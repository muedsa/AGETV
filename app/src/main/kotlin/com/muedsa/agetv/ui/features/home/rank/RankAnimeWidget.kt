package com.muedsa.agetv.ui.features.home.rank

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Card
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.muedsa.agetv.model.age.RankAnimeModel

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun RankAnimeWidget(
    model: RankAnimeModel,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        onClick = onClick
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
                    .wrapContentWidth(Alignment.Start),
                text = model.title,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium,
            )
            if (model.ccNt.isNotBlank()) {
                Text(
                    modifier = Modifier
                        .padding(8.dp)
                        .wrapContentWidth(Alignment.End),
                    text = "\uD83D\uDD25${model.ccNt}",
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }

    }
}