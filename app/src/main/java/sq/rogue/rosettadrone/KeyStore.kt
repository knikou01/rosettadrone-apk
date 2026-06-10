package sq.rogue.rosettadrone

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.core.content.edit

object KeyStore {

    private const val PREFS_FILE = "secure_keys"
    private const val KEY_DJI = "dji_api_key"
    private const val KEY_MAPS = "google_maps_key"

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

    fun saveDjiKey(context: Context, key: String) {
        getPrefs(context).edit { putString(KEY_DJI, key.trim()) }
    }

    fun saveGoogleKey(context: Context, key: String) {
        getPrefs(context).edit { putString(KEY_MAPS, key.trim()) }
    }

    fun getDjiKey(context: Context): String? {
        return getPrefs(context).getString(KEY_DJI, null)
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
    }
}