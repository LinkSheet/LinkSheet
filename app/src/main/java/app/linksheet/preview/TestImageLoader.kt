package app.linksheet.preview

import android.content.Context
import coil3.ImageLoader
import coil3.test.FakeImageLoaderEngine


fun TestImageLoader(context: Context, block: FakeImageLoaderEngine.Builder.() -> Unit): ImageLoader {
    val engine = FakeImageLoaderEngine.Builder().apply(block).build()
    val imageLoader = ImageLoader.Builder(context)
        .components { add(engine) }
        .build()

    return imageLoader
}
