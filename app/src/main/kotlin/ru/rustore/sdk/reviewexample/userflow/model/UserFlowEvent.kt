package ru.rustore.sdk.reviewexample.userflow.model

sealed class UserFlowEvent {
    object ReviewEnd: UserFlowEvent()
}
