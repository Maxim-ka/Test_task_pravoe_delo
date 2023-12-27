package reschikov.test.pravoedelo.data.network

import reschikov.test.pravoedelo.data.network.models.Code
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface IAuth {

    @GET("getCode")
    fun getCode(@Query("login") login: String): Call<Code>

    @GET("getToken")
    fun getToken(@Query("login") login: String, @Query("password") password: String): Call<String>
}