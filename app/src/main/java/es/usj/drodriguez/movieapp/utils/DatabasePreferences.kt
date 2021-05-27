package es.usj.drodriguez.movieapp.utils

import android.content.Context

class DatabasePreferences(private val context: Context) {
    private fun edit(mode: Int) = getDatabaseSharedPreferences(mode).edit()
    private fun getDatabaseSharedPreferences(mode: Int) = context.getSharedPreferences(PREFERENCES_FILE, mode)


    fun setHost(host: String?, mode: Int){
        edit(mode).putString(PREF_HOST, host).apply()
    }
    fun getHost(mode: Int) = getDatabaseSharedPreferences(mode).getString(PREF_HOST, "")

    fun setOnline(isOnline: Boolean, mode: Int) {
        edit(mode).putBoolean(PREF_ONLINE, isOnline).apply()
    }
    fun isOnline(mode: Int) = getDatabaseSharedPreferences(mode).getBoolean(PREF_ONLINE, true)
    companion object{
        private const val PREFERENCES_FILE = "database"
        private const val PREF_HOST = "host"
        private const val PREF_ONLINE = "online"
    }
}