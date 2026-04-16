package mihon.core

import android.content.Context

object FirebaseConfig {
    fun init(context: Context) = Unit

    fun setAnalyticsEnabled(enabled: Boolean) = Unit

    fun setCrashlyticsEnabled(enabled: Boolean) = Unit

    fun log(message: String) = Unit

    fun setUserProperty(name: String, value: String) = Unit
}
