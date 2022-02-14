package org.ireader.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.ireader.core.utils.Constants

@Entity(tableName = Constants.PAGE_KET_TABLE)
data class RemoteKeys(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val sourceId: Long,
    val prevPage: Int?,
    val nextPage: Int?,
)