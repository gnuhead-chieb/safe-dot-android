/*
 * Copyright (C) 2020.  Aravind Chowdary (@kamaravichow)
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
package com.aravi.dot.activities.main

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils.SimpleStringSplitter
import android.view.View
import android.view.accessibility.AccessibilityManager
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.aravi.dot.BuildConfig
import com.aravi.dot.R
import com.aravi.dot.activities.custom.CustomisationActivity
import com.aravi.dot.activities.log.LogsActivity
import com.aravi.dot.activities.main.MainActivity
import com.aravi.dot.databinding.ActivityMainBinding
import com.aravi.dot.manager.PreferenceManager
import com.aravi.dot.manager.PreferenceManager.Companion.getInstance
import com.aravi.dot.service.DotService
import com.aravi.dot.util.Utils.showAutoStartDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private var TRIGGERED_START = false
    private var sharedPreferenceManager: PreferenceManager? = null
    private var serviceIntent: Intent? = null
    private var mBinding: ActivityMainBinding? = null
    override fun onStart() {
        super.onStart()
        if (!sharedPreferenceManager!!.isServiceEnabled) {
            mBinding!!.mainSwitch.isChecked = checkAccessibility()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding!!.root)
        sharedPreferenceManager = getInstance(application)
        loadFromPrefs()
        init()
        checkAutoStartRequirement()
    }

    private fun loadFromPrefs() {
        mBinding!!.vibrationSwitch.isChecked = sharedPreferenceManager!!.isVibrationEnabled
        mBinding!!.locationSwitch.isChecked = sharedPreferenceManager!!.isLocationEnabled
        mBinding!!.mainSwitch.isChecked = sharedPreferenceManager!!.isServiceEnabled
    }

    private fun init() {
        mBinding!!.locationSwitch.setOnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                if (ActivityCompat.checkSelfPermission(
                        this@MainActivity,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    MaterialAlertDialogBuilder(this@MainActivity)
                        .setIcon(
                            ContextCompat.getDrawable(
                                this@MainActivity,
                                R.drawable.ic_round_location
                            )
                        )
                        .setTitle("Requires Location Permission")
                        .setMessage("This features requires LOCATION PERMISSION to work as expected\n\nNOTE: This app doesn't have permission to connect to internet so your data is safe on your device.")
                        .setNeutralButton("Later") { dialog: DialogInterface?, which: Int ->
                            mBinding!!.locationSwitch.isChecked = false
                        }
                        .setPositiveButton("Continue") { dialog: DialogInterface?, which: Int ->
                            askPermission(
                                Manifest.permission.ACCESS_FINE_LOCATION
                            )
                        }
                        .show()
                } else {
                    sharedPreferenceManager!!.isLocationEnabled = true
                }
            }
        }
        mBinding!!.vibrationSwitch.setOnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            sharedPreferenceManager!!.isVibrationEnabled = isChecked
        }
        mBinding!!.mainSwitch.setOnCheckedChangeListener { buttonView: CompoundButton?, b: Boolean ->
            TRIGGERED_START = if (b) {
                checkForAccessibilityAndStart()
                true
            } else {
                stopService()
                false
            }
        }
        mBinding!!.twitterButton.setOnClickListener { v: View? -> openWeb("https://www.twitter.com/kamaravichow") }
        mBinding!!.githubButton.setOnClickListener { v: View? -> openWeb("https://www.github.com/kamaravichow") }
        mBinding!!.logsOption.setOnClickListener { view: View? ->
            val intent = Intent(this@MainActivity, LogsActivity::class.java)
            startActivity(intent)
        }
        mBinding!!.customisationOption.setOnClickListener { v: View? ->
            startActivity(
                Intent(
                    this@MainActivity,
                    CustomisationActivity::class.java
                )
            )
        }
        mBinding!!.shareOption.setOnClickListener { v: View? ->
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(
                Intent.EXTRA_TEXT,
                "Protect your camera and microphone privacy with SafeDot. Download from Google Play : https://play.google.com/store/apps/details?id=com.aravi.dotpro"
            )
            shareIntent.type = "text/plain"
            startActivity(shareIntent)
        }
        mBinding!!.versionText.text = "Version - " + BuildConfig.VERSION_NAME
    }

    private fun checkForAccessibilityAndStart() {
        if (!accessibilityPermission(applicationContext, DotService::class.java)) {
            mBinding!!.mainSwitch.isChecked = false
            MaterialAlertDialogBuilder(this)
                .setTitle("Requires Accessibility Permission")
                .setMessage("You're required to enable accessibility permission to Safe Dot Pro to enable the safe dots")
                .setIcon(R.drawable.ic_baseline_accessibility_24)
                .setPositiveButton("Open Accessibility") { dialog: DialogInterface?, which: Int ->
                    startActivity(
                        Intent("android.settings.ACCESSIBILITY_SETTINGS")
                    )
                }
                .setNegativeButton("Cancel", null)
                .setCancelable(true)
                .show()
        } else {
            mBinding!!.mainSwitch.isChecked = true
            sharedPreferenceManager!!.isServiceEnabled = true
            serviceIntent = Intent(this@MainActivity, DotService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent)
            } else {
                startService(serviceIntent)
            }
        }
    }

    private fun stopService() {
        if (isAccessibilityServiceRunning) {
//            sharedPreferenceManager.setServiceEnabled(false);
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
            Toast.makeText(this, getString(R.string.close_app_note), Toast.LENGTH_SHORT).show()
        }
    }
    //    private void sendFeedbackEmail() {
    //        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", getString(R.string.feedback_email_address), null));
    //        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_subject));
    //        emailIntent.putExtra(Intent.EXTRA_TEXT, "Device Information : \n----- Don't clear these ----\n " + Build.DEVICE + " ,\n " + Build.BOARD + " ,\n " + Build.BRAND + " , " + Build.MANUFACTURER + " ,\n " + Build.MODEL + "\n ------ ");
    //        startActivity(Intent.createChooser(emailIntent, "Send feedback..."));
    //    }
    /**
     * @return
     */
    private fun checkAccessibility(): Boolean {
        val manager = getSystemService(ACCESSIBILITY_SERVICE) as AccessibilityManager
        return manager.isEnabled
    }

    /**
     * @return
     */
    private val isAccessibilityServiceRunning: Boolean
        private get() {
            val prefString = Settings.Secure.getString(
                this.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            return prefString != null && prefString.contains(this.packageName + "/" + DotService::class.java.name)
        }

    /**
     * @param message
     */
    private fun showSnack(message: String) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show()
    }

    /**
     * @param url
     */
    private fun openWeb(url: String) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        startActivity(i)
    }

    /**
     * Asks permission runtime
     *
     * @param permission
     */
    private fun askPermission(permission: String) {
        if (ContextCompat.checkSelfPermission(this, permission) != 0) {
            requestPermissions(arrayOf(permission), 0)
            sharedPreferenceManager!!.isLocationEnabled = true
        }
    }

    /**
     * Chinese ROM's kill the app services frequently so AutoStart Permission is required
     */
    private fun checkAutoStartRequirement() {
        val manufacturer = Build.MANUFACTURER
        if (sharedPreferenceManager!!.isFirstLaunch) {
            if ("xiaomi".equals(manufacturer, ignoreCase = true)
                || "oppo".equals(manufacturer, ignoreCase = true)
                || "vivo".equals(manufacturer, ignoreCase = true)
                || "Honor".equals(manufacturer, ignoreCase = true)
            ) {
                showAutoStartDialog(this@MainActivity, manufacturer)
                sharedPreferenceManager!!.setFirstLaunch()
            }
        }
    }

    override fun onPostResume() {
        super.onPostResume()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {
        private const val MY_REQUEST_CODE = 1802
        //    @Override
        //    protected void onResume() {
        //        super.onResume();
        //        if (TRIGGERED_START) {
        //            TRIGGERED_START = false;
        //            checkForAccessibilityAndStart();
        //        }
        //        if (!sharedPreferenceManager.isServiceEnabled()) {
        //            mBinding.mainSwitch.setChecked(checkAccessibility());
        //        }
        //    }
        /**
         * @param context
         * @param cls
         * @return
         */
        fun accessibilityPermission(context: Context, cls: Class<*>?): Boolean {
            val componentName = ComponentName(context, cls!!)
            val string = Settings.Secure.getString(
                context.contentResolver,
                "enabled_accessibility_services"
            )
                ?: return false
            val simpleStringSplitter = SimpleStringSplitter(':')
            simpleStringSplitter.setString(string)
            while (simpleStringSplitter.hasNext()) {
                val unflattenFromString =
                    ComponentName.unflattenFromString(simpleStringSplitter.next())
                if (unflattenFromString != null && unflattenFromString == componentName) {
                    return true
                }
            }
            return false
        }
    }
}