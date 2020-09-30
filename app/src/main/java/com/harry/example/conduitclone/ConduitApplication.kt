package com.harry.example.conduitclone

import android.app.Application

import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import kotlin.properties.Delegates

class ConduitApplication : Application() {
    //private var retainedObjectCount by Delegates.notNull<Int>()
    override fun onCreate() {
        super.onCreate()
//        val objectWatcher: ObjectWatcher = AppWatcher.objectWatcher
//        retainedObjectCount = objectWatcher.retainedObjectCount
        startKoin {
            androidLogger()
            androidContext(this@ConduitApplication)
            koin.loadModules(arrayListOf(viewModelModules, networkModules))
        }
    }
}