package com.muedsa.agetv.ui.features.setting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.OutlinedIconButton
import androidx.tv.material3.Text
import com.muedsa.agetv.model.LazyType
import com.muedsa.agetv.viewmodel.AppSettingViewModel
import com.muedsa.compose.tv.theme.ScreenPaddingLeft
import com.muedsa.compose.tv.widget.ErrorMessageBoxState
import com.muedsa.compose.tv.widget.FocusScaleSwitch

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun AppSettingScreen(
    viewModel: AppSettingViewModel = hiltViewModel(),
    errorMsgBoxState: ErrorMessageBoxState,
) {

    val settingLD by viewModel.settingLDSF.collectAsState()

    LaunchedEffect(key1 = settingLD) {
        if (settingLD.type == LazyType.FAILURE) {
            errorMsgBoxState.error(settingLD.error)
        }
    }

    if (settingLD.type == LazyType.SUCCESS && settingLD.data != null) {
        val settingModel = settingLD.data!!
        Column(
            modifier = Modifier.padding(
                start = ScreenPaddingLeft,
                top = ScreenPaddingLeft
            )
        ) {
            Text(
                modifier = Modifier.width(150.dp),
                text = "设置",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(30.dp))

            Row(
                modifier = Modifier.padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    modifier = Modifier.width(100.dp),
                    text = "全局弹幕开关",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleMedium
                )
                FocusScaleSwitch(
                    checked = settingModel.danmakuEnable,
                    onCheckedChange = {
                        viewModel.changeDanmakuEnable(it)
                    }
                )
            }

            Row(
                modifier = Modifier.padding(bottom = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.width(100.dp),
                    text = "弹幕缩放",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleMedium
                )

                OutlinedIconButton(onClick = {
                    viewModel.changeDanmakuSizeScale(settingModel.danmakuSizeScale - 5)
                }) {
                    Icon(imageVector = Icons.Outlined.Remove, contentDescription = "-")
                }

                Text(
                    modifier = Modifier.width(60.dp),
                    text = "${settingModel.danmakuSizeScale}%",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )

                OutlinedIconButton(onClick = {
                    viewModel.changeDanmakuSizeScale(settingModel.danmakuSizeScale + 5)
                }) {
                    Icon(imageVector = Icons.Outlined.Add, contentDescription = "+")
                }
            }


            Row(
                modifier = Modifier.padding(bottom = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.width(100.dp),
                    text = "弹幕透明度",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleMedium
                )

                OutlinedIconButton(onClick = {
                    viewModel.changeDanmakuAlpha(settingModel.danmakuAlpha - 5)
                }) {
                    Icon(imageVector = Icons.Outlined.Remove, contentDescription = "-")
                }

                Text(
                    modifier = Modifier.width(60.dp),
                    text = "${settingModel.danmakuAlpha}%",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )

                OutlinedIconButton(onClick = {
                    viewModel.changeDanmakuAlpha(settingModel.danmakuAlpha + 5)
                }) {
                    Icon(imageVector = Icons.Outlined.Add, contentDescription = "+")
                }
            }

            Row(
                modifier = Modifier.padding(bottom = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.width(100.dp),
                    text = "弹幕屏占比",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleMedium
                )

                OutlinedIconButton(onClick = {
                    viewModel.changeDanmakuScreenPart(settingModel.danmakuScreenPart - 5)
                }) {
                    Icon(imageVector = Icons.Outlined.Remove, contentDescription = "-")
                }

                Text(
                    modifier = Modifier.width(60.dp),
                    text = "${settingModel.danmakuScreenPart}%",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )

                OutlinedIconButton(onClick = {
                    viewModel.changeDanmakuScreenPart(settingModel.danmakuScreenPart + 5)
                }) {
                    Icon(imageVector = Icons.Outlined.Add, contentDescription = "+")
                }
            }
        }
    }


}