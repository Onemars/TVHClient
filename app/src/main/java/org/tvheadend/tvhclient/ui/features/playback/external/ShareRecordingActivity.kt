package org.tvheadend.tvhclient.ui.features.playback.external

import android.content.Intent
import timber.log.Timber

class ShareRecordingActivity : BasePlaybackActivity() {

    override fun onTicketReceived() {
        val url = viewModel.getPlaybackUrl()
        Timber.d("Sharing recording from server with url $url")

        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, url)
            type = "text/plain"
        }

        Timber.d("Showing share sheet")
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
        finish()
    }
}
