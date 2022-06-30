package com.star.lite.junk.adworker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import by.kirich1409.viewbindingdelegate.viewBinding
import com.star.lite.junk.adworker.databinding.ActivityMainBinding
import com.star.lite.junk.adworkerlib.AdWorker

class MainActivity : AppCompatActivity() {

    private val viewBinding: ActivityMainBinding by viewBinding(ActivityMainBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        adWorkerInitialize()
        initViews()
    }

    private fun initViews() {
        viewBinding.btnShowInter.setOnClickListener {
            AdWorker().showInter()
        }
    }

    private fun adWorkerInitialize() {
        AdWorker().initialize(
            this,
            this,
            "yandex"
        )
        AdWorker().setRegion("yandex", "admob")
        AdWorker().startAdWorker(this)
        AdWorker().loadBannerIntoContainer(viewBinding.banner, this, viewBinding.adViewContainer)
    }

    override fun onStop() {
        super.onStop()
        AdWorker().isBannerVisible = false
    }

    override fun onDestroy() {
        super.onDestroy()
        AdWorker().isBannerVisible = false
    }
}
