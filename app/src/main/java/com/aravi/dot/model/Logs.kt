package com.aravi.dot.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "logs_database")
class Logs(
    @field:PrimaryKey var timestamp: Long,
    var packageName: String,
    var camera_state: Int,
    var mic_state: Int,
    var loc_state: Int
) : Serializable