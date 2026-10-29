package eu.kanade.tachiyomi.ui.setting.track

import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import eu.kanade.tachiyomi.data.sync.service.GoogleDriveService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mihon.app.di.appGraph
import tachiyomi.core.common.i18n.stringResource
import tachiyomi.i18n.sy.SYMR

/**
 * SY-specific file
 */
class GoogleDriveLoginActivity : BaseOAuthLoginActivity() {
    // BaseOAuthLoginActivity's member injection only covers its own fields, so subclass fields
    // would stay uninitialized; take the service from the graph directly instead.
    private val googleDriveService: GoogleDriveService
        get() = appGraph.googleDriveService

    override fun handleResult(uri: Uri) {
        val code = uri.getQueryParameter("code")
        val error = uri.getQueryParameter("error")
        if (code != null) {
            lifecycleScope.launch(Dispatchers.IO) {
                googleDriveService.handleAuthorizationCode(
                    code,
                    this@GoogleDriveLoginActivity,
                    onSuccess = {
                        Toast.makeText(
                            this@GoogleDriveLoginActivity,
                            stringResource(SYMR.strings.google_drive_login_success),
                            Toast.LENGTH_LONG,
                        ).show()

                        returnToSettings()
                    },
                    onFailure = { error ->
                        Toast.makeText(
                            this@GoogleDriveLoginActivity,
                            stringResource(SYMR.strings.google_drive_login_failed, error),
                            Toast.LENGTH_LONG,
                        ).show()
                        returnToSettings()
                    },
                )
            }
        } else if (error != null) {
            Toast.makeText(
                this@GoogleDriveLoginActivity,
                stringResource(SYMR.strings.google_drive_login_failed, error),
                Toast.LENGTH_LONG,
            ).show()

            returnToSettings()
        } else {
            returnToSettings()
        }
    }
}
