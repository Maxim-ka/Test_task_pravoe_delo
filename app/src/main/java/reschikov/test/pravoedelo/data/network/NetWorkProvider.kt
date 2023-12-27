package reschikov.test.pravoedelo.data.network

import android.net.ConnectivityManager
import android.os.Build
import android.util.Log
import kotlinx.coroutines.coroutineScope
import reschikov.test.pravoedelo.data.IRequester
import reschikov.test.pravoedelo.data.network.models.Code
import reschikov.test.pravoedelo.domain.AppException
import javax.inject.Inject
import kotlin.coroutines.suspendCoroutine


class NetWorkProvider @Inject constructor(private val iAuth: IAuth,
                                          private val cm: ConnectivityManager) : IRequester {


    override suspend fun getSMSCode(tel: String): Pair<Code?, Throwable?> {
        if (checkLackOfNetwork()) return Pair(null, AppException.NoInternet)
        return  try {
            Pair(requestCode(tel), null)
        } catch (e: Throwable) {
            Pair(null, e)
        }
    }

    override suspend fun getToken(tel: String, code: String): Pair<String?, Throwable?> {
        if (checkLackOfNetwork()) return Pair(null, AppException.NoInternet)
        return  try {
            Pair(requestToken(tel, code), null)
        } catch (e: Throwable) {
            Pair(null, e)
        }
    }

    @Throws(Throwable::class)
    private suspend fun requestCode(tel: String) = coroutineScope {
        Log.d("ФФФ", "tel $tel")
        suspendCoroutine<Code> {
            iAuth.getCode(tel).enqueue(getCallBack(it))
        }
    }

    @Throws(Throwable::class)
    private suspend fun requestToken(tel: String, code: String) = coroutineScope {
        Log.d("ФФФ", "tel $tel, code $code")
        suspendCoroutine<String> {
            iAuth.getToken(tel, code).enqueue(getCallBack(it))
        }
    }

    private fun checkLackOfNetwork(): Boolean {
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
           return cm.activeNetwork == null
       }
       return !cm.isDefaultNetworkActive
    }
}