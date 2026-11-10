package com.rivo.browser

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.rivo.browser.engine.gecko.GeckoBrowserTabFactory

class BrowserViewModel(application: Application) : AndroidViewModel(application) {
    val browser = BrowserController(
        application.applicationContext,
        GeckoBrowserTabFactory(application.applicationContext)
    )

    override fun onCleared() {
        browser.dispose()
    }
}
