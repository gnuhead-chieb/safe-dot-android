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
package com.aravi.dot.activities.log.adapter

import android.content.Context
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aravi.dot.R
import com.aravi.dot.model.Logs
import com.aravi.dot.util.Utils.convertSecondsToHMmSs
import com.aravi.dot.util.Utils.getNameFromPackageName

class LogAdapter(private val context: Context, private val logsList: List<Logs>) :
    RecyclerView.Adapter<LogHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogHolder {
        val inflatedView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_log, parent, false)
        return LogHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: LogHolder, position: Int) {
        val item = logsList!![position]
        holder.appName.text = getNameFromPackageName(context, item.packageName)
        holder.appPackage.text = item.packageName
        try {
            val icon = context.packageManager.getApplicationIcon(item.packageName)
            holder.appIcon.setImageDrawable(icon)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        holder.appTimestamp.text = convertSecondsToHMmSs(item.timestamp)
        when (item.camera_state) {
            0 -> {
                holder.cameraStart.visibility = View.INVISIBLE
                holder.cameraStop.visibility = View.INVISIBLE
                holder.logCamDot.visibility = View.INVISIBLE
            }
            1 -> {
                holder.logCamDot.visibility = View.VISIBLE
                holder.cameraStart.visibility = View.VISIBLE
                holder.cameraStop.visibility = View.INVISIBLE
            }
            2 -> {
                holder.logCamDot.visibility = View.VISIBLE
                holder.cameraStart.visibility = View.INVISIBLE
                holder.cameraStop.visibility = View.VISIBLE
            }
        }
        when (item.mic_state) {
            0 -> {
                holder.micStart.visibility = View.INVISIBLE
                holder.micStop.visibility = View.INVISIBLE
                holder.logMicDot.visibility = View.INVISIBLE
            }
            1 -> {
                holder.logMicDot.visibility = View.VISIBLE
                holder.micStart.visibility = View.VISIBLE
                holder.micStop.visibility = View.INVISIBLE
            }
            2 -> {
                holder.logMicDot.visibility = View.VISIBLE
                holder.micStart.visibility = View.INVISIBLE
                holder.micStop.visibility = View.VISIBLE
            }
        }
        when (item.loc_state) {
            0 -> {
                holder.locStart.visibility = View.INVISIBLE
                holder.locStop.visibility = View.INVISIBLE
                holder.logLocDot.visibility = View.INVISIBLE
            }
            1 -> {
                holder.logLocDot.visibility = View.VISIBLE
                holder.locStart.visibility = View.VISIBLE
                holder.locStop.visibility = View.INVISIBLE
            }
            2 -> {
                holder.logLocDot.visibility = View.VISIBLE
                holder.locStart.visibility = View.INVISIBLE
                holder.locStop.visibility = View.VISIBLE
            }
        }
    }

    override fun getItemCount(): Int {
        return logsList?.size ?: 0
    }
}