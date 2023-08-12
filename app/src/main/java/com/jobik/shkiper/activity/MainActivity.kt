package com.jobik.shkiper.activity

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.jobik.shkiper.NotepadApplication
import com.jobik.shkiper.R
import com.jobik.shkiper.SharedPreferencesKeys
import com.jobik.shkiper.app_handlers.ThemePreferenceManager
import com.jobik.shkiper.database.models.NotePosition
import com.jobik.shkiper.services.localization.LocaleHelper
import com.jobik.shkiper.navigation.AppScreens
import com.jobik.shkiper.services.billing_service.BillingService
import com.jobik.shkiper.services.in_app_updates_service.InAppUpdatesService
import com.jobik.shkiper.services.review_service.ReviewService
import com.jobik.shkiper.services.statistics_service.StatisticsService
import com.jobik.shkiper.ui.components.modals.MainMenuBottomSheet
import com.jobik.shkiper.ui.components.modals.OfferWriteReview
import com.jobik.shkiper.ui.theme.CustomAppTheme
import com.jobik.shkiper.util.SnackbarHostUtil
import com.jobik.shkiper.util.SnackbarVisualsCustom
import com.jobik.shkiper.util.ThemeUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@ExperimentalAnimationApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var billingClientLifecycle: BillingService
    private lateinit var inAppUpdatesService: InAppUpdatesService

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(
            LocaleHelper.setLocale(newBase, NotepadApplication.currentLanguage)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Billing APIs are all handled in the this lifecycle observer.
        billingClientLifecycle = (application as NotepadApplication).billingClientLifecycle
        lifecycle.addObserver(billingClientLifecycle)
        inAppUpdatesService = InAppUpdatesService(this)

        ThemeUtil.theme = ThemePreferenceManager(this).getSavedUserTheme()
        val startDestination = getStartDestination()
        val canShowOfferReview = mutableStateOf(ReviewService(applicationContext).needShowOfferReview())

        checkForUpdates()

        setContent {
            CustomAppTheme(ThemeUtil.themeColors) {
                Box(
                    Modifier.fillMaxSize().background(CustomAppTheme.colors.mainBackground)
                ) {
                    MainMenuBottomSheet(startDestination)
                }
                if (canShowOfferReview.value)
                    OfferWriteReview { canShowOfferReview.value = false }
            }
        }
    }

    private val updateActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            // Handle the result of the update flow here.
            if (result.resultCode != Activity.RESULT_OK) {
                // If the update flow fails, you can retry it or notify the user.
                // Note: In a flexible update, the user has the option to postpone the update.
            }
        }

    private fun checkForUpdates() {
        if (InAppUpdatesService.isUpdatedChecked) return
        // Initialize the updateActivityResultLauncher.
        inAppUpdatesService.checkForUpdate(updateActivityResultLauncher)
    }

    override fun onResume() {
        super.onResume()
        billingClientLifecycle.queryProductPurchases()
        inAppUpdatesService.checkForDownloadedUpdate()
    }

    private fun getStartDestination(): String {
        val route = getNotificationRoute()
        if (route != null)
            return route
        return getOnboardingRoute(applicationContext) ?: AppScreens.NoteList.notePosition(NotePosition.MAIN.name)
    }

    private fun getOnboardingRoute(context: Context): String? {
        val sharedPreferences =
            context.getSharedPreferences(SharedPreferencesKeys.ApplicationStorageName, Context.MODE_PRIVATE)
        val isOnboardingPageFinished =
            sharedPreferences.getBoolean(SharedPreferencesKeys.IsOnboardingPageFinished, false)
        return if (isOnboardingPageFinished) null else AppScreens.Onboarding.route
    }

    private fun getNotificationRoute(): String? {
        // Retrieve the extras from the Intent
        val extras = intent.extras ?: return null
        val noteId = extras.getString(SharedPreferencesKeys.NoteIdExtra, null) ?: return null
        return AppScreens.Note.noteId(noteId)
    }
}
