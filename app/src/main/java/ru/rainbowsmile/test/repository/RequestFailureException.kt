package ru.rainbowsmile.test.repository

import ru.rainbowsmile.test.utils.Constants
import java.lang.Exception

class RequestFailureException : Exception() {
    override val message: String
        get() = Constants.REQUEST_FAILURE_EXCEPTION
}
