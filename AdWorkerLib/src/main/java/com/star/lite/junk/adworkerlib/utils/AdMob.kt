package com.star.lite.junk.adworkerlib.utils

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.gms.ads.*
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import java.util.*

class AdMob {
    val TAG = "AdMob"
    var bannerIsVisible: Boolean = false
    private var appOpenAd: AppOpenAd? = null
    var mInterstitialAd: InterstitialAd? = null
    var isOpenAdsError = false
    private var loadCallback: AppOpenAd.AppOpenAdLoadCallback? = null
    private var loadTime: Long = 0
    var isInterError = false
    private val _isShowingAd = MutableLiveData(false)
    val isShowingAd get() = _isShowingAd.asLiveData()

    fun startAdmobWorker(context: Context) {
        fetchOpenAd(context)
        loadInter(context)
    }

    private val isOpenAdAvailable: Boolean
        get() = appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)

    /** Creates and returns ad request.  */
    private val adRequest: AdRequest
        get() = AdRequest.Builder().build()
    val isInterReady: Boolean
        get() = mInterstitialAd != null

    fun showAdmobInter(activity: Activity) {
        if (mInterstitialAd != null) {
            _isShowingAd.value = true
            Log.wtf(TAG, "Start showing inter")
            setInterCallback(activity)
            mInterstitialAd!!.show(activity)
        } else {
            Log.d(TAG, "The interstitial ad wasn't ready yet.")
        }
    }

    fun isAdmobInterShowReady(): Boolean{
        return !isInterError && mInterstitialAd == null
    }

    /** Utility method to check if ad was loaded more than n hours ago.  */
    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
        val dateDifference = Date().time - loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * numHours
    }

    fun fetchOpenAd(context: Context) {
        Log.wtf(TAG, "Fetching Open Ad")
        isOpenAdsError = false
        // Have unused ad, no need to fetch another.
        if (isOpenAdAvailable) {
            return
        }
        loadCallback = object : AppOpenAd.AppOpenAdLoadCallback() {
            /**
             * Called when an app open ad has loaded.
             *
             * @param ad the loaded app open ad.
             */
            override fun onAdLoaded(ad: AppOpenAd) {
                appOpenAd = ad
                loadTime = Date().time
            }

            /**
             * Called when an app open ad has failed to load.
             *
             * @param loadAdError the error.
             */
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                // Handle the error.
                isOpenAdsError = true
            }
        }
        val request = adRequest
        AppOpenAd.load(
            context, APP_OPEN_AD_UNIT_ID, request,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback as AppOpenAd.AppOpenAdLoadCallback
        )
    }

    private fun setInterCallback(context: Context) {
        mInterstitialAd!!.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                // Called when fullscreen content is dismissed.
                _isShowingAd.value = false
                Log.d("TAG", "The ad was dismissed.")
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                // Called when fullscreen content failed to show.
                Log.d("TAG", "The ad failed to show.")
                isInterError = true
            }

            override fun onAdShowedFullScreenContent() {
                // Called when fullscreen content is shown.
                // Make sure to set your reference to null so you don't
                // show it a second time.
                mInterstitialAd = null
                _isShowingAd.value = true
                loadInter(context)
                Log.d("TAG", "The ad was shown.")
                Log.d("TAG", "The ad was shown.")
            }
        }
    }

    fun loadInter(context: Context) {
        isInterError = false
        Log.wtf(TAG, "Start loading inter")
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(context, INTER_AD_UNIT_ID!!, adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    // The mInterstitialAd reference will be null until
                    // an ad is loaded.
                    mInterstitialAd = interstitialAd
                    Log.i(TAG, "onAdLoaded")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    // Handle the error
                    Log.i(TAG, loadAdError.message)
                    mInterstitialAd = null
                    isInterError = true
                    //loadIronInterstitial()
                }
            }
        )
    }

    /** LifecycleObserver methods  */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart(activity: Activity) {
        showOpenAdIfAvailable(activity, activity)
        Log.d(TAG, "onStart")
    }

    /** Shows the ad if one isn't already showing.  */
    private fun showOpenAdIfAvailable(context: Context, activity: Activity) {
        // Only show ad if there is not already an app open ad currently showing
        // and an ad is available.
        if (!isShowingAd.value!! && isOpenAdAvailable) {
            Log.d(TAG, "Will show ad.")
            val fullScreenContentCallback: FullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        // Set the reference to null so isAdAvailable() returns false.
                        _isShowingAd.value = false
                        appOpenAd = null
                        fetchOpenAd(context)
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {}
                    override fun onAdShowedFullScreenContent() {
                        _isShowingAd.value = true
                    }
                }
            appOpenAd!!.fullScreenContentCallback = fullScreenContentCallback
            appOpenAd!!.show(activity)
        } else {
            Log.d(TAG, "Open Ad is not Available")
        }
    }


    fun loadAdmobBannerIntoContainer(activity: Activity, adContainerView: FrameLayout, context: Context) {
        adContainerView.visibility = View.INVISIBLE
        val mAdView = AdView(context)
        mAdView.adUnitId = BANNER_AD_UNIT_ID
        adContainerView.addView(mAdView)
        val adRequest = AdRequest.Builder().build()
        val adSize = size(activity, adContainerView, context)
        mAdView.adSize = adSize
        mAdView.loadAd(adRequest)
        mAdView.adListener = object : AdListener() {
            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)

            }
            override fun onAdLoaded() {
                super.onAdLoaded()
                adContainerView.visibility = View.VISIBLE
                bannerIsVisible = true
            }

            override fun onAdImpression() {
                super.onAdImpression()
            }
        }
    }

    private fun size(activity: Activity, adContainerView: FrameLayout, context: Context): AdSize {
        val display = activity.windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)

        val density = outMetrics.density

        var adWidthPixels = adContainerView.width.toFloat()
        if (adWidthPixels == 0f) {
            adWidthPixels = outMetrics.widthPixels.toFloat()
        }

        val adWidth = (adWidthPixels / density).toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth)
    }
}

const val BANNER_AD_UNIT_ID: String = "ca-app-pub-3940256099942544/6300978111"
const val APP_OPEN_AD_UNIT_ID: String = "ca-app-pub-3940256099942544/3419835294"
const val INTER_AD_UNIT_ID: String = "ca-app-pub-3940256099942544/1033173712"
