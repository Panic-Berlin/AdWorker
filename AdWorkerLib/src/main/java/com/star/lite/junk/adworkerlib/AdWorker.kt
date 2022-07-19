package com.star.lite.junk.adworkerlib

import android.app.Activity
import android.util.Log
import android.widget.FrameLayout
import com.google.android.gms.ads.MobileAds
import com.google.firebase.analytics.FirebaseAnalytics
import com.ironsource.mediationsdk.IronSource
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.star.lite.junk.adworkerlib.retrofit.RegionRes
import com.star.lite.junk.adworkerlib.retrofit.RegionService
import com.star.lite.junk.adworkerlib.utils.AdMob
import com.star.lite.junk.adworkerlib.utils.ConstantsAds.Companion.ADMOB
import com.star.lite.junk.adworkerlib.utils.ConstantsAds.Companion.APP_KEY
import com.star.lite.junk.adworkerlib.utils.ConstantsAds.Companion.BASE_URL
import com.star.lite.junk.adworkerlib.utils.ConstantsAds.Companion.YANDEX
import com.star.lite.junk.adworkerlib.utils.ConstantsAds.Companion.region
import com.star.lite.junk.adworkerlib.utils.IronSourceAds
import com.star.lite.junk.adworkerlib.utils.YandexAds
import com.yandex.mobile.ads.banner.BannerAdView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class AdWorker{

    private val TAG = "AdWorker"


    fun getRegion(endToken: String){
        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
        val api = retrofit.create(RegionService::class.java)

        when(endToken){
            TOKEN_WITH_P -> {
                api.getRegionWithP().enqueue(object : Callback<RegionRes> {
                    override fun onResponse(call: Call<RegionRes>, response: Response<RegionRes>) {
                        region = response.body()?.result.toString()
                        Log.d(TAG, "onResponse: $region")
                    }

                    override fun onFailure(call: Call<RegionRes>, t: Throwable) {
                        Log.d(TAG, "onFailure: $t")
                    }
                })
            }
            TOKEN_WITH_BEGIN -> {
                api.getRegionWithBegin().enqueue(object : Callback<RegionRes> {
                    override fun onResponse(call: Call<RegionRes>, response: Response<RegionRes>) {
                        region = response.body()?.result.toString()
                        Log.d(TAG, "onResponse: $region")
                    }

                    override fun onFailure(call: Call<RegionRes>, t: Throwable) {
                        Log.d(TAG, "onFailure: $t")
                    }
                })
            }
        }
    }

    fun initialize(activity: Activity, firebaseAnalytics: FirebaseAnalytics, pageName: String){
        when(region){
            YANDEX -> {
                com.yandex.mobile.ads.common.MobileAds.initialize(activity){
                    YandexAds(activity).startYandexAdWorker(firebaseAnalytics, pageName)
                }
            }
            ADMOB -> {
                MobileAds.initialize(activity){
                    AdMob().startAdmobWorker(activity, firebaseAnalytics, pageName)
                }
            }
            else -> {
                IronSource.init(activity, APP_KEY){
                   IronSourceAds().startIronSource(firebaseAnalytics, pageName)
                }
            }
        }
    }

    fun showInter(activity: Activity, firebaseAnalytics: FirebaseAnalytics, pageName: String){
        when(region){
            YANDEX -> {
                YandexAds(activity).showYandexInter()
            }
            ADMOB -> {
                AdMob().showAdmobInter(activity, firebaseAnalytics, pageName)
            }
            else -> {
                IronSourceAds().showIronInter()
            }
        }
    }

    fun loadBanner(
        bannerAdView: BannerAdView,
        activity: Activity,
        adContainerView: FrameLayout,
        firebaseAnalytics: FirebaseAnalytics,
        pageName: String
    ){
        when(region){
            YANDEX -> {
                YandexAds(activity).loadYandexBannerIntoContainer(bannerAdView, firebaseAnalytics, pageName)
            }
            ADMOB -> {
                AdMob().loadAdmobBannerIntoContainer(activity, adContainerView, activity, firebaseAnalytics, pageName)
            }
            else -> {
                IronSourceAds().loadIronBanner(activity, adContainerView, firebaseAnalytics, pageName)
            }
        }
    }

    fun onStart(activity: Activity, firebaseAnalytics: FirebaseAnalytics, pageName: String){
        AdMob().onStart(activity, firebaseAnalytics, pageName)
    }

    fun onPause(activity: Activity){
        IronSourceAds().onPause(activity)
    }

    fun onResume(activity: Activity){
        IronSourceAds().onResume(activity)
    }
}

const val TOKEN_WITH_P = "p"
const val TOKEN_WITH_BEGIN = "begin"

