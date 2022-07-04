package com.star.lite.junk.adworkerlib

import android.app.Activity
import android.widget.FrameLayout
import com.google.android.gms.ads.MobileAds
import com.ironsource.mediationsdk.IronSource
import com.star.lite.junk.adworkerlib.utils.AdMob
import com.star.lite.junk.adworkerlib.utils.IronSourceAds
import com.star.lite.junk.adworkerlib.utils.YandexAds
import com.yandex.mobile.ads.banner.BannerAdView

class AdWorker{

    private var YANDEX: String = "io4xqqApBq5DG0qV"
    private var ADMOB: String = "gb2gTqPgccq3ACXt"
    private val APP_KEY = "157edbc35"

    fun initialize(activity: Activity, region: String){
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

    fun showInter(region: String, activity: Activity){
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
        region: String
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
