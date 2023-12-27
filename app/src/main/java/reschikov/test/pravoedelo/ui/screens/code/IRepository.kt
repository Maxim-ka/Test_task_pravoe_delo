package reschikov.test.pravoedelo.ui.screens.code

import reschikov.test.pravoedelo.domain.Success


interface IRepository {

    suspend fun getSMSCode(phone: String): Pair<Success?, Throwable?>
    suspend fun getToken(phone: String, code: String): Pair<String?, Throwable?>
}