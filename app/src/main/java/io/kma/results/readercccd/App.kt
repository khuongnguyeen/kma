package io.kma.results.readercccd

import androidx.multidex.MultiDexApplication
import com.google.firebase.FirebaseApp
import io.kma.results.readercccd.common.settings
import io.kma.results.readercccd.usecase.Logger
import io.reactivex.plugins.RxJavaPlugins
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security

class App : MultiDexApplication() {

    override fun onCreate() {
        handleUnhandledRxJavaErrors()
        applyTheme()
        super.onCreate()
        Security.insertProviderAt(BouncyCastleProvider(), 1)
        FirebaseApp.initializeApp(this.applicationContext)
    }

    private fun applyTheme() {
        settings.reapplyTheme()
    }

    private fun handleUnhandledRxJavaErrors() {
        RxJavaPlugins.setErrorHandler { error ->
            Logger.log(error)
        }
    }
}