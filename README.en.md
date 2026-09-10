<!-- ── Language switch (EN active) ──────────────────────────────────── -->
<div align="left" style="margin:0 0 14px 0;">

  <span style="display:inline-block;
               padding:.28rem .6rem;
               border:1px solid rgba(0,0,0,.18);
               border-radius:10px 0 0 10px;
               font-weight:400;
               font-size:12px;
               letter-spacing:.06em;
               color:#111827;
               background:linear-gradient(180deg,#ffffff,#e9edf2);
               box-shadow:0 1px 0 rgba(0,0,0,.06);">
    [RU][ru]
  </span><span style="display:inline-block;
               margin-left:-1px;
               padding:.28rem .6rem;
               border:1px solid rgba(0,0,0,.14);
               border-radius:0 10px 10px 0;
               font-weight:400;
               font-size:12px;
               letter-spacing:.06em;
               background:linear-gradient(180deg,#f3f4f6,#ffffff);
               box-shadow:inset 0 2px 6px rgba(0,0,0,.10);">
    EN
  </span>

</div>
<!-- ────────────────────────────────────────────────────────────────── -->

# Example of integrating the RuStore Reviews and Ratings SDK
## [Reviews and Ratings SDK Documentation](https://help.rustore.ru/rustore/for_developers/developer-documentation/SDK-reviews-ratings)


### Table of Contents
- [Conditions for SDK Ratings and Reviews to Work](#conditions-for-sdk-ratings-and-reviews-to-work)
- [Preparing Required Parameters](#preparing-required-parameters)
- [Setting Up the Sample Application](#setting-up-the-sample-application)
- [Usage Scenario](#usage-scenario)
- [Distribution Conditions](#distribution-conditions)
- [Technical Support](#technical-support)


### Conditions for SDK Ratings and Reviews to Work

To use the SDK for ratings and reviews, the following conditions must be met:

User Requirements:

- Android OS version 7.0 or higher.

- The user's device has the latest version of RuStore installed.

- The user is logged into RuStore.

- The app must be published in RuStore.

Developer/App Requirements:

- The `ApplicationId` specified in `build.gradle` must match the `applicationId` of the APK file you published in the RuStore console.

- The keystore signature must match the signature used to sign the app published in the RuStore console. Ensure that the `buildType` being used (e.g., debug) uses the same signature as the published app (e.g., release).

### Preparing Required Parameters

1. `applicationId` - found in your project's `build.gradle` file from the app you published in the RuStore console
    ```
    android {
        defaultConfig {
            applicationId = "ru.rustore.sdk.reviewexample"
        }
    }
    ```

2. `release.keystore` - the signature used to sign the app published in the RuStore console.

3. `release.properties` - this file should contain the signing parameters of the app published in the RuStore console. [How to work with APK signature keys](https://www.rustore.ru/help/developers/publishing-and-verifying-apps/app-publication/apk-signature/)


### Setting Up the Sample Application

1. Replace the `applicationId` in `build.gradle` with the `applicationId` of the APK file you published in the RuStore console:
   ```
   android {
       defaultConfig {
          applicationId = "ru.rustore.sdk.reviewexample"
       }
   }
   ```
   
2. In the `cert` directory, replace the `release.keystore` certificate with your app's certificate. Also, configure the `key_alias`, `key_password`, and `store_password` in `release.properties`. The `release.keystore` signature must match the signature used to sign the app published in the RuStore console. Make sure that the `buildType` being used (e.g., debug) uses the same signature as the published app (e.g., release).

3. Run the project and check that the app works correctly.


### Usage Scenario

Imagine we have a game where the player needs to tap a button five times to win.
This means the start of the user flow is the game start (screen opening), and the end is winning the game.

![Start of Flow](https://i.imgur.com/mUmqqHl.jpg) ![App Rating](https://i.imgur.com/qBteJTG.jpg) ![End of Flow](https://i.imgur.com/FnDmNYP.jpg)


#### Preparing for Ratings

To work with ratings, create a `RuStoreReviewManager` using `RuStoreReviewManagerFactory`.
```
val reviewManager = RuStoreReviewManagerFactory.create(context)
```

#### Preparing to Launch App Rating

It is recommended to use [requestReviewFlow](https://www.rustore.ru/help/sdk/reviews-ratings/kotlin-java/6-0-0#preparing-to-launch-app-rating) beforehand before calling `launchReviewFlow` to prepare necessary information for displaying the screen. The lifetime of `ReviewInfo` is about five minutes.

```
private fun requestReviewFlow() {
    if (reviewInfo != null) return

    reviewManager.requestReviewFlow().addOnSuccessListener { reviewInfo ->
            this.reviewInfo = reviewInfo
    }
}
```

#### Launching App Rating

To launch the rating and review form for the app, call the [launchReviewFlow](https://www.rustore.ru/help/sdk/reviews-ratings/kotlin-java/6-0-0#launching-app-rating) method using previously obtained `ReviewInfo`.

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

> The full code of the examples above is available in this repository (UserFlowExampleFragment, UserFlowExampleViewModel).


### Distribution Conditions
This software, including source codes, binary libraries, and other files, is distributed under the MIT license. Licensing information is available in the `MIT-LICENSE.txt` document.


### Technical Support
If you have any questions regarding integration of the Reviews and Ratings SDK, please contact us via [this link](https://www.rustore.ru/help/sdk/reviews-ratings).

[ru]: README.md
[en]: README.en.md
