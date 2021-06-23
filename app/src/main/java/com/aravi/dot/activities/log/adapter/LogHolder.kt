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

import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aravi.dot.R
import de.hdodenhof.circleimageview.CircleImageView

/**
 * Created by Aravind Chowdary on
 */
class LogHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val item: RelativeLayout
    val appIcon: CircleImageView
    val logCamDot: ImageView
    val logMicDot: ImageView
    val logLocDot: ImageView
    val appName: TextView
    val appPackage: TextView
    val appTimestamp: TextView
    val cameraStart: View
    val cameraStop: View
    val micStart: View
    val micStop: View
    val locStart: View
    val locStop: View

    init {
        item = itemView.findViewById(R.id.logItem)
        appIcon = itemView.findViewById(R.id.logAppIcon)
        appName = itemView.findViewById(R.id.logAppName)
        appPackage = itemView.findViewById(R.id.logAppPackage)
        appTimestamp = itemView.findViewById(R.id.logTimestamp)
        logCamDot = itemView.findViewById(R.id.logCameraDot)
        logMicDot = itemView.findViewById(R.id.logMicDot)
        logLocDot = itemView.findViewById(R.id.logLocDot)
        cameraStart = itemView.findViewById(R.id.lineCameraStart)
        cameraStop = itemView.findViewById(R.id.lineCameraStop)
        micStart = itemView.findViewById(R.id.lineMicStart)
        micStop = itemView.findViewById(R.id.lineMicStop)
        locStart = itemView.findViewById(R.id.lineLocStart)
        locStop = itemView.findViewById(R.id.lineLocStop)
    }
}