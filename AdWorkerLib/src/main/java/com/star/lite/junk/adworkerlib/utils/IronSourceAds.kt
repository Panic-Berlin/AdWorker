package com.star.lite.junk.adworkerlib.utils

import android.app.Activity
import android.util.Log
import android.widget.FrameLayout
import com.ironsource.mediationsdk.ISBannerSize
import com.ironsource.mediationsdk.IronSource
import com.ironsource.mediationsdk.logger.IronSourceError
import com.ironsource.mediationsdk.sdk.BannerListener
import com.ironsource.mediationsdk.sdk.InterstitialListener

class IronSourceAds {

    private val TAG = "IronSource"

    fun startIronSource(){
        loadIronInterstitial()
    }

    var isIronInterLoad = false
    private fun loadIronInterstitial() {
        IronSource.setInterstitialListener(object : InterstitialListener {
            override fun onInterstitialAdReady() {
                isIronInterLoad = true
                Log.d(TAG, "onInterstitialAdReady: load")
            }

            override fun onInterstitialAdLoadFailed(p0: IronSourceError?) {
                isIronInterLoad = false
                Log.d(TAG, "onInterstitialAdLoadFailed: $p0")
            }

            override fun onInterstitialAdOpened() {
                Log.d(TAG, "onInterstitialAdOpened: ad open")
            }

            override fun onInterstitialAdClosed() {
                IronSource.loadInterstitial()
            }

            override fun onInterstitialAdShowSucceeded() {
                Log.d(TAG, "onInterstitialAdShowSucceeded: show success")
            }

            override fun onInterstitialAdShowFailed(p0: IronSourceError?) {
                Log.d(TAG, "onInterstitialAdShowFailed: $p0")
            }

            override fun onInterstitialAdClicked() {
                Log.d(TAG, "onInterstitialAdClicked: clicked")
            }

        })
        IronSource.loadInterstitial()
    }

    fun showIronInter(){
        IronSource.showInterstitial()
    }

    fun loadIronBanner(mainActivity: Activity, adContainerView: FrameLayout) {
        val banner = IronSource.createBanner(mainActivity, ISBannerSize.BANNER)
        adContainerView.addView(banner)
        banner.bannerListener = object : BannerListener {
            override fun onBannerAdLoaded() {
                // Called after a banner ad has been successfully loaded
                Log.d(TAG, "onBannerAdLoaded: bannerLoad")
            }

            override fun onBannerAdLoadFailed(error: IronSourceError) {
                // Called after a banner has attempted to load an ad but failed.
                Log.d(TAG, "onBannerAdLoadFailed: $error")
            }

            override fun onBannerAdClicked() {
                // Called after a banner has been clicked.
            }

            override fun onBannerAdScreenPresented() {
                // Called when a banner is about to present a full screen content.
            }

            override fun onBannerAdScreenDismissed() {
                // Called after a full screen content has been dismissed
            }

            override fun onBannerAdLeftApplication() {
                // Called when a user would be taken out of the application context.
            }
        }
        IronSource.loadBanner(banner)
    }

    fun onPause(activity: Activity){
        IronSource.onPause(activity)
    }

    fun onResume(activity: Activity){
        IronSource.onResume(activity)
    }
}
