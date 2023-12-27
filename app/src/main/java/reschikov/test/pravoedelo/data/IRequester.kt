package reschikov.test.pravoedelo.data

import reschikov.test.pravoedelo.data.network.models.Code
import reschikov.test.pravoedelo.domain.AppException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

interface IRequester {

    suspend fun getSMSCode(tel: String): Pair<Code?, Throwable?>

    suspend fun getToken(tel: String, code: String): Pair<String?, Throwable?>

    fun <T> getCallBack(continuation: Continuation<T>): Callback<T> {
        return object : Callback<T> {
            override fun onFailure(call: Call<T>, t: Throwable) {
                continuation.resumeWithException(t)
            }

            override fun onResponse(call: Call<T>, response: Response<T>) {
                if(response.isSuccessful){
                    response.body()?.let { continuation.resume(it) }
                } else {
                    val err = if (response.code() == 422) {
                        response.errorBody()?.let { (AppException.ValidationError(it.string())) }
                    } else {
                        response.errorBody()?.let { Throwable(it.string()) }
                    }
                    err?.let { continuation.resumeWithException(it) }
                }
            }
        }
    }


}