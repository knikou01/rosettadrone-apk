package sq.rogue.rosettadrone

import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object KeyStore {

    private const val PREFS_FILE = "secure_keys"
    private const val KEY_DJI = "dji_api_key"
    private const val KEY_MAPS = "google_maps_key"

    // Plain prefs used only for early DJI key injection in attachBaseContext
    private const val PLAIN_PREFS_FILE = "bootstrap_keys"
    private const val PLAIN_KEY_DJI = "dji_api_key_plain"

    private fun getPrefs(context: Context) =
        EncryptedSharedPreferences.create(
            context,
            PREFS_FILE,
            MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

    private fun getPlainPrefs(context: Context) =
        context.getSharedPreferences(PLAIN_PREFS_FILE, Context.MODE_PRIVATE)

    fun saveDjiKey(context: Context, key: String) {
        getPrefs(context).edit { putString(KEY_DJI, key.trim()) }
        // Also save in plain prefs for early injection
        getPlainPrefs(context).edit { putString(PLAIN_KEY_DJI, key.trim()) }
    }

    fun saveGoogleKey(context: Context, key: String) {
        getPrefs(context).edit { putString(KEY_MAPS, key.trim()) }
    }

    fun getDjiKey(context: Context): String? {
        return getPrefs(context).getString(KEY_DJI, null)
    }

    // Used during attachBaseContext before encryption is available
    fun getDjiKeyEarly(context: Context): String? {
        return context.getSharedPreferences(PLAIN_PREFS_FILE, Context.MODE_PRIVATE)
            .getString(PLAIN_KEY_DJI, null)
    }

    fun getGoogleKey(context: Context): String? {
        return getPrefs(context).getString(KEY_MAPS, null)
    }

    fun hasKeys(context: Context): Boolean {
        val dji = getDjiKey(context)
        val maps = getGoogleKey(context)
        return !dji.isNullOrBlank() && !maps.isNullOrBlank()
    }

    fun clearKeys(context: Context) {
        getPrefs(context).edit { clear() }
        getPlainPrefs(context).edit { clear() }
    }
}