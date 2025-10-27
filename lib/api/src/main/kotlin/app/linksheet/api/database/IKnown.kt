package app.linksheet.api.database

import android.content.ContentValues

interface IKnown {
    fun toContentValues(): ContentValues
}
