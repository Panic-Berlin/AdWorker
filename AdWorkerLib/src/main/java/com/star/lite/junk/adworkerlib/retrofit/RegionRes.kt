package com.star.lite.junk.adworkerlib.retrofit

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegionRes(
    @field:Json(name = "result")
    val result: String
)
