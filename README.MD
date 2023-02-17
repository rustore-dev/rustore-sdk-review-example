# Пример внедрения SDK отзывов и оценок RuStore
## [Документация SDK отзывов и оценок](https://help.rustore.ru/rustore/for_developers/developer-documentation/SDK-reviews-ratings)

### Требуемые условия

Для корректной работы SDK необходимо соблюдать следующие условия:

Требования для пользователя:
- На устройстве пользователя установлено приложение RuStore.

- Версия RuStoreApp на устройстве пользователя актуальная.

- Пользователь  авторизован в приложении RuStore.

Требования для разработчика/приложения:

- Android 6 (SDK 23)

- ApplicationId, указанный в build.gradle, совпадает с applicationId apk-файла, который вы публиковали в консоль RuStore:
```
android {
    defaultConfig {
        applicationId = "ru.rustore.sdk.reviewexample" // Зачастую в buildTypes приписывается .debug
    }
}
```

- Подпись keystore должна совпадать с подписью, которой было подписано приложение, опубликованное в консоль RuStore. Убедитесь, что используемый buildType (пр. debug) использует такую же подпись, что и опубликованное приложение (пр. release).

### Описание приложения

Представим, что у нас есть игра, где нужно нажать 5 раз кнопку, чтобы победить.
Это означает, что начало флоу пользователя - старт игры (открытие экрана), а конец флоу - победа в игре.

![Начало флоу](https://i.imgur.com/mUmqqHl.jpg) ![Оценка приложения](https://i.imgur.com/qBteJTG.jpg) ![Конец флоу](https://i.imgur.com/FnDmNYP.jpg)

### Использование методов

Рекомендуется использовать requestReviewFlow() в начале флоу пользователя (где-то за 0-3 минуты до показа шторки), чтобы запуск шторки оценки произошел мгновенно:
```
class UserFlowExampleFragment : Fragment() {

    private val viewModel: UserFlowExampleViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.init(requireContext())
        // ...
    }
```

```
class UserFlowExampleViewModel : ViewModel() {
    private lateinit var reviewManager: RuStoreReviewManager
    private var reviewInfo: ReviewInfo? = null
    
    private var isInitCalled: Boolean = false
    
    fun init(context: Context) {
        if (isInitCalled) return

        createReviewManager(context)
        requestReviewFlow()

        isInitCalled = true
    }
}
```
Открывать launchReviewFlow() необходимо в конце флоу пользователя (победа в игре):
```
class UserFlowExampleViewModel) : ViewModel() {
    private lateinit var reviewManager: RuStoreReviewManager
    private var reviewInfo: ReviewInfo? = null

    fun onCounterClick() {
        // ...
        if (hasWon) {
            launchReviewFlow()
        }
        // ...
    }

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
}

class UserFlowExampleFragment : Fragment() {
    // ...
    private fun handleEvent(event: UserFlowEvent) {
        when (event) {
            UserFlowEvent.ReviewEnd -> {
                // Продолжаем флоу приложения
            }
        }
    }
}
```
при этом продолжать флоу приложения не в зависимости от поставленной оценки (в любом случае показываем диалог).

> Полный код приведенных выше примеров кода приведен в данном репозитории (UserFlowExampleFragment, UserFlowExampleViewModel).

### Есть вопросы
Если появились вопросы по интеграции SDK оценок и отзывов, обратитесь по этой ссылке:
https://help.rustore.ru/rustore/trouble/user/help_user_email
или напишите на почту support@rustore.ru.
