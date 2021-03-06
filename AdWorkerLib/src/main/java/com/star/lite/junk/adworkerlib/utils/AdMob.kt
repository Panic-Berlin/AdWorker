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
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.star.lite.junk.adworkerlib.utils.ConstantsAds.Companion.APP_OPEN_AD_UNIT_ID
import com.star.lite.junk.adworkerlib.utils.ConstantsAds.Companion.BANNER_AD_UNIT_ID
import com.star.lite.junk.adworkerlib.utils.ConstantsAds.Companion.EVENT_NAME_BANNER_IMPERSSION
import com.star.lite.junk.adworkerlib.utils.ConstantsAds.Companion.EVENT_NAME_INTER_IMPERSSION
import com.star.lite.junk.adworkerlib.utils.ConstantsAds.Companion.EVENT_NAME_OPEN_AD_IMPERSSION
import com.star.lite.junk.adworkerlib.utils.ConstantsAds.Companion.EVENT_PARAM_AD_SOURCE
import com.star.lite.junk.adworkerlib.utils.ConstantsAds.Companion.INTER_AD_UNIT_ID
import com.star.lite.junk.adworkerlib.utils.ConstantsAds.Companion.isBannerVisible
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

    fun startAdmobWorker(context: Context, firebaseAnalytics: FirebaseAnalytics, pageName: String) {
        fetchOpenAd(context)
        loadInter(context, firebaseAnalytics, pageName)
    }

    private val isOpenAdAvailable: Boolean
        get() = appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)

    /** Creates and returns ad request.  */
    private val adRequest: AdRequest
        get() = AdRequest.Builder().build()
    val isInterReady: Boolean
        get() = mInterstitialAd != null

    fun showAdmobInter(activity: Activity, firebaseAnalytics: FirebaseAnalytics, pageName: String) {
        if (mInterstitialAd != null) {
            _isShowingAd.value = true
            Log.wtf(TAG, "Start showing inter")
            setInterCallback(activity, firebaseAnalytics, pageName)
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

    private fun setInterCallback(context: Context, firebaseAnalytics: FirebaseAnalytics, pageName: String) {
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
                loadInter(context, firebaseAnalytics, pageName)
                firebaseAnalytics.logEvent(EVENT_NAME_INTER_IMPERSSION) {
                    param(EVENT_PARAM_AD_SOURCE, pageName)
                }
                Log.d("TAG", "The ad was shown.")
                Log.d("TAG", "The ad was shown.")
            }
        }
    }

    fun loadInter(context: Context, firebaseAnalytics: FirebaseAnalytics, pageName: String) {
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
                    IronSourceAds().loadIronInterstitial(firebaseAnalytics, pageName)
                }
            }
        )
    }

    /** LifecycleObserver methods  */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart(activity: Activity, firebaseAnalytics: FirebaseAnalytics, pageName: String) {
        showOpenAdIfAvailable(activity, activity, firebaseAnalytics, pageName)
        Log.d(TAG, "onStart")
    }

    /** Shows the ad if one isn't already showing.  */
    private fun showOpenAdIfAvailable(context: Context, activity: Activity, firebaseAnalytics: FirebaseAnalytics, pageName: String) {
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
                        firebaseAnalytics.logEvent(EVENT_NAME_OPEN_AD_IMPERSSION) {
                            param(EVENT_PARAM_AD_SOURCE, pageName)
                        }
                    }
                }
            appOpenAd!!.fullScreenContentCallback = fullScreenContentCallback
            appOpenAd!!.show(activity)
        } else {
            Log.d(TAG, "Open Ad is not Available")
        }
    }


    fun loadAdmobBannerIntoContainer(activity: Activity, adContainerView: FrameLayout, context: Context, firebaseAnalytics: FirebaseAnalytics, pageName: String) {
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
                isBannerVisible = true
            }

            override fun onAdImpression() {
                super.onAdImpression()
                firebaseAnalytics.logEvent(EVENT_NAME_BANNER_IMPERSSION) {
                    param(EVENT_PARAM_AD_SOURCE, pageName)
                }
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
