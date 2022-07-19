package com.star.lite.junk.adworkerlib.retrofit

import retrofit2.Call
import retrofit2.http.GET

interface RegionService {

    @GET("begin")
    fun getRegionWithBegin(): Call<RegionRes>

    @GET("p")
    fun getRegionWithP(): Call<RegionRes>
}
