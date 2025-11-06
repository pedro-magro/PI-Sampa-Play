package com.example.myapplication.util

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.myapplication.data.LoginResponse

object SessionManager {
    private const val PREF_NAME = "SampaPlaySession"
    private const val KEY_USER_ID = "USER_ID"
    private const val KEY_USER_NOME = "USER_NOME"
    private const val KEY_USER_EMAIL = "USER_EMAIL"
    private const val KEY_USER_TIPO = "USER_TIPO"
    private const val KEY_USER_ZONA_ID = "USER_ZONA_ID"
    private const val KEY_USER_ZONA_NOME = "USER_ZONA_NOME"

    // Função interna para pegar as prefs seguras
    private fun getEncryptedPrefs(context: Context): SharedPreferences {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        return EncryptedSharedPreferences.create(
            PREF_NAME,
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    // Salva todos os dados do usuário (seu código original)
    fun salvarSessao(context: Context, usuario: LoginResponse) {
        val editor = getEncryptedPrefs(context).edit()
        editor.putInt(KEY_USER_ID, usuario.usuarioId)
        editor.putString(KEY_USER_NOME, usuario.usuarioNome)
        editor.putString(KEY_USER_EMAIL, usuario.usuarioEmail)
        editor.putString(KEY_USER_TIPO, usuario.usuarioTipo)
        editor.putInt(KEY_USER_ZONA_ID, usuario.zonaId ?: 0)
        editor.putString(KEY_USER_ZONA_NOME, usuario.zonaNome)
        editor.apply()
    }

    // --- FUNÇÕES ADICIONADAS ---

    // Recupera o nome do usuário para o Perfil
    fun getUserNome(context: Context): String? {
        return getEncryptedPrefs(context).getString(KEY_USER_NOME, null)
    }

    // Recupera o email do usuário para o Perfil
    fun getUserEmail(context: Context): String? {
        return getEncryptedPrefs(context).getString(KEY_USER_EMAIL, null)
    }

    // Recupera o tipo de usuário
    fun getUserTipo(context: Context): String? {
        return getEncryptedPrefs(context).getString(KEY_USER_TIPO, null)
    }

    // Recupera o ID do usuário
    fun getUserId(context: Context): Int {
        return getEncryptedPrefs(context).getInt(KEY_USER_ID, 0)
    }

    fun getUserZonaId(context: Context): Int? {
        return getEncryptedPrefs(context).getInt(KEY_USER_ZONA_ID, 0)
    }

    // Função de Logout: Limpa TODOS os dados da sessão
    fun clearSession(context: Context) {
        val editor = getEncryptedPrefs(context).edit()
        editor.clear()
        editor.apply()
    }
}