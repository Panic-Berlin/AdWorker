package com.star.lite.junk.adworker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.star.lite.junk.adworker.databinding.ActivityMainBinding
import com.star.lite.junk.adworkerlib.AdWorker
import com.star.lite.junk.adworkerlib.utils.ConstantsAds

class MainActivity : AppCompatActivity() {

    private val viewBinding: ActivityMainBinding by viewBinding(ActivityMainBinding::bind)
    private val pageName = "MainActivity"
    var YANDEX: String = "iLjOmXcXo1XqjgHu"
    var ADMOB: String = "kTzKDWTUTq1W3Bdq"
    var region = "iLjOmXcXo1XqjgHu"
    var APP_KEY = "157edbc35"
    var BASE_URL = "https://atrack.us/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this)
        val firebaseAnalytics = Firebase.analytics
        setConstants()
        ads(firebaseAnalytics)
        initViews(firebaseAnalytics)
    }

    private fun setConstants(){
        ConstantsAds.APP_KEY = APP_KEY
        ConstantsAds.YANDEX = YANDEX
        ConstantsAds.ADMOB = ADMOB
        ConstantsAds.BASE_URL = BASE_URL
        ConstantsAds.region = region
    }

    private fun ads(firebaseAnalytics: FirebaseAnalytics) {
        AdWorker().getRegion()
        AdWorker().initialize(this, firebaseAnalytics, pageName)
        AdWorker().loadBanner(
            viewBinding.banner,
            this,
            viewBinding.adViewContainer,
            firebaseAnalytics, pageName
        )
    }

    private fun initViews(firebaseAnalytics: FirebaseAnalytics) {
        viewBinding.btnShowInter.setOnClickListener {
            AdWorker().showInter(this, firebaseAnalytics, pageName)
        }
    }

    override fun onResume() {
        super.onResume()
        AdWorker().onResume(this)
    }

    override fun onPause() {
        super.onPause()
        AdWorker().onPause(this)
    }
}
