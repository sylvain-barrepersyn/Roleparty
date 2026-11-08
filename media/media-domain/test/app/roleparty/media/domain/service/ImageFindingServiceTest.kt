package app.roleparty.media.domain.service

import app.roleparty.media.commons.ImageId
import app.roleparty.media.test.MediaBehaviorTest
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

class ImageFindingServiceTest : MediaBehaviorTest() {

    private val findImage = fixtures.findImage

    init {
        Given("an uploaded image") {
            val imageId = fixtures.`given an image`()

            When("finding it") {
                Then("it should be found") {
                    findImage.byId(imageId).shouldNotBeNull().id shouldBe imageId
                }
            }
        }

        Given("an unknown image") {
            When("finding it") {
                Then("it should find nothing") {
                    findImage.byId(ImageId.generate()).shouldBeNull()
                }
            }
        }
    }
}
