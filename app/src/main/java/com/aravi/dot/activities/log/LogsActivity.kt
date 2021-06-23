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
package com.aravi.dot.activities.log

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.aravi.dot.R
import com.aravi.dot.activities.log.adapter.LogAdapter
import com.aravi.dot.databinding.ActivityLogsBinding
import com.aravi.dot.model.Logs
import com.google.android.material.snackbar.Snackbar
import java.util.*

class LogsActivity : AppCompatActivity() {
    private var mBinding: ActivityLogsBinding? = null
    private var mLogsViewModel: LogsViewModel? = null
    private var logsList: List<Logs>? = null
    private var adapter: LogAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityLogsBinding.inflate(layoutInflater)
        setContentView(mBinding!!.root)
        mLogsViewModel = ViewModelProviders.of(this).get(LogsViewModel::class.java)
        setSupportActionBar(mBinding!!.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        init()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onStart() {
        // Hides the clear button and shows progress bar on start
        mBinding!!.clearLogsButton.hide()
        mBinding!!.progressBar.visibility = View.VISIBLE
        super.onStart()
    }

    private fun init() {
        // Creates a new array-list object on initialisation
        logsList = ArrayList()

        // I've set the linear layout manager in the layout file
        mBinding!!.logsRecyclerView.layoutManager = LinearLayoutManager(this)

        // get all the log list live data from the view model
        mLogsViewModel!!.getmLogsList().observe(this, { logs: List<Logs?> ->
            mBinding!!.progressBar.visibility = View.INVISIBLE
            adapter = LogAdapter(this@LogsActivity, logs as List<Logs>)
            mBinding!!.logsRecyclerView.adapter = adapter
            if (logs.isEmpty()) {
                mBinding!!.clearLogsButton.hide()
                findViewById<View>(R.id.emptyListImage).visibility = View.VISIBLE
            } else {
                mBinding!!.clearLogsButton.show()
                mBinding!!.clearLogsButton.setOnClickListener { v: View? ->
                    mLogsViewModel!!.clearLogs()
                    showSnackBar("Logs Cleared")
                }
                mBinding!!.emptyListImage.visibility = View.INVISIBLE
            }
        } as (List<Logs?>?) -> Unit)
    }

    /**
     * Snack-bar Method
     * Gets String As Input
     */
    private fun showSnackBar(message: String) {
        Snackbar.make(mBinding!!.root, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        finish()
    }
}