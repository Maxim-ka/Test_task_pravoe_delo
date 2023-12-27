package reschikov.test.pravoedelo.domain

sealed class AppException : Throwable(){
    object NoInternet : AppException()
    class ValidationError(val string: String) : AppException()
}
