package com.star.lite.junk.adworkerlib.utils

import android.app.Activity
import android.util.Log
import android.view.View
import com.yandex.mobile.ads.banner.BannerAdEventListener
import com.yandex.mobile.ads.banner.BannerAdView
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.interstitial.InterstitialAdEventListener

class YandexAds(activity: Activity) {

    //Yandex
    private val TAG = "YandexAds"
    private var YANDEX_BANNER_BLOCK_ID = "adf-279013/966631"
    private var YANDEX_INTER_BLOCK_ID = "adf-279013/966533"
    var yandexBannerIsVisible = false
    var isYandexInterstitialLoaded = false

    private var yandexInterstitialAd = com.yandex.mobile.ads.interstitial.InterstitialAd(activity)

    fun startYandexAdWorker() {
        loadYandexInterstitial()
    }

    /**
     * Загрузка рекламы в баннере
     */
    fun loadYandexBannerIntoContainer(bannerAdView: BannerAdView) {
        bannerAdView.visibility = View.INVISIBLE
        bannerAdView.setAdUnitId(YANDEX_BANNER_BLOCK_ID)
        bannerAdView.setAdSize(com.yandex.mobile.ads.banner.AdSize.BANNER_320x50) // Banner size. Default 320x50
        bannerAdView.setBannerAdEventListener(object : BannerAdEventListener {
            override fun onAdLoaded() {
                bannerAdView.visibility = View.VISIBLE
                yandexBannerIsVisible = true
                Log.d(TAG, "onAdLoadedBanner: load")
            }

            override fun onAdFailedToLoad(p0: AdRequestError) {
                Log.d(TAG, "onAdFailedToLoadBanner: $p0")
            }

            override fun onAdClicked() {
                Log.d(TAG, "onAdClickedBanner: click")
            }

            override fun onLeftApplication() {
                Log.d(TAG, "onLeftApplicationBanner: Left app")
            }

            override fun onReturnedToApplication() {
                Log.d(TAG, "onReturnedToApplicationBanner: Return to app")
            }

            override fun onImpression(p0: ImpressionData?) {
                Log.d(TAG, "onImpressionBanner: $p0")
            }

        })
        val adRequest = com.yandex.mobile.ads.common.AdRequest.Builder().build()
        bannerAdView.loadAd(adRequest)
    }

    /**
     * Загрузка полноэкранной рекламы
     */
    private fun loadYandexInterstitial() {
        yandexInterstitialAd!!.setAdUnitId(YANDEX_INTER_BLOCK_ID)
        val adRequest = com.yandex.mobile.ads.common.AdRequest.Builder().build()
        yandexInterstitialAd!!.setInterstitialAdEventListener(object : InterstitialAdEventListener {
            override fun onAdLoaded() {
                isYandexInterstitialLoaded = true
                Log.d(TAG, "onInterAdLoaded: yandex ad load")
            }

            override fun onAdFailedToLoad(p0: AdRequestError) {
                isYandexInterstitialLoaded = false
                Log.d(TAG, "onInterAdFailedToLoad: $p0")
            }

            override fun onAdShown() {
                Log.d(TAG, "onrAdShownInterstitial: Ad show")
            }

            override fun onAdDismissed() {
                yandexInterstitialAd.loadAd(adRequest)
                Log.d(TAG, "onAdDismissedInterstitial: dismiss")
            }

            override fun onAdClicked() {
                Log.d(TAG, "onAdClickedInterstitial: click")
            }

            override fun onLeftApplication() {
                Log.d(TAG, "onLeftApplicationInterstitial: Left app")
            }

            override fun onReturnedToApplication() {
                Log.d(TAG, "onReturnedToApplicationInterstitial: Return to app")
            }

            override fun onImpression(p0: ImpressionData?) {
                Log.d(TAG, "onImpression: $p0")
            }

        })
        yandexInterstitialAd.loadAd(adRequest)
    }

    /**
     * Демонстрация полнжкранной рекламы
     */
    fun showYandexInter() {
        yandexInterstitialAd.show()
    }
}
