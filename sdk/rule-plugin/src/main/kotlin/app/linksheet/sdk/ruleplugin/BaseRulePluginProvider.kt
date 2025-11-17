package app.linksheet.sdk.ruleplugin

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import app.linksheet.sdk.Message
import app.linksheet.sdk.PluginMethod
import app.linksheet.sdk.PostRuleMessage
import app.linksheet.sdk.PreRuleMessage
import kotlin.reflect.KClass

abstract class BaseRulePluginProvider(
    private vararg val handlers: PluginMethod<*>
) : ContentProvider() {
    abstract fun handleMessage(message: Message): Message?

    override fun call(method: String, arg: String?, extras: Bundle?): Bundle? {
        val handler = handlers.firstOrNull { it.name == method }
        if(handler == null) return null

        val message = extras?.getParcelable("message", handler.input.java) ?: return null
        val result = handleMessage(message)

        return null
    }

    override fun onCreate(): Boolean = true
    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? = null

    override fun getType(uri: Uri): String? = null
    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int = 0
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int = 0
}
