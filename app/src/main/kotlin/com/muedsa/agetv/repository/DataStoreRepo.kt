package com.muedsa.agetv.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import javax.inject.Inject

private const val PREFS_NAME = "setting"

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFS_NAME)

class DataStoreRepo @Inject constructor(private val context: Context) {

    val dataStore: DataStore<Preferences> = context.dataStore

//    suspend fun <T> get(key: Preferences.Key<T>): T? {
//        return dataStore.data.first()[key]
//    }
//
//    suspend fun collectPrefs(collector: FlowCollector<Preferences>) {
//        dataStore.data.collect(collector)
//    }
//
//    suspend fun putString(key: Preferences.Key<String>, value: String) {
//        dataStore.edit {
//            it[key] = value
//        }
//    }
}