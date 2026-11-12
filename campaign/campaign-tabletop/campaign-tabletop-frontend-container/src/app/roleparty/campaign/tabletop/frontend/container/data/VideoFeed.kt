package app.roleparty.campaign.tabletop.frontend.container.data

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asComposeImageBitmap
import dev.onvoid.webrtc.media.FourCC
import dev.onvoid.webrtc.media.video.VideoBufferConverter
import dev.onvoid.webrtc.media.video.VideoFrame
import dev.onvoid.webrtc.media.video.VideoTrack
import dev.onvoid.webrtc.media.video.VideoTrackSink
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.ImageInfo

private const val BYTES_PER_PIXEL = 4

internal class VideoFeed(private val scope: CoroutineScope, private val display: CoroutineDispatcher) {

    private val mutableFrame = MutableStateFlow<ImageBitmap?>(null)
    val frame: StateFlow<ImageBitmap?> = mutableFrame.asStateFlow()

    private val converting = AtomicBoolean(false)
    private val sink = VideoTrackSink(::receive)
    private var track: VideoTrack? = null
    private var pixels = ByteArray(0)
    private var shown: Bitmap? = null
    private var previous: Bitmap? = null

    @Synchronized
    fun show(track: VideoTrack) {
        this.track?.removeSink(sink)
        this.track = track
        track.addSink(sink)
    }

    @Synchronized
    fun release() {
        track?.removeSink(sink)
        track = null
        clear()
    }

    fun clear() {
        scope.launch(display) { display(null) }
    }

    private fun receive(frame: VideoFrame) {
        if (!converting.compareAndSet(false, true)) return
        try {
            val width = frame.buffer.width
            val height = frame.buffer.height
            if (pixels.size != width * height * BYTES_PER_PIXEL) pixels = ByteArray(width * height * BYTES_PER_PIXEL)
            VideoBufferConverter.convertFromI420(frame.buffer, pixels, FourCC.ARGB)
            scope.launch(display) {
                try {
                    val info = ImageInfo(width, height, ColorType.BGRA_8888, ColorAlphaType.OPAQUE)
                    display(Bitmap().apply { installPixels(info, pixels, info.minRowBytes) })
                } finally {
                    converting.set(false)
                }
            }
        } catch (_: Exception) {
            converting.set(false)
        }
    }

    private fun display(bitmap: Bitmap?) {
        bitmap?.setImmutable()
        mutableFrame.value = bitmap?.asComposeImageBitmap()
        previous?.close()
        previous = shown
        shown = bitmap
    }
}
