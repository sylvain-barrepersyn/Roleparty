package app.roleparty.media.infrastructure

import app.roleparty.media.commons.ImageFormat
import app.roleparty.media.infrastructure.adapter.primary.ImageRpcController
import app.roleparty.media.test.MediaBehaviorTest
import app.roleparty.media.test.MediaFixture
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull

class ImageRpcControllerTest : MediaBehaviorTest() {

    private val controller = ImageRpcController(fixtures.uploadImage)

    init {
        Given("a JPEG image") {
            When("uploading it through RPC") {
                val imageId = controller.upload(MediaFixture.JPEG_CONTENT, ImageFormat.JPEG)

                Then("it should be stored") {
                    fixtures.findImage.byId(imageId.shouldNotBeNull()).shouldNotBeNull()
                }
            }
        }

        Given("content that is not an image") {
            When("uploading it through RPC") {
                val imageId = controller.upload("not an image".toByteArray(), ImageFormat.JPEG)

                Then("it should be refused") {
                    imageId.shouldBeNull()
                }
            }
        }
    }
}
