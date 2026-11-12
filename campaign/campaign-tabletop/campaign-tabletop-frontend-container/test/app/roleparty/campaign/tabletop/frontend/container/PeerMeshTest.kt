package app.roleparty.campaign.tabletop.frontend.container

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toPixelMap
import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.tabletop.frontend.container.data.PeerMesh
import app.roleparty.campaign.tabletop.frontend.container.data.VideoFeed
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.floats.shouldBeGreaterThan
import io.kotest.matchers.floats.shouldBeLessThan
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

class PeerMeshTest : FunSpec({

    test("two people should connect, then see the camera of the one who turns it on, without negotiating again") {
        withContext(Dispatchers.Default) {
            val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
            val gameMaster = Seated(AccountId.generate(), scope)
            val player = Seated(AccountId.generate(), scope)
            gameMaster.hears(player)
            player.hears(gameMaster)

            try {
                player.reaching { call(gameMaster.accountId) }

                withTimeout(20.seconds) {
                    player.mesh.connected.first { gameMaster.accountId in it }
                    gameMaster.mesh.connected.first { player.accountId in it }
                }
                val negotiations = player.signalled + gameMaster.signalled
                gameMaster.feedOf(player.accountId).frame.value.shouldBeNull()

                player.media.openCamera()
                player.reaching { refreshTracks() }
                val painting = scope.launch {
                    while (true) {
                        player.camera.paintRed()
                        delay(50.milliseconds)
                    }
                }
                val seen = withTimeout(20.seconds) { gameMaster.feedOf(player.accountId).frame.first { it != null } }!!
                painting.cancel()

                seen.width shouldBe PaintedCamera.WIDTH
                seen.height shouldBe PaintedCamera.HEIGHT
                seen.centerColor().red shouldBeGreaterThan 0.7f
                seen.centerColor().blue shouldBeLessThan 0.3f
                seen.centerColor().green shouldBeLessThan 0.3f
                (player.signalled + gameMaster.signalled) shouldBe negotiations

                val camera = player.media.releaseCamera()
                player.reaching { refreshTracks() }
                camera?.close()
                gameMaster.reaching { hangUp(player.accountId) }
                gameMaster.mesh.connected.value shouldBe emptySet()
                withTimeout(5.seconds) { gameMaster.feedOf(player.accountId).frame.first { it == null } }
            } finally {
                player.reaching { hangUpAll() }
                gameMaster.reaching { hangUpAll() }
                scope.cancel()
                player.media.close()
                gameMaster.media.close()
            }
        }
    }
})

private fun ImageBitmap.centerColor() = toPixelMap()[width / 2, height / 2]

private class Seated(val accountId: AccountId, private val scope: CoroutineScope) {

    val camera = PaintedCamera()
    val media = headlessMedia(camera)
    private val feeds = ConcurrentHashMap<AccountId, VideoFeed>()
    private val mutex = Mutex()
    private var other: Seated? = null

    @Volatile
    var signalled = 0
        private set

    val mesh: PeerMesh = PeerMesh(media, emptyList(), scope, ::feedOf) { _, signal ->
        signalled++
        val receiver = checkNotNull(other)
        scope.launch { receiver.reaching { receive(accountId, signal) } }
    }

    fun hears(other: Seated) {
        this.other = other
    }

    fun feedOf(accountId: AccountId): VideoFeed = feeds.computeIfAbsent(accountId) { VideoFeed(scope, Dispatchers.Default) }

    suspend fun reaching(act: suspend PeerMesh.() -> Unit): Unit = mutex.withLock { mesh.act() }
}
