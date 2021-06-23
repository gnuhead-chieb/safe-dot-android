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

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.aravi.dot.model.Logs
import java.util.concurrent.Executors

/**
 * Created by Aravind Chowdary on
 */
@Database(entities = [Logs::class], version = 3, exportSchema = true)
abstract class LogsRoomDatabase : RoomDatabase() {
    abstract fun logsDao(): LogsDao?

    companion object {
        private const val NUMBER_OF_THREADS = 4

        @Volatile
        private var INSTANCE: LogsRoomDatabase? = null
        val databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS)

        /**
         * @param context
         * @return
         */
        fun getDatabase(context: Context): LogsRoomDatabase? {
            if (INSTANCE == null) {
                synchronized(LogsRoomDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            LogsRoomDatabase::class.java, "logsfree"
                        )
                            .addCallback(sRoomDatabaseCallback)
                            .build()
                    }
                }
            }
            return INSTANCE
        }

        private val sRoomDatabaseCallback: Callback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
            }
        }
    }
}