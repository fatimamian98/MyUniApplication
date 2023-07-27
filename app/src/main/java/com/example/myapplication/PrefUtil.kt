package com.example.myapplication

import android.content.Context
import android.content.SharedPreferences

object PrefUtil {
    private val context: Context
        get() = MyApp.instance

    /** Preferences Instance **/
    private val pref: SharedPreferences by lazy {
        context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    }

    private fun edit(): SharedPreferences.Editor {
        return pref.edit()
    }

    fun clear(): Boolean {
        return pref.edit().clear().commit()
    }

    var userName: String?
        get() = pref.getString("userName", null)
        set(value) = edit().putString("userName", value).apply()

    var password: String?
        get() = pref.getString("password", "")
        set(value) = edit().putString("password", value).apply()
}