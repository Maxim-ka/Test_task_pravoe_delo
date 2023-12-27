package reschikov.test.pravoedelo.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import reschikov.test.pravoedelo.domain.Success
import reschikov.test.pravoedelo.ui.screens.code.IRepository
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class Repository @Inject constructor(private val iRequester: IRequester) : IRepository {


    override suspend fun getSMSCode(phone: String): Pair<Success?, Throwable?> =
        withContext(Dispatchers.IO) {
            val reply = iRequester.getSMSCode(phone)
            reply.first?.let { Pair(Success, null) } ?: Pair(null, reply.second)
        }

    override suspend fun getToken(phone: String, code: String): Pair<String?, Throwable?> =
        withContext(Dispatchers.IO) {
            val reply = iRequester.getToken(phone, code)
            reply.first?.let { Pair(it, null) } ?: Pair(null, reply.second)
        }
}