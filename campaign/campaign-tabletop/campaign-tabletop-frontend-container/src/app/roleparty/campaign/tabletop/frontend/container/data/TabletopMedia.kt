package app.roleparty.campaign.tabletop.frontend.container.data

import dev.onvoid.webrtc.PeerConnectionFactory
import dev.onvoid.webrtc.media.MediaDevices
import dev.onvoid.webrtc.media.audio.AudioDeviceModule
import dev.onvoid.webrtc.media.audio.AudioOptions
import dev.onvoid.webrtc.media.audio.AudioTrackSource
import dev.onvoid.webrtc.media.video.CustomVideoSource
import io.ktor.client.webrtc.JvmWebRtc
import io.ktor.client.webrtc.WebRtcClient
import io.ktor.client.webrtc.WebRtcMedia
import io.ktor.client.webrtc.media.AudioFactory
import io.ktor.client.webrtc.media.CameraVideoFactory
import io.ktor.client.webrtc.media.JvmAudioTrack
import io.ktor.client.webrtc.media.JvmMediaDevices
import io.ktor.client.webrtc.media.JvmVideoTrack
import io.ktor.client.webrtc.media.VideoFactory
import io.ktor.utils.io.ExperimentalKtorApi
import java.util.UUID

private const val CAMERA_WIDTH = 640
private const val CAMERA_HEIGHT = 360
private const val CAMERA_FRAME_RATE = 24

@OptIn(ExperimentalKtorApi::class)
internal class TabletopMedia(
    audioFactory: AudioFactory = SystemAudioFactory(),
    videoFactory: VideoFactory = CameraVideoFactory(),
) : AutoCloseable {

    private val devices = JvmMediaDevices(audioFactory, videoFactory)

    val client = WebRtcClient(JvmWebRtc) { mediaTrackFactory = devices }

    val silence: WebRtcMedia.AudioTrack by lazy {
        val factory = devices.peerConnectionFactory
        JvmAudioTrack(factory.createAudioTrack("silence-${UUID.randomUUID()}", factory.createAudioSource(AudioOptions())))
    }

    val blank: WebRtcMedia.VideoTrack by lazy {
        JvmVideoTrack(devices.peerConnectionFactory.createVideoTrack("blank-${UUID.randomUUID()}", CustomVideoSource()))
    }

    var camera: WebRtcMedia.VideoTrack? = null
        private set

    var microphone: WebRtcMedia.AudioTrack? = null
        private set

    suspend fun openCamera(): WebRtcMedia.VideoTrack =
        client.createVideoTrack {
            width = CAMERA_WIDTH
            height = CAMERA_HEIGHT
            frameRate = CAMERA_FRAME_RATE
        }.also { camera = it }

    fun releaseCamera(): WebRtcMedia.VideoTrack? = camera.also { camera = null }

    suspend fun openMicrophone(): WebRtcMedia.AudioTrack = client.createAudioTrack().also { microphone = it }

    fun releaseMicrophone(): WebRtcMedia.AudioTrack? = microphone.also { microphone = null }

    override fun close() {
        releaseCamera()?.close()
        releaseMicrophone()?.close()
        client.close()
        devices.close()
    }
}

internal class SystemAudioFactory : AudioFactory {

    override val audioModule: AudioDeviceModule = AudioDeviceModule().apply {
        deviceOrNull { MediaDevices.getDefaultAudioCaptureDevice() }?.let(::setRecordingDevice)
        deviceOrNull { MediaDevices.getDefaultAudioRenderDevice() }?.let(::setPlayoutDevice)
    }

    override val peerConnectionFactory: PeerConnectionFactory = PeerConnectionFactory(audioModule)

    override fun createAudioSource(constraints: WebRtcMedia.AudioTrackConstraints): AudioTrackSource =
        peerConnectionFactory.createAudioSource(
            AudioOptions().apply {
                autoGainControl = constraints.autoGainControl ?: true
                echoCancellation = constraints.echoCancellation ?: true
                noiseSuppression = constraints.noiseSuppression ?: true
            }
        )

    override fun close() {
        audioModule.dispose()
        peerConnectionFactory.dispose()
    }

    private fun <T> deviceOrNull(find: () -> T?): T? =
        try {
            find()
        } catch (_: RuntimeException) {
            null
        }
}
