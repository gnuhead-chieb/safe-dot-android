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
package com.aravi.dot.activities.log.database

import android.app.Application
import androidx.lifecycle.LiveData
import com.aravi.dot.model.Logs

/**
 * Created by Aravind Chowdary on
 */
class LogsRepository(application: Application?) {
    private lateinit var logsDao: LogsDao // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    /**
     * @return
     */
    lateinit var logs: LiveData<List<Logs?>?>

    /**
     * @param logs
     */
    fun insertLog(logs: Logs?) {
        // You must call this on a non-UI thread or your app will throw an exception. Room ensures
        // that you're not doing any long running operations on the main thread, blocking the UI.
        LogsRoomDatabase.databaseWriteExecutor.execute { logsDao.insert(logs) }
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    fun clearLogs() {
        LogsRoomDatabase.databaseWriteExecutor.execute { logsDao.clearAllLogs() }
    }

    /**
     * @param application
     */
    init {
        val db = application?.let { LogsRoomDatabase.getDatabase(it) }
        if (db != null) {
            logsDao = db.logsDao()!!
            logs = logsDao.orderedLogs!!
        }
    }
}