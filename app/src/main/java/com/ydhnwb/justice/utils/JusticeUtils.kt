package com.ydhnwb.justice.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE

class JusticeUtils {
    companion object {
        var API_ENDPOINT = "http://juice-apps.herokuapp.com/"

        fun getToken(c : Context) : String? {
            val s = c.getSharedPreferences("USER", MODE_PRIVATE)
            return s?.getString("TOKEN", null)
        }

        fun setToken(context: Context, token : String){
            val pref = context.getSharedPreferences("USER", MODE_PRIVATE)
            pref.edit().apply{
                putString("TOKEN", token)
                apply()
            }
        }

        fun clearToken(context: Context){
            val pref = context.getSharedPreferences("USER", MODE_PRIVATE)
            pref.edit().clear().apply()
        }
    }
}