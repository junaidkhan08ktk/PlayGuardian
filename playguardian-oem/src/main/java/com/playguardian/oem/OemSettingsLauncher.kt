package com.playguardian.oem

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.util.Log

/**
 * Attempts to launch one of the provided settings [Intent]s by iterating the list in order
 * and starting the first activity that can be resolved on the current device.
 *
 * This class is deliberately defensive: it catches all launch exceptions and continues
 * through the fallback chain rather than propagating errors to the caller.
 */
object OemSettingsLauncher {

    private const val TAG = "OemSettingsLauncher"

    /**
     * Tries to start an Activity for each intent in [intents], in the order provided.
     *
     * @param activity The current foreground [Activity] used to start settings screens.
     * @param intents  Ordered list of candidate intents (most specific first).
     * @return `true` if at least one intent was successfully started; `false` if none resolved.
     */
    fun launch(activity: Activity, intents: List<Intent>): Boolean {
        for (intent in intents) {
            if (tryLaunch(activity, intent)) {
                return true
            }
        }
        Log.w(TAG, "No resolvable settings intent found after ${intents.size} attempt(s).")
        return false
    }

    private fun tryLaunch(activity: Activity, intent: Intent): Boolean {
        return try {
            val resolves = activity.packageManager.resolveActivity(intent, 0) != null
            if (!resolves) {
                Log.d(TAG, "Intent not resolvable: ${intent.action} data=${intent.data}")
                return false
            }
            activity.startActivity(intent)
            true
        } catch (e: ActivityNotFoundException) {
            Log.d(TAG, "ActivityNotFoundException for intent: ${intent.action}", e)
            false
        } catch (e: SecurityException) {
            Log.d(TAG, "SecurityException for intent: ${intent.action}", e)
            false
        } catch (e: Exception) {
            Log.d(TAG, "Unexpected exception launching intent: ${intent.action}", e)
            false
        }
    }
}
