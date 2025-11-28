package com.example.myapplication.CloudinaryApplication

import android.app.Application
import com.cloudinary.android.MediaManager
class MyApplication : Application() {

    override fun onCreate(){
        super.onCreate()
        val config = HashMap<String, Any>()
        config["cloud_name"] = "ddw0nbift"
        config["secure"] = true

        MediaManager.init(this, config)
    }


}