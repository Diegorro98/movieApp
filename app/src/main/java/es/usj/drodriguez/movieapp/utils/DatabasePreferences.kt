package es.usj.drodriguez.movieapp.utils

import android.content.Context

class DatabasePreferences(private val context: Context) {
    private val PREFERENCES_FILE = "database"
    private val PREF_HOST = "host"
    private val PREF_LAST_UPDATE = "last_update"
    private val PREF_ONLINE = "online"
    private fun edit(mode: Int) = getDatabaseSharedPreferences(mode).edit()
    private fun getDatabaseSharedPreferences(mode: Int) = context.getSharedPreferences(PREFERENCES_FILE, mode)


    fun setHost(host: String?, mode: Int){
        edit(mode).putString(PREF_HOST, host).apply()
    }
    fun getHost(mode: Int) = getDatabaseSharedPreferences(mode).getString(PREF_HOST, "")

    fun setLastUpdate(updateTime: Long, mode: Int){
        edit(mode).putLong(PREF_LAST_UPDATE, updateTime).apply()
    }
    fun getLastUpdate(mode: Int) = getDatabaseSharedPreferences(mode).getLong(PREF_LAST_UPDATE, 0)

    fun setOnline(isOnline: Boolean, mode: Int) {
        edit(mode).putBoolean(PREF_ONLINE, isOnline).apply()
    }
    fun isOnline(mode: Int) = getDatabaseSharedPreferences(mode).getBoolean(PREF_ONLINE, true)
}