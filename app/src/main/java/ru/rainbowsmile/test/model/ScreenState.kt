package ru.rainbowsmile.test.model

sealed class ScreenState {
    object Success : ScreenState()
    data class Error(val exception: Throwable) : ScreenState()
    object Loading : ScreenState()
}
