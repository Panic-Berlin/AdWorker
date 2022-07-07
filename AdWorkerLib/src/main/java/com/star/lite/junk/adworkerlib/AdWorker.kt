package com.star.lite.junk.adworkerlib

import android.app.Activity
import android.util.Log
import android.widget.FrameLayout
import com.google.android.gms.ads.MobileAds
import com.ironsource.mediationsdk.IronSource
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.star.lite.junk.adworkerlib.retrofit.RegionRes
import com.star.lite.junk.adworkerlib.retrofit.RegionService
import com.star.lite.junk.adworkerlib.utils.AdMob
import com.star.lite.junk.adworkerlib.utils.IronSourceAds
import com.star.lite.junk.adworkerlib.utils.YandexAds
import com.yandex.mobile.ads.banner.BannerAdView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class AdWorker{

    private var YANDEX: String = "iLjOmXcXo1XqjgHu"
    private var ADMOB: String = "kTzKDWTUTq1W3Bdq"
    private var region = "iLjOmXcXo1XqjgHu"
    private val APP_KEY = "157edbc35"
    private val TAG = "AdWorker"


    fun getRegion(){
        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
        val api = retrofit.create(RegionService::class.java)

        api.getRegion().enqueue(object : Callback<RegionRes> {
            override fun onResponse(call: Call<RegionRes>, response: Response<RegionRes>) {
                region = response.body()?.result.toString()
                Log.d(TAG, "onResponse: $region")
            }

            override fun onFailure(call: Call<RegionRes>, t: Throwable) {
                Log.d(TAG, "onFailure: $t")
            }
        })
    }

    fun initialize(activity: Activity){
        when(region){
            YANDEX -> {
                com.yandex.mobile.ads.common.MobileAds.initialize(activity){
                    YandexAds(activity).startYandexAdWorker()
                }
            }
            ADMOB -> {
                MobileAds.initialize(activity){
                    AdMob().startAdmobWorker(activity)
                }
            }
            else -> {
                IronSource.init(activity, APP_KEY){
                   IronSourceAds().startIronSource()
                }
            }
        }
    }

    fun showInter(activity: Activity){
        when(region){
            YANDEX -> {
                YandexAds(activity).showYandexInter()
            }
            ADMOB -> {
                AdMob().showAdmobInter(activity)
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
    ){
        when(region){
            YANDEX -> {
                YandexAds(activity).loadYandexBannerIntoContainer(bannerAdView)
            }
            ADMOB -> {
                AdMob().loadAdmobBannerIntoContainer(activity, adContainerView, activity)
            }
            else -> {
                IronSourceAds().loadIronBanner(activity, adContainerView)
            }
        }
    }

    fun onStart(activity: Activity){
        AdMob().onStart(activity)
    }

    fun onPause(activity: Activity){
        IronSourceAds().onPause(activity)
    }

    fun onResume(activity: Activity){
        IronSourceAds().onResume(activity)
    }
}
private const val BASE_URL = "https://atrack.us/"
