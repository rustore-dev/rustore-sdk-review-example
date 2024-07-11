# Пример внедрения SDK отзывов и оценок RuStore
## [Документация SDK отзывов и оценок](https://help.rustore.ru/rustore/for_developers/developer-documentation/SDK-reviews-ratings)


### Оглавление
- [Условия работы SDK оценок и отзывов](#Условия-работы-SDK-оценок-и-отзывов)
- [Подготовка требуемых параметров](#Подготовка-требуемых-параметров)
- [Настройка примера приложения](#Настройка-примера-приложения)
- [Сценарий использования](#Сценарий-использования)
- [Условия распространения](#Условия-распространения)
- [Техническая поддержка](#Техническая-поддержка)


### Условия работы SDK оценок и отзывов

Для работы SDK оценок и отзывов необходимо соблюдение следующих условий:

Требования для пользователя:

- ОС Android версии 7.0 или выше.

- На устройстве пользователя установлена актуальная версия RuStore.

- Пользователь авторизован в RuStore.

- Приложение должно быть опубликовано в RuStore.

Требования для разработчика/приложения:

- ApplicationId, указанный в build.gradle, совпадает с applicationId apk-файла, который вы публиковали в консоль RuStore.

- Подпись keystore должна совпадать с подписью, которой было подписано приложение, опубликованное в консоль RuStore. Убедитесь, что используемый buildType (пр. debug) использует такую же подпись, что и опубликованное приложение (пр. release).

### Подготовка требуемых параметров

1. `applicationId` - из приложения, которое вы публиковали в консоль RuStore, находится в файле build.gradle вашего проекта
    ```
    android {
        defaultConfig {
            applicationId = "ru.rustore.sdk.reviewexample"
        }
    }
    ```

2. `release.keystore` - подпись, которой было подписано приложение, опубликованное в консоль RuStore.

3. `release.properties` - в этом файле должны быть указаны параметры подписи, которой было подписано приложение, опубликованное в консоль RuStore. [Как работать с ключами подписи APK-файлов](https://www.rustore.ru/help/developers/publishing-and-verifying-apps/app-publication/apk-signature/)


### Настройка примера приложения

1. Замените `applicationId`, в файле build.gradle, на applicationId apk-файла, который вы публиковали в консоль RuStore:
   ```
   android {
       defaultConfig {
          applicationId = "ru.rustore.sdk.reviewexample"
       }
   }
   ```
   
2. В директории `cert` замените сертификат `release.keystore` - сертификатом своего приложения, так же в `release.properties` выполните настройку параметров `key_alias`, `key_password`, `store_password`. Подпись `release.keystore` должна совпадать с подписью, которой было подписано приложение, опубликованное в консоль RuStore. Убедитесь, что используемый `buildType` (пр. debug) использует такую же подпись, что и опубликованное приложение (пр. release).

3. Запустите проект и проверьте работу приложения


### Сценарий-использования

Представим, что у нас есть игра, где нужно нажать 5 раз кнопку, чтобы победить.
Это означает, что начало флоу пользователя - старт игры (открытие экрана), а конец флоу - победа в игре.

![Начало флоу](https://i.imgur.com/mUmqqHl.jpg) ![Оценка приложения](https://i.imgur.com/qBteJTG.jpg) ![Конец флоу](https://i.imgur.com/FnDmNYP.jpg)


#### Подготовка к работе с оценками

Для работы с оценками необходимо создать RuStoreReviewManager с помощью `RuStoreReviewManagerFactory`.
```
val reviewManager = RuStoreReviewManagerFactory.create(context)
```

#### Подготовка к запуску оценки приложения

Рекомендуется использовать [requestReviewFlow](https://www.rustore.ru/help/sdk/reviews-ratings/kotlin-java/6-0-0#подготовка-к-запуску-оценки-приложения) заранее, перед вызовом launchReviewFlow, чтобы подготовить необходимую информацию для отображения экрана. Срок жизни ReviewInfo — около пяти минут.

```
private fun requestReviewFlow() {
    if (reviewInfo != null) return

    reviewManager.requestReviewFlow().addOnSuccessListener { reviewInfo ->
            this.reviewInfo = reviewInfo
    }
}
```

#### Запуск оценки приложения

Для запуска формы запроса оценки и отзыва о приложении у пользователя вызовите метод [launchReviewFlow](https://www.rustore.ru/help/sdk/reviews-ratings/kotlin-java/6-0-0#запуск-оценки-приложения), используя ранее полученный ReviewInfo.

```
    private fun launchReviewFlow() {
        val reviewInfo = reviewInfo
        if (reviewInfo != null) {
            reviewManager.launchReviewFlow(reviewInfo)
            .addOnSuccessListener {
                _event.tryEmit(UserFlowEvent.ReviewEnd)
            }
            .addOnFailureListener {
                _event.tryEmit(UserFlowEvent.ReviewEnd)
            }
        } else {
            _event.tryEmit(UserFlowEvent.ReviewEnd)
        }
    }
```

> Полный код приведенных выше примеров кода приведен в данном репозитории (UserFlowExampleFragment, UserFlowExampleViewModel).


### Условия распространения
Данное программное обеспечение, включая исходные коды, бинарные библиотеки и другие файлы распространяется под лицензией MIT. Информация о лицензировании доступна в документе `MIT-LICENSE.txt`


### Техническая поддержка
Если появились вопросы по интеграции SDK оценок и отзывов, обратитесь по [ссылке](https://www.rustore.ru/help/sdk/reviews-ratings).
