package ru.rustore.sdk.reviewexample.userflow

import android.content.Context
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import ru.rustore.sdk.reviewexample.userflow.model.UserFlowEvent
import ru.rustore.sdk.reviewexample.userflow.model.UserFlowState
import ru.vk.store.sdk.review.RuStoreReviewManager
import ru.vk.store.sdk.review.RuStoreReviewManagerFactory
import ru.vk.store.sdk.review.model.ReviewInfo

class UserFlowExampleViewModel : ViewModel() {

    private var isInitCalled: Boolean = false

    private val _state = MutableStateFlow(UserFlowState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<UserFlowEvent>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val event = _event.asSharedFlow()

    val counterWinCondition = COUNTER_WIN_CONDITION

    private lateinit var reviewManager: RuStoreReviewManager

    private var reviewInfo: ReviewInfo? = null

    fun init(context: Context) {
        if (isInitCalled) return

        createReviewManager(context)
        // Запрашиваем reviewInfo в начале флоу пользователя,
        // где-то за 0-3 минуты до показа шторки,
        // чтобы запуск шторки оценки в конце флоу произошел мгновенно.
        requestReviewFlow()

        isInitCalled = true
    }

    fun onCounterClick() {
        _state.update { state ->
            if (state.hasWon) return@update state

            val newCounterValue = state.counterValue + 1
            val hasWon = newCounterValue >= counterWinCondition
            if (hasWon) {
                launchReviewFlow()
            }

            state.copy(
                counterValue = newCounterValue,
                hasWon = hasWon
            )
        }
    }

    private fun createReviewManager(context: Context) {
        reviewManager = RuStoreReviewManagerFactory.create(context)
    }

    // Запрашиваем reviewInfo где-то за 0-3 минуты до показа шторки,
    // чтобы запуск шторки оценки произошел мгновенно.
    // Не показываем ошибку, если что-то пошло не так:
    // флоу пользователя не должно быть прервано ошибкой оценки.
    private fun requestReviewFlow() {
        if (reviewInfo != null) return

        reviewManager.requestReviewFlow().addOnSuccessListener { reviewInfo ->
            this.reviewInfo = reviewInfo
        }
    }

    // При конце флоу пользователя, предлагаем оценить приложение,
    // используя ранее полученный reviewInfo.
    // При этом, вне зависимости от результата (успех/ошибка),
    // продолжаем флоу нашего приложения.
    // Не показываем ошибку, если что-то пошло не так:
    // флоу пользователя не должно быть прервано ошибкой оценки.
    private fun launchReviewFlow() {
        val reviewInfo = reviewInfo
        if (reviewInfo != null) {
            reviewManager.launchReviewFlow(reviewInfo).addOnSuccessListener {
                _event.tryEmit(UserFlowEvent.ReviewEnd)
            }.addOnFailureListener {
                _event.tryEmit(UserFlowEvent.ReviewEnd)
            }
        } else {
            _event.tryEmit(UserFlowEvent.ReviewEnd)
        }
    }

    companion object {
        private const val COUNTER_WIN_CONDITION: Int = 5
    }
}
