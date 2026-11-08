package app.roleparty.media.infrastructure

import app.roleparty.media.infrastructure.adapter.secondary.InfrastructuredImages
import app.roleparty.media.test.ImagesContractTest

class InfrastructuredImagesContractTest : ImagesContractTest({ InfrastructuredImages(TestPostgres.database) })
