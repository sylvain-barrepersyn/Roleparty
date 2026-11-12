package app.roleparty.campaign.tabletop.frontend.container

import app.roleparty.campaign.tabletop.frontend.container.data.TabletopMedia
import dev.onvoid.webrtc.PeerConnectionFactory
import dev.onvoid.webrtc.media.audio.AudioOptions
import dev.onvoid.webrtc.media.audio.AudioTrackSource
import dev.onvoid.webrtc.media.audio.HeadlessAudioDeviceModule
import dev.onvoid.webrtc.media.video.CustomVideoSource
import dev.onvoid.webrtc.media.video.NativeI420Buffer
import dev.onvoid.webrtc.media.video.VideoFrame
import io.ktor.client.webrtc.WebRtcMedia
import io.ktor.client.webrtc.media.AudioFactory
import io.ktor.client.webrtc.media.VideoCapturer
import io.ktor.client.webrtc.media.VideoFactory

internal class HeadlessAudioFactory : AudioFactory {

    override val audioModule = HeadlessAudioDeviceModule()

    override val peerConnectionFactory = PeerConnectionFactory(audioModule)

    override fun createAudioSource(constraints: WebRtcMedia.AudioTrackConstraints): AudioTrackSource =
        peerConnectionFactory.createAudioSource(AudioOptions())

    override fun close() {
        audioModule.dispose()
        peerConnectionFactory.dispose()
    }
}

internal class PaintedCamera : VideoFactory {

    private val source by lazy { CustomVideoSource() }
    var opened = 0
        private set
    var available = true

    override fun createVideoCapturer(constraints: WebRtcMedia.VideoTrackConstraints): VideoCapturer {
        if (!available) throw WebRtcMedia.DeviceException("No suitable video device found.")
        opened++
        return object : VideoCapturer {
            override val source = this@PaintedCamera.source
            override fun start() = Unit
            override fun stop() = Unit
            override fun close() = Unit
        }
    }

    fun paintRed() {
        val buffer = NativeI420Buffer.allocate(WIDTH, HEIGHT)
        buffer.dataY.fill(RED_Y)
        buffer.dataU.fill(RED_U)
        buffer.dataV.fill(RED_V)
        val frame = VideoFrame(buffer, System.nanoTime())
        source.pushFrame(frame)
        frame.release()
    }

    private fun java.nio.ByteBuffer.fill(value: Int) {
        while (hasRemaining()) put(value.toByte())
    }

    companion object {
        const val WIDTH = 320
        const val HEIGHT = 180
        private const val RED_Y = 81
        private const val RED_U = 90
        private const val RED_V = 240
    }
}

internal fun headlessMedia(camera: PaintedCamera = PaintedCamera()) = TabletopMedia(HeadlessAudioFactory(), camera)
