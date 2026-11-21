package com.paisapilot11819

import android.app.Application
import com.paisapilot11819.data.local.PaisaPilotDatabase

class PaisaPilotApp : Application() {

    val database: PaisaPilotDatabase by lazy {
        PaisaPilotDatabase.getDatabase(this)
    }

    companion object {
        lateinit var instance: PaisaPilotApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
