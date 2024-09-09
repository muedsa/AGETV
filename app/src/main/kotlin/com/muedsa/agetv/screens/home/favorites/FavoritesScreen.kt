package com.muedsa.agetv.screens.home.favorites

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.OutlinedButton
import androidx.tv.material3.Text
import com.muedsa.agetv.screens.NavigationItems
import com.muedsa.agetv.screens.home.useLocalHomeScreenBackgroundState
import com.muedsa.agetv.screens.navigate
import com.muedsa.agetv.theme.AgePosterSize
import com.muedsa.compose.tv.model.ContentModel
import com.muedsa.compose.tv.theme.ImageCardRowCardPadding
import com.muedsa.compose.tv.theme.ScreenPaddingLeft
import com.muedsa.compose.tv.useLocalNavHostController
import com.muedsa.compose.tv.widget.CardType
import com.muedsa.compose.tv.widget.ImageContentCard
import com.muedsa.compose.tv.widget.ScreenBackgroundType
import com.muedsa.uitl.LogUtil

@Composable
fun FavoritesScreen(
    viewModel: FavoriteViewModel = hiltViewModel()
) {
    val navController = useLocalNavHostController()
    val backgroundState = useLocalHomeScreenBackgroundState()

    val favoriteAnimeList by viewModel.favoriteAnimeSF.collectAsState()
    var deleteMode by remember {
        mutableStateOf(false)
    }

    val focusManager = LocalFocusManager.current

    BackHandler(enabled = deleteMode) {
        deleteMode = false
    }

    Column(modifier = Modifier.padding(start = ScreenPaddingLeft)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = if (deleteMode) "删除模式" else "最近追番",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.width(30.dp))
            OutlinedButton(onClick = {
                deleteMode = !deleteMode
            }) {
                Text(if (deleteMode) "退出" else "删除模式")
                Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                Icon(
                    imageVector = if (deleteMode) Icons.Outlined.Check else Icons.Outlined.Delete,
                    contentDescription = if (deleteMode) "退出" else "删除模式"
                )
            }
        }

        LazyVerticalGrid(
            modifier = Modifier
                .padding(start = 0.dp, top = 20.dp, end = 20.dp, bottom = 20.dp),
            columns = GridCells.Adaptive(AgePosterSize.width + ImageCardRowCardPadding),
            contentPadding = PaddingValues(
                top = ImageCardRowCardPadding,
                bottom = ImageCardRowCardPadding
            )
        ) {
            itemsIndexed(
                items = favoriteAnimeList,
                key = { _, item -> item.id }
            ) { index, item ->
                ImageContentCard(
                    modifier = Modifier
                        .padding(end = ImageCardRowCardPadding),
                    url = item.cover,
                    imageSize = AgePosterSize,
                    type = CardType.STANDARD,
                    model = ContentModel(
                        item.name
                    ),
                    onItemFocus = {
                        backgroundState.url = item.cover
                        backgroundState.type = ScreenBackgroundType.BLUR
                    },
                    onItemClick = {
                        LogUtil.d("Click $item")
                        if (deleteMode) {
                            if (index + 1 < favoriteAnimeList.size) {
                                focusManager.moveFocus(FocusDirection.Next)
                            } else {
                                focusManager.moveFocus(FocusDirection.Previous)
                            }
                            viewModel.remove(item)
                        } else {
                            navController.navigate(
                                NavigationItems.Detail,
                                listOf(item.id.toString())
                            )
                        }
                    }
                )
            }
        }
    }
}