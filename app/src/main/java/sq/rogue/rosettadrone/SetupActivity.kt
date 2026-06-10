package sq.rogue.rosettadrone

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri

class SetupActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_EDIT_MODE = "edit_mode"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val editMode = intent.getBooleanExtra(EXTRA_EDIT_MODE, false)

        // If keys are already saved and we're not in edit mode, skip to ConnectionActivity
        if (KeyStore.hasKeys(this) && !editMode) {
            goToConnectionActivity()
            return
        }

        setContentView(R.layout.activity_setup)

        val inputDjiKey = findViewById<EditText>(R.id.input_dji_key)
        val inputGoogleKey = findViewById<EditText>(R.id.input_google_key)
        val btnSave = findViewById<Button>(R.id.btn_save)
        val textError = findViewById<TextView>(R.id.text_error)

        // Pre-fill existing keys if in edit mode
        if (editMode) {
            KeyStore.getDjiKey(this)?.let { inputDjiKey.setText(it) }
            KeyStore.getGoogleKey(this)?.let { inputGoogleKey.setText(it) }
        }

        // Open DJI developer portal in browser
        findViewById<TextView>(R.id.btn_open_dji).setOnClickListener {
            openUrl("https://developer.dji.com/user/apps")
        }

        // Open Google Cloud Console in browser
        findViewById<TextView>(R.id.btn_open_google).setOnClickListener {
            openUrl("https://console.cloud.google.com/apis/credentials")
        }

        btnSave.setOnClickListener {
            val djiKey = inputDjiKey.text.toString().trim()
            val googleKey = inputGoogleKey.text.toString().trim()

            if (djiKey.isBlank()) {
                showError(textError, "Please enter your DJI API key.")
                return@setOnClickListener
            }
            if (googleKey.isBlank()) {
                showError(textError, "Please enter your Google Maps key.")
                return@setOnClickListener
            }

            KeyStore.saveDjiKey(this, djiKey)
            KeyStore.saveGoogleKey(this, googleKey)

            // Apply keys and trigger re-registration immediately
            (application as RDApplication).reinjectKeys()
            RDApplication.startLoginApplication()

            if (editMode) {
                // Return to ConnectionActivity without adding to back stack
                finish()
            } else {
                goToConnectionActivity()
            }
        }
    }

    private fun showError(textError: TextView, message: String) {
        textError.text = message
        textError.visibility = View.VISIBLE
    }

    private fun openUrl(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
    }

    private fun goToConnectionActivity() {
        startActivity(Intent(this, ConnectionActivity::class.java))
        finish()
    }

    override fun registerReceiver(receiver: BroadcastReceiver?, filter: IntentFilter?): Intent? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            super.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            super.registerReceiver(receiver, filter)
        }
    }

    override fun registerReceiver(
        receiver: BroadcastReceiver?,
        filter: IntentFilter?,
        flags: Int
    ): Intent? {
        var updatedFlags = flags
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (updatedFlags and (Context.RECEIVER_EXPORTED or Context.RECEIVER_NOT_EXPORTED) == 0) {
                updatedFlags = updatedFlags or Context.RECEIVER_NOT_EXPORTED
            }
        }
        return super.registerReceiver(receiver, filter, updatedFlags)
    }
}
