package com.muedsa.agetv

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey

val KEY_DANMAKU_ENABLE = booleanPreferencesKey("danmaku_enable")

val KEY_DANMAKU_MERGE_ENABLE = booleanPreferencesKey("danmaku_merge_enable")

val KEY_DANMAKU_SIZE_SCALE = intPreferencesKey("danmaku_size_scale")

val KEY_DANMAKU_ALPHA = intPreferencesKey("danmaku_alpha")

val KEY_DANMAKU_SCREEN_PART = intPreferencesKey("danmaku_size_part")