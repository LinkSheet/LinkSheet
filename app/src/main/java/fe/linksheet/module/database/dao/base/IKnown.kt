package fe.linksheet.module.database.dao.base

import android.content.ContentValues

interface IKnown {
    fun toContentValues(): ContentValues
}
