package com.rivo.browser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels

class MainActivity : ComponentActivity() {
    private val browserViewModel: BrowserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BrowserScreen(browserViewModel)
        }
    }

    override fun onStart() {
        super.onStart()
        browserViewModel.browser.onHostResumed()
    }

    override fun onStop() {
        browserViewModel.browser.onHostPaused()
        super.onStop()
    }
}
