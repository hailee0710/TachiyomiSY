package mihon.core

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics

object FirebaseConfig {
    private lateinit var analytics: FirebaseAnalytics
    private lateinit var crashlytics: FirebaseCrashlytics

    fun init(context: Context) {
        try {
            FirebaseApp.initializeApp(context)
            analytics = FirebaseAnalytics.getInstance(context)
            crashlytics = FirebaseCrashlytics.getInstance()
        } catch (e: Exception) {
            // Ignore
        }
    }

    fun setAnalyticsEnabled(enabled: Boolean) {
        if (::analytics.isInitialized) {
            analytics.setAnalyticsCollectionEnabled(enabled)
        }
    }

    fun setCrashlyticsEnabled(enabled: Boolean) {
        if (::crashlytics.isInitialized) {
            crashlytics.isCrashlyticsCollectionEnabled = enabled
        }
    }

    fun log(message: String) {
        if (::crashlytics.isInitialized) {
            crashlytics.log(message)
        }
    }

    fun setUserProperty(name: String, value: String) {
        if (::analytics.isInitialized) {
            analytics.setUserProperty(name, value)
        }
    }
}
