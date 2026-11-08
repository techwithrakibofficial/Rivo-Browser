package com.rivo.browser

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class BrowserViewModel(application: Application) : AndroidViewModel(application) {
    val browser = BrowserController(application.applicationContext)

    override fun onCleared() {
        browser.dispose()
    }
}
