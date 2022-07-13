# AdWorker - advertising library(AdMob, YandexAds, IronSource)

## How to:

To get a Git project into your build:

**Step 1.** Add the JitPack repository to your build file

Add it in your root settings.gradle at the end of repositories:
```
repositories {
	...
	maven { url 'https://jitpack.io' }
}
```

**Step 2.** Add the dependency
```
dependencies {
	implementation 'com.github.Panic-Berlin:AdWorker:v1.3.2'
}
```
**That's it!**

## Initialize

**Step 1.** Add ads id

```
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ...
        ConstantsAds.YANDEX_INTER_BLOCK_ID = "your yandex interstitial id" //Demo id: R-M-DEMO-interstitial
        ConstantsAds.YANDEX_BANNER_BLOCK_ID = "your yandex banner id" //Demo id: R-M-DEMO-320x50
        ConstantsAds.BANNER_AD_UNIT_ID = "your admob banner id" //Demo id: ca-app-pub-3940256099942544/6300978111
        ConstantsAds.APP_OPEN_AD_UNIT_ID = "your admob app open ad id" //Demo id: ca-app-pub-3940256099942544/3419835294
        ConstantsAds.INTER_AD_UNIT_ID = "your admob interstitial id" //Demo id: ca-app-pub-3940256099942544/1033173712
        ConstantsAds.APP_KEY = "your iron source app key"
        ...
   }
```

**Step 2.** Set region.

```
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ...
        ConstantsAds.YANDEX = "region russia"
        ConstantsAds.ADMOB = "other regions"
        ConstantsAds.BASE_URL = "your base url for get region"
        AdWorker().getRegion()
        ...
    }
```    
    
**Step 3.** Initialize.


```
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ...
        AdWorker().initialize(activity, firebaseAnalytics, pageName)
        ...
    }
``` 

**Step 4.** Load banner ads.

```
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ...
        AdWorker().loadBanner(yandexBanner, activity, adMobBanner, firebaseAnalytics, pageName)
        ...
    }
```

**Step 5.** Show interstitial ads.

```
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ...
        viewBinding.btnShowInter.setOnClickListener {
            AdWorker().showInter(activity, firebaseAnalytics, pageName)
        }
        ...
    }
```

**Step 6.** Add OnResume and onPause

```
override fun onResume() {
    super.onResume()
    AdWorker().onResume(activity)
}

override fun onPause() {
    super.onPause()
    AdWorker().onPause(activity)
}
```
___
**Great, everything is ready!**
