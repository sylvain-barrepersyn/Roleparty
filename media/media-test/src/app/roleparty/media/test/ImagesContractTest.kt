package app.roleparty.media.test

import app.roleparty.core.test.FixturedBehaviorTest
import app.roleparty.media.commons.ImageFormat
import app.roleparty.media.commons.ImageId
import app.roleparty.media.domain.model.Image
import app.roleparty.media.domain.port.secondary.Images
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

abstract class ImagesContractTest(createImages: () -> Images) : FixturedBehaviorTest() {

    private val images = createImages()

    init {
        Given("an unknown image") {
            val imageId = ImageId.generate()

            When("finding it") {
                Then("it should find nothing") {
                    images.findById(imageId).shouldBeNull()
                }
            }
        }

        Given("a saved image") {
            val image = Image(ImageId.generate(), MediaFixture.JPEG_CONTENT, ImageFormat.JPEG)
            images.save(image)

            When("finding it") {
                Then("it should find it with its content and format") {
                    images.findById(image.id) shouldBe image
                }
            }
        }
    }
}
