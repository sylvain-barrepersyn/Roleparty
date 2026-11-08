package app.roleparty.media.domain.service

import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import app.roleparty.media.commons.ImageFormat
import app.roleparty.media.domain.model.Image
import app.roleparty.media.domain.model.MediaError
import app.roleparty.media.test.MediaBehaviorTest
import app.roleparty.media.test.MediaFixture
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

class ImageUploadingServiceTest : MediaBehaviorTest() {

    private val uploadImage = fixtures.uploadImage

    init {
        Given("a JPEG image") {
            val content = MediaFixture.JPEG_CONTENT

            When("uploading it") {
                val imageId = uploadImage(content, ImageFormat.JPEG).shouldBeSuccess()

                Then("it should be stored with its content and format") {
                    val image = fixtures.findImage.byId(imageId).shouldNotBeNull()
                    image shouldBe Image(imageId, content, ImageFormat.JPEG)
                }
            }
        }

        Given("content that is not a JPEG image") {
            val content = "<script>alert(1)</script>".toByteArray()

            When("uploading it as a JPEG image") {
                val outcome = uploadImage(content, ImageFormat.JPEG)

                Then("it should be refused") {
                    outcome shouldBeFailure MediaError.InvalidImage(ImageFormat.JPEG)
                    fixtures.storedImages() shouldBe 0
                }
            }
        }

        Given("empty content") {
            When("uploading it") {
                val outcome = uploadImage(ByteArray(0), ImageFormat.JPEG)

                Then("it should be refused") {
                    outcome shouldBeFailure MediaError.InvalidImage(ImageFormat.JPEG)
                }
            }
        }

        Given("a JPEG image larger than the limit") {
            val content = MediaFixture.JPEG_CONTENT + ByteArray(Image.MAX_BYTES)

            When("uploading it") {
                val outcome = uploadImage(content, ImageFormat.JPEG)

                Then("it should be refused") {
                    outcome shouldBeFailure MediaError.ImageTooLarge(Image.MAX_BYTES)
                    fixtures.storedImages() shouldBe 0
                }
            }
        }
    }
}
