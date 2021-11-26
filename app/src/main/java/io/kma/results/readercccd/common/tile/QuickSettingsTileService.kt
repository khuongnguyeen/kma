package io.kma.results.readercccd.common.tile

import android.content.Intent
import android.os.Build
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import io.kma.results.readercccd.ui.activities.QRTabsActivity

@RequiresApi(api = Build.VERSION_CODES.N)
class QuickSettingsTileService : TileService() {

    override fun onClick() {
        super.onClick()
        val intent = Intent(applicationContext, QRTabsActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivityAndCollapse(intent)
    }
}