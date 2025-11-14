```text
📱 Android News — Real-Time Android Updates with Push Notifications
      Android News is a real-time Android developer news app that delivers the
  latest updates from trusted sources such as Android Developers Blog, NewsAPI.

      The app features FCM push notifications, Firebase Cloud Functions,
  topic-based alerts, offline caching with Room, and a clean MVVM + Clean Architecture structure.

      This project is purpose-built to showcase mastery in push notifications,
  modern Android APIs, and full end-to-end backend integration.

🚀 Features
  📰 News
    Latest Android-related articles from multiple sources
    Category filters: Android, Jetpack, Kotlin, Official
    
  🔔 Real-Time Notifications
    FCM topic-based push alerts
    Tap notification → deep links into DetailScreen(id)
    Big text style notifications + optional thumbnail

  ⚙️ Settings
    Subscribe/unsubscribe to FCM topics
    Show device FCM token (for testing)
    Toggle background refresh

🏛️ Architecture
  This project follows Clean Architecture + MVVM:

  presentation (Compose UI)
  domain (use cases + models)
  data (repositories + local + remote)
  core (FCM, notifications, WorkManager, DI)


    com.example.androidnews
    │
    ├── ui/
    │   ├── home/
    │   ├── detail/
    │   ├── history/
    │   ├── settings/
    │   └── navigation/AppNav.kt
    │
    ├── domain/
    │   ├── model/Article.kt
    │   └── usecase/
    │       ├── ShareURL.kt
    │       ├── ShareToken.kt
    │       ├── CopyToken.kt
    │       └── OpenUrlBrowser.kt
    │
    ├── data/
    │   ├── remote/
    │   │   ├── api/NewsApi.kt
    │   │   └── dto/*.kt
    │   ├── local/
    │   │   ├── entity/ArticleEntity.kt
    │   │   ├── dao/ArticleDao.kt
    │   │   └── db/AppDatabase.kt
    │   └── repo/
    │       ├── ArticleRepository.kt
    │       ├── ArticleRepositoryImpl.kt
    │       └── PushRepo.kt
    │
    ├── core/
    │   ├── di/ (Hilt modules)
    │   ├── notif/
    │   │   ├── NewsChannels.kt
    │   │   ├── NotificationBuilder.kt
    │   │   └── ActionReceiver.kt
    │   └── fcm/
    │       └── NewsFirebaseService.kt
    │
    └── MainApplication.kt
  </p>

🛠 Tech Stack
  🎨 UI & Architecture
    Jetpack Compose
    Material 3
    Navigation Compose
    MVVM + Clean Architecture
    Hilt Dependency Injection
    
  📡 Networking
    Retrofit + OkHttp
    Gson (JSON parsing)
    OkHttp Logging Interceptor
    
  💾 Local Storage
    Room Database
    Flows for live reactive updates
    
  ☁ Push Notifications
    Firebase Cloud Messaging (FCM)
    Topic-based subscriptions (android, jetpack, kotlin, official)
    FirebaseMessagingService for data messages
    Notification Channels (per category)
    Rich notifications: BigText, optional image, deep links

🧩 End-to-End Flow
  
  Cloud Scheduler (cron)
        │
        ▼
Cloud Function fetches + diffs news
        │
        ▼
     FCM Push (topic data)
        │
        ▼
NewsFirebaseService receives push
        │
        ├── upsert article into Room
        └── build notification (deep link)
                │
                ▼
         User opens DetailScreen(id)
         
