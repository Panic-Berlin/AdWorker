package com.star.lite.junk.adworker

import android.app.Application
import com.star.lite.junk.adworkerlib.AdWorker

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        AdWorker().setApp(this)
    }
}
