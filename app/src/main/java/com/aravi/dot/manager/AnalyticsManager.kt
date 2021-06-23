/*
 * Copyright (c) 2021. Aravind Chowdary
 */
package com.aravi.dot.manager

import android.app.Activity
import android.content.Context
import android.os.Bundle
import com.google.android.gms.tasks.Task
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics

class AnalyticsManager(context: Context?) {
    val analytics: FirebaseAnalytics
    private val crashlytics: FirebaseCrashlytics

    /**
     * Initialisation method
     */
    private fun init() {
        analytics.setAnalyticsCollectionEnabled(true)
        crashlytics.setCrashlyticsCollectionEnabled(true)
    }

    /**
     * Activity log method
     * this data is used to understand the behaviour of usage
     * helps understand what features users use the most
     */
    fun setActivity(activity: Activity) {
        val activityLog = Bundle()
        activityLog.putString(FirebaseAnalytics.Param.SCREEN_NAME, activity.title.toString())
        activityLog.putString(FirebaseAnalytics.Param.SCREEN_CLASS, activity.localClassName)
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, activityLog)
    }

    /**
     * Gets the permission state
     *
     * @param permission
     * @param state
     */
    fun setPermissionStatus(permission: String?, state: Boolean) {
        val bPermission = Bundle()
        bPermission.putString("PERMISSION_NAME", permission)
        bPermission.putBoolean("PERMISSION_STATE", state)
        analytics.logEvent("PERMISSION_EVENT", bPermission)
    }

    /**
     * resets all the analytics data
     * use only in bad cases
     */
    fun resetAnalytics() {
        analytics.resetAnalyticsData()
    }

    /**
     * Record exeption
     * this method used to record exeptions or errors in the app at runtime
     */
    fun exception(e: Exception?) {
        crashlytics.recordException(e!!)
    }

    /**
     * this makes a log in crashylitics
     */
    fun makeCrashLog(log: String?) {
        crashlytics.log(log!!)
    }

    /**
     * Past check method
     * this checks if there are any unsent reports and sends them if exists
     * unsent can be caused by loosing internet etc,...
     */
    fun pastCheck() {
        crashlytics.checkForUnsentReports().addOnCompleteListener { task: Task<Boolean> ->
            if (task.result!!) {
                crashlytics.sendUnsentReports()
            }
        }
    }

    /**
     * checks if the app is crashed in last session
     */
    fun crashed(): Boolean {
        return crashlytics.didCrashOnPreviousExecution()
    }

    companion object {
        private var instance: AnalyticsManager? = null

        /**
         * Analytics manager initialisation
         *
         * @param context
         * @return
         */
        @JvmStatic
        fun getInstance(context: Context?): AnalyticsManager? {
            if (instance == null) {
                instance = AnalyticsManager(context)
            }
            return instance
        }
    }

    init {
        analytics = FirebaseAnalytics.getInstance(context!!)
        crashlytics = FirebaseCrashlytics.getInstance()
        init()
    }
}