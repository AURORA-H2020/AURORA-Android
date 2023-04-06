package eu.inscico.aurora_app.utils

sealed class TypedResult<out Success : Any, out Failure : Any> {

    companion object {}

    data class Success<out Success : Any>(
        val value: Success
    ) : TypedResult<Success, Nothing>()

    data class Failure<out Failure : Any>(
        val reason: Failure
    ) : TypedResult<Nothing, Failure>()

}