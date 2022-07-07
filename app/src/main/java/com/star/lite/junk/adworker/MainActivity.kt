package com.star.lite.junk.adworker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import by.kirich1409.viewbindingdelegate.viewBinding
import com.star.lite.junk.adworker.databinding.ActivityMainBinding
import com.star.lite.junk.adworkerlib.AdWorker

class MainActivity : AppCompatActivity() {

    private val viewBinding: ActivityMainBinding by viewBinding(ActivityMainBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ads()
        initViews()
    }

    private fun ads() {
        AdWorker().getRegion()
        AdWorker().initialize(this)
        AdWorker().loadBanner(
            viewBinding.banner,
            this,
            viewBinding.adViewContainer,
        )
    }

    private fun initViews() {
        viewBinding.btnShowInter.setOnClickListener {
            AdWorker().showInter(this)
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
