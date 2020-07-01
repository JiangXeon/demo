package com.xeon.baseDemo.api

import com.xeon.baseDemo.App
import com.xeon.baseDemo.data.Dto
import com.xeon.baseDemo.data.LocationRule
import com.xeon.baseDemo.utils.Req
import com.xeon.baseDemo.utils.getImeiAndICCIDString
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AppApi {
    @GET("/app/locationRule")
    fun getLocationRule(@Header("apiKey") apikey: String = App.instance.getImeiAndICCIDString()): Single<Dto<LocationRule>>

    @POST("/app/locations")
    fun upLoadLocations(
        @Body req: Req,
        @Header("apiKey") apikey: String = App.instance.getImeiAndICCIDString(req.content)
    ): Observable<Dto<Boolean>>

    @GET("/")
    fun testBaidu(): Single<String>
}