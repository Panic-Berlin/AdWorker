package com.star.lite.junk.adworkerlib

import android.app.Activity
import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.widget.FrameLayout
import androidx.annotation.NonNull
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.*
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.star.lite.junk.adworkerlib.utils.asLiveData
import com.yandex.mobile.ads.banner.BannerAdEventListener
import com.yandex.mobile.ads.banner.BannerAdView
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.common.MobileAds
import com.yandex.mobile.ads.interstitial.InterstitialAdEventListener
import java.util.*

class AdWorker: LifecycleObserver,
    Application.ActivityLifecycleCallbacks {

    private var APP_OPEN_AD_UNIT_ID: String? = null
    private var INTER_AD_UNIT_ID: String? = null
    private var BANNER_AD_UNIT_ID: String? = null
    private var appOpenAd: AppOpenAd? = null
    private var currentActivity: Activity? = null
    private var loadTime: Long = 0
    var isOpenAdsError = false
    var isInterError = false
    private val _isShowingAd = MutableLiveData(false)
    val isShowingAd get() = _isShowingAd.asLiveData()
    var mInterstitialAd: InterstitialAd? = null
    var bannerIsVisible = false
    private var loadCallback: AppOpenAd.AppOpenAdLoadCallback? = null
    private var nativeAdLoader: AdLoader? = null
    private var loadedNativeAd: NativeAd? = null
    private var firebaseAnalytics: FirebaseAnalytics? = null
    private var mainActivity: Activity? = null
    private var region: String? = null
    private var YANDEX: String? = null
    private var ADMOB: String? = null
    var isBannerVisible: Boolean = false
    private var APP_KEY: String? = null
    private var context: Context? = null
    var pageName: String? = null
    var USE_TEST_ADS: Boolean = true
    private var myApplication: Application? = null

    fun setApp(application: Application){
        this.myApplication = application
        application.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    fun setRegion(yandex: String, admob: String){
        this.YANDEX = yandex
        this.ADMOB = admob
    }

    fun initialize(
        @NonNull
        context: Context,
        @NonNull
        activity: Activity,
        @NonNull
        region: String
    ){
        this.mainActivity = activity
        this.region = region
        this.context = context
    }

    fun startAdWorker(mainActivity: Activity) {
        when (region) {
            YANDEX -> {
                MobileAds.initialize(mainActivity) {
                    startYandexAdWorker(mainActivity = mainActivity)

                }
            }
            ADMOB -> {
                com.google.android.gms.ads.MobileAds.initialize(mainActivity) {
                    startAdmobWorker(mainActivity = mainActivity)
                }
                //IronSource.init(mainActivity, APP_KEY)
            }
        }
    }

    fun loadBannerIntoContainer(
        bannerAdView: BannerAdView,
        activity: Activity,
        adContainerView: FrameLayout,
    ) {
        when (region) {
            YANDEX -> {
                Log.d(TAG, "loadBannerIntoContainer: before if")
                if (!yandexBannerIsVisible && !isBannerVisible) {
                    Log.d(TAG, "loadBannerIntoContainer: after if")
                    loadYandexBannerIntoContainer(bannerAdView)
                    isBannerVisible = true
                }
            }
            ADMOB -> {
                if (!bannerIsVisible) {
                    loadAdmobBannerIntoContainer(activity, adContainerView, context!!)
                }
            }
        }
    }

    fun showInter() {
        when (region) {
            YANDEX -> {
                showYandexInter()
            }
            ADMOB -> {
                showAdmobInter()
            }
        }
    }

    var isIronInterLoad = false
    /*private fun loadIronInterstitial() {
        IronSource.setInterstitialListener(object : InterstitialListener {
            override fun onInterstitialAdReady() {
                isIronInterLoad = true
                Log.d(ContentValues.TAG, "onInterstitialAdReady: load")
            }

            override fun onInterstitialAdLoadFailed(p0: IronSourceError?) {
                isIronInterLoad = false
                Log.d(ContentValues.TAG, "onInterstitialAdLoadFailed: $p0")
            }

            override fun onInterstitialAdOpened() {
                Log.d(ContentValues.TAG, "onInterstitialAdOpened: ad open")
            }

            override fun onInterstitialAdClosed() {
                IronSource.loadInterstitial()
            }

            override fun onInterstitialAdShowSucceeded() {
                Log.d(ContentValues.TAG, "onInterstitialAdShowSucceeded: show success")
            }

            override fun onInterstitialAdShowFailed(p0: IronSourceError?) {
                Log.d(ContentValues.TAG, "onInterstitialAdShowFailed: $p0")
            }

            override fun onInterstitialAdClicked() {
                Log.d(ContentValues.TAG, "onInterstitialAdClicked: clicked")
            }

        })
        IronSource.loadInterstitial()
    }

    fun showIronInter(){
        IronSource.showInterstitial()
    }

    private fun loadIronBanner(mainActivity: Activity, adContainerView: FrameLayout) {
        val banner = IronSource.createBanner(mainActivity, ISBannerSize.BANNER)
        adContainerView.addView(banner)
        banner.bannerListener = object : BannerListener {
            override fun onBannerAdLoaded() {
                // Called after a banner ad has been successfully loaded
                Log.d(ContentValues.TAG, "onBannerAdLoaded: bannerLoad")
            }

            override fun onBannerAdLoadFailed(error: IronSourceError) {
                // Called after a banner has attempted to load an ad but failed.
                Log.d(ContentValues.TAG, "onBannerAdLoadFailed: $error")
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
    }*/

    //Yandex
    private var YANDEX_BANNER_BLOCK_ID = "R-M-1622219-1"
    private var YANDEX_INTER_BLOCK_ID = "R-M-1622219-3"
    var yandexBannerIsVisible = false
    var isYandexInterstitialLoaded = false
    private var yandexInterstitialAd: com.yandex.mobile.ads.interstitial.InterstitialAd? = null

    /**
     * Запуск загрузки рекламы с вознагрождением
     * и полноэкранной рекламы
     */
    private fun startYandexAdWorker(mainActivity: Activity){
        FirebaseApp.initializeApp(mainActivity)
        firebaseAnalytics = Firebase.analytics
        yandexInterstitialAd = com.yandex.mobile.ads.interstitial.InterstitialAd(mainActivity)
        loadYandexInterstitial()
    }

    /**
     * Загрузка рекламы в баннере
     */
    private fun loadYandexBannerIntoContainer(bannerAdView: BannerAdView) {
        bannerAdView.visibility = View.INVISIBLE
        bannerAdView.setAdUnitId(YANDEX_BANNER_BLOCK_ID)
        bannerAdView.setAdSize(com.yandex.mobile.ads.banner.AdSize.BANNER_320x50) // Banner size. Default 320x50
        bannerAdView.setBannerAdEventListener(object : BannerAdEventListener {
            override fun onAdLoaded() {
                bannerAdView.visibility = View.VISIBLE
                yandexBannerIsVisible = true
                Log.d(ContentValues.TAG, "onAdLoadedBanner: load")
            }

            override fun onAdFailedToLoad(p0: AdRequestError) {
                Log.d(ContentValues.TAG, "onAdFailedToLoadBanner: $p0")
            }

            override fun onAdClicked() {
                Log.d(ContentValues.TAG, "onAdClickedBanner: click")
            }

            override fun onLeftApplication() {
                Log.d(ContentValues.TAG, "onLeftApplicationBanner: Left app")
            }

            override fun onReturnedToApplication() {
                Log.d(ContentValues.TAG, "onReturnedToApplicationBanner: Return to app")
            }

            override fun onImpression(p0: ImpressionData?) {
                Log.d(ContentValues.TAG, "onImpressionBanner: $p0")
                firebaseAnalytics!!.logEvent(Companion.EVENT_NAME_BANNER_IMPERSSION){
                    param(EVENT_PARAM_AD_SOURCE, pageName!!)
                }
            }

        })
        val adRequest = com.yandex.mobile.ads.common.AdRequest.Builder().build()
        bannerAdView.loadAd(adRequest)
    }

    /**
     * Загрузка полноэкранной рекламы
     */
    private fun loadYandexInterstitial(){
        yandexInterstitialAd!!.setAdUnitId(YANDEX_INTER_BLOCK_ID)
        val adRequest = com.yandex.mobile.ads.common.AdRequest.Builder().build()
        yandexInterstitialAd!!.setInterstitialAdEventListener(object : InterstitialAdEventListener {
            override fun onAdLoaded() {
                isYandexInterstitialLoaded = true
                Log.d(TAG, "onAdLoaded: yandex ad load")
            }

            override fun onAdFailedToLoad(p0: AdRequestError) {
                isYandexInterstitialLoaded = false
                Log.d(ContentValues.TAG, "onAdFailedToLoad: $p0")
            }

            override fun onAdShown() {
                Log.d(ContentValues.TAG, "onAdShownInterstitial: Ad show")
            }

            override fun onAdDismissed() {
                yandexInterstitialAd!!.loadAd(adRequest)
                Log.d(ContentValues.TAG, "onAdDismissedInterstitial: dismiss")
            }

            override fun onAdClicked() {
                Log.d(ContentValues.TAG, "onAdClickedInterstitial: click")
            }

            override fun onLeftApplication() {
                Log.d(ContentValues.TAG, "onLeftApplicationInterstitial: Left app")
            }

            override fun onReturnedToApplication() {
                Log.d(ContentValues.TAG, "onReturnedToApplicationInterstitial: Return to app")
            }

            override fun onImpression(p0: ImpressionData?) {
                Log.d(ContentValues.TAG, "onImpression: $p0")
                firebaseAnalytics!!.logEvent(Companion.EVENT_NAME_INTER_IMPERSSION) {
                    param(EVENT_PARAM_AD_SOURCE, pageName!!)
                }
            }

        })
        yandexInterstitialAd!!.loadAd(adRequest)
    }

    /**
     * Демонстрация полнжкранной рекламы
     */
    private fun showYandexInter(){
        if(isYandexInterstitialLoaded){
            yandexInterstitialAd!!.show()
        }
    }

    private fun startAdmobWorker(mainActivity: Activity?) {
        this.mainActivity = mainActivity
        FirebaseApp.initializeApp(context!!)
        firebaseAnalytics = Firebase.analytics
        fetchOpenAd(context!!)
        loadInter(context!!)
    }

    /** LifecycleObserver methods  */
   /* @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        showOpenAdIfAvailable(context!!)
        Log.d(TAG, "onStart")
    }*/

    fun isAdmobInterShowReady(): Boolean{
        return !isInterError && mInterstitialAd == null
    }

    private fun loadAdmobBannerIntoContainer(activity: Activity, adContainerView: FrameLayout, context: Context) {
        adContainerView.visibility = View.INVISIBLE
        val mAdView = AdView(context)
        mAdView.adUnitId = BANNER_AD_UNIT_ID!!
        adContainerView.addView(mAdView)
        val adRequest = AdRequest.Builder().build()
        val adSize = size(activity, adContainerView, context)
        mAdView.setAdSize(adSize)
        mAdView.loadAd(adRequest)
        mAdView.adListener = object : AdListener() {
            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                //loadIronBanner(activity, adContainerView)
            }
            override fun onAdLoaded() {
                super.onAdLoaded()
                adContainerView.visibility = View.VISIBLE
                bannerIsVisible = true
            }

            override fun onAdImpression() {
                super.onAdImpression()
                firebaseAnalytics!!.logEvent(Companion.EVENT_NAME_BANNER_IMPERSSION) {
                    param(EVENT_PARAM_AD_SOURCE, pageName!!)
                }
            }
        }
    }

    private fun size(activity: Activity, adContainerView: FrameLayout, context: Context): AdSize{
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

    private fun getScreenWidth(context: Context): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = (context as Activity).windowManager.currentWindowMetrics
            val insets = windowMetrics.windowInsets
                .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            windowMetrics.bounds.width() - insets.left - insets.right
        } else {
            val outMetrics = context.resources.displayMetrics
            outMetrics.widthPixels
        }
    }

    private val isOpenAdAvailable: Boolean
        get() = appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)

    /** Creates and returns ad request.  */
    private val adRequest: AdRequest
        get() = AdRequest.Builder().build()
    val isInterReady: Boolean
        get() = mInterstitialAd != null

    private fun showAdmobInter() {
        if (mInterstitialAd != null) {
            _isShowingAd.value = true
            Log.wtf(TAG, "Start showing inter")
            setInterCallback()
            mInterstitialAd!!.show(mainActivity!!)
        } else {
            Log.d(TAG, "The interstitial ad wasn't ready yet.")
        }
    }

    private fun setInterCallback() {
        mInterstitialAd!!.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                // Called when fullscreen content is dismissed.
                Log.d("TAG", "The ad was dismissed.")
                _isShowingAd.value = false;
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                // Called when fullscreen content failed to show.
                Log.d("TAG", "The ad failed to show.")
                isInterError = true
            }

            override fun onAdShowedFullScreenContent() {
                _isShowingAd.value = true
                // Called when fullscreen content is shown.
                // Make sure to set your reference to null so you don't
                // show it a second time.
                mInterstitialAd = null
                loadInter(context!!)
                Log.d("TAG", "The ad was shown.")
                firebaseAnalytics!!.logEvent(Companion.EVENT_NAME_INTER_IMPERSSION) {
                    param(EVENT_PARAM_AD_SOURCE, pageName!!)
                }
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

    /** Shows the ad if one isn't already showing.  */
    private fun showOpenAdIfAvailable(context: Context) {
        // Only show ad if there is not already an app open ad currently showing
        // and an ad is available.
        if (!isShowingAd.value!! && isOpenAdAvailable) {
            Log.d(TAG, "Will show ad.")
            val fullScreenContentCallback: FullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        // Set the reference to null so isAdAvailable() returns false.
                        appOpenAd = null
                        _isShowingAd.value = false
                        fetchOpenAd(context)
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {}
                    override fun onAdShowedFullScreenContent() {
                        _isShowingAd.value = true
                        firebaseAnalytics!!.logEvent(EVENT_NAME_OPEN_AD_IMPERSSION) {
                            param(EVENT_PARAM_AD_SOURCE, pageName!!)
                        }
                    }
                }
            appOpenAd!!.fullScreenContentCallback = fullScreenContentCallback
            appOpenAd!!.show(mainActivity!!)
        } else {
            Log.d(TAG, "Open Ad is not Available")
        }
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
            context, APP_OPEN_AD_UNIT_ID!!, request,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback as AppOpenAd.AppOpenAdLoadCallback
        )
    }

    /** ActivityLifecycleCallback methods  */
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityStopped(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {
        currentActivity = null
    }

    companion object {
        private const val TAG = "NewAdWorker"
        private const val EVENT_NAME_OPEN_AD_IMPERSSION = "openad_impression"
        private const val EVENT_NAME_BANNER_IMPERSSION = "banner_impression"
        private const val EVENT_NAME_INTER_IMPERSSION = "inter_impression"
        private const val EVENT_PARAM_AD_SOURCE = "adsource"
    }

    init {
        if (USE_TEST_ADS){
            APP_OPEN_AD_UNIT_ID = "ca-app-pub-3940256099942544/3419835294" // TEST AD ID
            INTER_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712" // TEST AD ID
            BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111" // TEST AD ID
        }else{
            APP_OPEN_AD_UNIT_ID = ""
            INTER_AD_UNIT_ID = ""
            BANNER_AD_UNIT_ID = ""
            YANDEX_INTER_BLOCK_ID = ""
            YANDEX_BANNER_BLOCK_ID = ""
        }
    }
}
