package com.example.storyapp.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token")

class TokenPreference private constructor(private val dataStore: DataStore<Preferences>){

    private val TOKEN_KEY = stringPreferencesKey("token_key")
    private val IS_VALID = booleanPreferencesKey("is_valid")

    fun getTokenKey(): Flow<String> {
        return dataStore.data.map{preferences->
            preferences[TOKEN_KEY]?:"token_key"
        }
    }

    suspend fun setTokenKey(token_auth: String){
        dataStore.edit { preferences->
            preferences[TOKEN_KEY] = token_auth
        }
    }

    fun getValid():Flow<Boolean>{
        return dataStore.data.map {preferences->
            preferences[IS_VALID]?: false
        }
    }

    suspend fun setValid(valid:Boolean){
        dataStore.edit {preferences->
            preferences[IS_VALID] = valid
        }
    }


    companion object {
        @Volatile
        private var INSTANCE: TokenPreference? = null

        fun getInstance(dataStore: DataStore<Preferences>): TokenPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = TokenPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}