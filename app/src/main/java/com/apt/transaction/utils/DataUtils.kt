package com.apt.transaction.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class DataUtils {

    private var dataStore: DataStore<Preferences>? = null

    companion object {
        private var instance: DataUtils? = null
        fun getInstance() = instance ?: synchronized(DataUtils::class.java) {
            instance ?: DataUtils()
        }
    }

    fun init(context: Context, name: String) {
        LogUtils.i(msg = "DataUtils init...")
        dataStore = PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            ),
            migrations = listOf(
                SharedPreferencesMigration(
                    context,
                    name
                )
            ),
            produceFile = { context.preferencesDataStoreFile(name) }
        )
    }

    suspend fun putString(key: String, value: String) {
        LogUtils.i(msg = "DataUtils putString,key is $key, value is $value")
        dataStore?.edit { settings ->
            settings[stringPreferencesKey(key)] = value
        }
    }

    suspend fun getString(key: String, defaultValue: String = "") =
        dataStore?.data?.map { preferences ->
            preferences[stringPreferencesKey(key)] ?: defaultValue
        }?.first() ?: ""
}