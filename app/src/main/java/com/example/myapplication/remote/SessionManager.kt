package com.example.myapplication.remote

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.security.crypto.MasterKeys
import com.example.myapplication.data.UsuarioResponse

class SessionManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "sampa_play_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private const val TOKEN = "token"
        private const val USER_ID = "user_id"
        private const val USER_NAME = "user_name"
        private const val USER_EMAIL = "user_email"
        private const val USER_ROLE = "user_role"
        private const val USER_ZONA = "user_zona"
    }

    fun saveToken(token:String){
        prefs.edit().putString(TOKEN, token).apply()
    }


    fun saveUser(user: UsuarioResponse){
        val editor = prefs.edit()
        editor.putInt(USER_ID, user.id)
        editor.putString(USER_NAME, user.nome)
        editor.putString(USER_EMAIL, user.email)
        editor.putString(USER_ROLE, user.tipo)

        if (user.zonaId != null) {
            editor.putInt(USER_ZONA, user.zonaId?: -1)
        }
        else {
            editor.remove(USER_ZONA)
        }

        editor.apply()
    }


    fun clearSession(){
        prefs.edit().clear().apply();
    }

    fun isLoggedIn(): Boolean = getToken() != null

    fun getToken(): String? = prefs.getString(TOKEN, null)
    fun getUserId(): Int = prefs.getInt(USER_ID, 0)
    fun getUserName(): String? = prefs.getString(USER_NAME, null)
    fun getUserEmail(): String? = prefs.getString(USER_EMAIL, null)
    fun getUserZonaId(): Int = prefs.getInt(USER_ZONA, 0)
    fun isAdmin(): Boolean = prefs.getString(USER_ROLE, "COMUM") == "ADMIN"

}