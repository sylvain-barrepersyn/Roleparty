package app.roleparty.media.frontend.container

import app.roleparty.core.commons.Outcome
import app.roleparty.media.commons.ImageFormat
import app.roleparty.media.commons.ImageId
import app.roleparty.media.frontend.component.ImageFieldFailure
import app.roleparty.media.frontend.container.data.MediaApi
import app.roleparty.media.frontend.container.data.MediaFailure
import app.roleparty.media.frontend.container.imagefield.ImageFieldAction
import app.roleparty.media.frontend.container.imagefield.ImageFieldEvent
import app.roleparty.media.frontend.container.imagefield.ImageFieldState
import app.roleparty.media.frontend.container.imagefield.ImageFieldViewModel
import io.github.vinceglb.filekit.PlatformFile
import io.kotest.assertions.nondeterministic.eventually
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.io.File
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.yield

private class FakeMediaApi(var outcome: Outcome<ImageId, MediaFailure>) : MediaApi {

    val uploads = mutableListOf<Pair<ByteArray, ImageFormat>>()

    override suspend fun upload(content: ByteArray, format: ImageFormat): Outcome<ImageId, MediaFailure> {
        uploads += content to format
        return outcome
    }

    override fun imageUrl(imageId: ImageId) = "http://images/$imageId"
}

@OptIn(ExperimentalCoroutinesApi::class)
class ImageFieldViewModelTest : FunSpec({

    beforeTest { Dispatchers.setMain(UnconfinedTestDispatcher()) }
    afterTest { Dispatchers.resetMain() }

    fun file(extension: String, content: ByteArray = byteArrayOf(1, 2, 3)) =
        PlatformFile(File.createTempFile("image", ".$extension").apply { writeBytes(content); deleteOnExit() })

    test("a picked JPEG file should be uploaded and reported") {
        val imageId = ImageId.generate()
        val api = FakeMediaApi(Outcome.Success(imageId))
        val viewModel = ImageFieldViewModel(api)
        val event = async { viewModel.events.first() }
        yield()

        viewModel.handle(ImageFieldAction.Upload(file("jpg", byteArrayOf(9, 8, 7))))

        event.await() shouldBe ImageFieldEvent.ImageUploaded(imageId)
        viewModel.state.value shouldBe ImageFieldState.Idle
        api.uploads.single().first.contentEquals(byteArrayOf(9, 8, 7)) shouldBe true
        api.uploads.single().second shouldBe ImageFormat.JPEG
    }

    test("a refusal of the backend should be shown") {
        val api = FakeMediaApi(Outcome.Failure(MediaFailure.Rejected("media.invalid-image", "Not an image.")))
        val viewModel = ImageFieldViewModel(api)

        viewModel.handle(ImageFieldAction.Upload(file("jpeg")))

        eventually(5.seconds) {
            viewModel.state.value shouldBe ImageFieldState.Failed(ImageFieldFailure.Rejected("Not an image."))
        }
    }

    test("an unreachable backend should be shown") {
        val viewModel = ImageFieldViewModel(FakeMediaApi(Outcome.Failure(MediaFailure.Unreachable)))

        viewModel.handle(ImageFieldAction.Upload(file("jpg")))

        eventually(5.seconds) {
            viewModel.state.value shouldBe ImageFieldState.Failed(ImageFieldFailure.Unreachable)
        }
    }

    test("a file of an unsupported format should not be uploaded") {
        val api = FakeMediaApi(Outcome.Success(ImageId.generate()))
        val viewModel = ImageFieldViewModel(api)

        viewModel.handle(ImageFieldAction.Upload(file("gif")))

        viewModel.state.value shouldBe ImageFieldState.Failed(ImageFieldFailure.Unexpected)
        api.uploads shouldBe emptyList()
    }
})
