export const FEED_URL_OFFICIAL =
  "https://android-developers.googleblog.com/atom.xml";

export const SOURCE_OFFICIAL = "Android Dev Blog";
export const CATEGORY_OFFICIAL = "official";

// Firestore doc that tracks last seen item for this source
export const LAST_SEEN_DOC_OFFICIAL =
 "lastSeen/official_androiddevblog";

// Android
export const FEED_URL_ANDROID =
  "https://androidstudio.googleblog.com/feeds/posts/default?alt=rss";
export const SOURCE_ANDROID = "Android Studio Release Updates";
export const CATEGORY_ANDROID = "android";
export const LAST_SEEN_DOC_ANDROID = "lastSeen/android_studio";

// Kotlin
export const FEED_URL_KOTLIN =
  "https://feeds.feedburner.com/kotlin";
export const SOURCE_KOTLIN = "Kotlin Blog";
export const CATEGORY_KOTLIN = "kotlin";
export const LAST_SEEN_DOC_KOTLIN = "lastSeen/kotlin_blog";

// Jetpack
export const FEED_URL_JETPACK =
  "https://developer.android.com/feeds/androidx-release-notes.xml";
export const SOURCE_JETPACK = "AndroidX Release Notes";
export const CATEGORY_JETPACK = "jetpack";
export const LAST_SEEN_DOC_JETPACK = "lastSeen/androidx_release_notes";


export type FeedConfig = {
  feedUrl: string;
  source: string;
  category: string;
  lastSeenDoc: string;
};

export const FEEDS: FeedConfig[] = [
  {
    feedUrl: FEED_URL_OFFICIAL,
    source: SOURCE_OFFICIAL,
    category: CATEGORY_OFFICIAL,
    lastSeenDoc: LAST_SEEN_DOC_OFFICIAL,
  },
  {
    feedUrl: FEED_URL_ANDROID,
    source: SOURCE_ANDROID,
    category: CATEGORY_ANDROID,
    lastSeenDoc: LAST_SEEN_DOC_ANDROID,
  },
  {
    feedUrl: FEED_URL_KOTLIN,
    source: SOURCE_KOTLIN,
    category: CATEGORY_KOTLIN,
    lastSeenDoc: LAST_SEEN_DOC_KOTLIN,
  },
  {
    feedUrl: FEED_URL_JETPACK,
    source: SOURCE_JETPACK,
    category: CATEGORY_JETPACK,
    lastSeenDoc: LAST_SEEN_DOC_JETPACK,
  },
];
