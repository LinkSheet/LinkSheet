package fe.linksheet.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Build
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

object ImageFactory {
    private val defaultConfig = Bitmap.Config.ARGB_8888
    private val options = BitmapFactory.Options().apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            outConfig = defaultConfig
        }
    }

    fun convertToBitmap(
        drawable: Drawable,
        width: Int = drawable.intrinsicWidth,
        height: Int = drawable.intrinsicHeight,
        config: Bitmap.Config = defaultConfig,
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, config)
        val canvas = Canvas(bitmap)

        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }

    fun convertToBitmap(bytes: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
    }

    fun compress(
        bitmap: Bitmap,
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.WEBP,
        quality: Int = 100,
    ): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(format, quality, stream)

        return stream.toByteArray()
    }

    fun hash(bitmap: Bitmap): Int {
        val buffer = ByteBuffer.allocate(bitmap.height * bitmap.rowBytes)
        bitmap.copyPixelsToBuffer(buffer)
        buffer.rewind()

        return buffer.hashCode()
    }
}
