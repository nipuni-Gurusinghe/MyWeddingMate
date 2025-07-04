package com.example.myweddingmateapp

import android.content.Context
import android.content.SharedPreferences


class PrefsHelper private constructor(context: Context) {
    private val sharedPref = context.getSharedPreferences("MyWeddingPrefs", Context.MODE_PRIVATE)

    companion object {
        @Volatile
        private var instance: PrefsHelper? = null

        fun getInstance(context: Context): PrefsHelper {
            return instance ?: synchronized(this) {
                instance ?: PrefsHelper(context).also { instance = it }
            }
        }
    }

    fun saveFavorites(favorites: Set<String>) {
        sharedPref.edit().putStringSet("favorites", favorites).apply()
    }

    fun getFavorites(): Set<String> {
        return sharedPref.getStringSet("favorites", mutableSetOf()) ?: mutableSetOf()
    }


}