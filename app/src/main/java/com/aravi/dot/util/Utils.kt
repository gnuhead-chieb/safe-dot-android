/*
 * Copyright (C) 2021.  Aravind Chowdary (@kamaravichow)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.aravi.dot.util

import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.*

object Utils {
    /**
     * @param input
     * @return
     */
    fun capitalizeFirstLetterOfString(input: String): String {
        return if (input.isEmpty()) "" else input.substring(0, 1).toUpperCase() + input.substring(1)
    }

    /**
     * @param context
     */
    @JvmStatic
    fun showAutoStartDialog(context: Context, manufacturer: String) {
        MaterialAlertDialogBuilder(context)
            .setTitle("Enable AutoStart ")
            .setMessage(manufacturer.toUpperCase() + " devices will kill the useful services to free up ram. You're required to provide the auto start permission to the app to keep app running as expected. ")
            .setPositiveButton("Setup Now") { dialog: DialogInterface?, which: Int ->
                openAutoStartAccordingToManufacturer(
                    context
                )
            }
            .setCancelable(true)
            .show()
    }

    /**
     * @param context
     */
    private fun openAutoStartAccordingToManufacturer(context: Context) {
        try {
            val intent = Intent()
            val manufacturer = Build.MANUFACTURER
            if ("xiaomi".equals(manufacturer, ignoreCase = true)) {
                intent.component = ComponentName(
                    "com.miui.securitycenter",
                    "com.miui.permcenter.autostart.AutoStartManagementActivity"
                )
            } else if ("oppo".equals(manufacturer, ignoreCase = true)) {
                intent.component = ComponentName(
                    "com.coloros.safecenter",
                    "com.coloros.safecenter.permission.startup.StartupAppListActivity"
                )
            } else if ("vivo".equals(manufacturer, ignoreCase = true)) {
                intent.component = ComponentName(
                    "com.vivo.permissionmanager",
                    "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
                )
            } else if ("Honor".equals(manufacturer, ignoreCase = true)) {
                intent.component = ComponentName(
                    "com.huawei.systemmanager",
                    "com.huawei.systemmanager.optimize.process.ProtectActivity"
                )
            }
            val list = context.packageManager.queryIntentActivities(
                intent,
                PackageManager.MATCH_DEFAULT_ONLY
            )
            if (list.size > 0) {
                context.startActivity(intent)
            }
        } catch (e: Exception) {
            Log.i("UTILS", "openAutoStartAccordingToManufacturer: " + e.message)
        }
    }

    /**
     * @param context
     * @param packageName
     * @return
     */
    @JvmStatic
    fun getNameFromPackageName(context: Context, packageName: String?): String {
        val packageManager = context.packageManager
        val applicationInfo: ApplicationInfo?
        applicationInfo = try {
            packageManager.getApplicationInfo(packageName!!, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
        return (if (applicationInfo != null) packageManager.getApplicationLabel(applicationInfo) else "(unknown)") as String
    }

    /**
     * @param millis
     * @return
     */
    @JvmStatic
    fun convertSecondsToHMmSs(millis: Long): String {
        val date = Date(millis)
        val cal = Calendar.getInstance()
        val formatter = SimpleDateFormat("HH:mm:ss")
        val fmt = SimpleDateFormat("yyyy-MM-dd")
        formatter.timeZone = cal.timeZone
        return formatter.format(date)
    }
}