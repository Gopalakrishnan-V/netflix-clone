package com.netflixclone

import com.facebook.stetho.Stetho

class DebugApplication : MyApplication() {
    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
    }
}
