package com.hmncube.myweather.data.database

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import javax.inject.Inject

class AppDatabase @Inject constructor(context: Context) {

    var database: SharedPreferences = context.getSharedPreferences("app_db",Context.MODE_PRIVATE)

    fun saveData(T : Any, key : String) {
        val editor = database.edit()
        val jsonString = Gson().toJson(T)
        editor?.putString(key, jsonString)
        editor?.apply()
    }

    fun deleteData(key : String) {
        val editor = database.edit()
        editor?.remove(key)
        editor?.apply()
    }

    fun readData(key : String) : String? {
        return database.getString(key, "")
    }
}