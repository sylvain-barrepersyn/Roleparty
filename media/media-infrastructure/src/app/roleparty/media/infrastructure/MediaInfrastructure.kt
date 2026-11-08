package app.roleparty.media.infrastructure

import app.roleparty.core.commons.requireValueOrFile
import app.roleparty.core.database.Postgres
import app.roleparty.core.ktor.rpc.serveRpc
import app.roleparty.media.contract.internal.ImageUploadingRpcService
import app.roleparty.media.database.setUpMediaSchema
import app.roleparty.media.domain.MediaModule
import app.roleparty.media.infrastructure.adapter.primary.ImageRpcController
import app.roleparty.media.infrastructure.adapter.primary.MediaLocalization
import app.roleparty.media.infrastructure.adapter.primary.mediaRoutes
import app.roleparty.media.infrastructure.adapter.secondary.InfrastructuredImages
import io.ktor.server.application.Application
import app.roleparty.core.ktor.rest.resourceRouting

private const val RPC_TOKEN = "MEDIA_RPC_TOKEN"
private const val RPC_PATH = "/rpc/media"

fun loadMediaModule(postgres: Postgres): MediaModule {
    postgres.setUpMediaSchema()

    return MediaModule(InfrastructuredImages(postgres.database))
}

fun Application.serveMediaRoutes(module: MediaModule, localization: MediaLocalization) {
    resourceRouting { mediaRoutes(module, localization) }
}

fun Application.serveMediaRpc(module: MediaModule, env: Map<String, String> = System.getenv()) {
    val controller = ImageRpcController(module.uploadImage)

    serveRpc(RPC_PATH, env.requireValueOrFile(RPC_TOKEN)) {
        registerService<ImageUploadingRpcService> { controller }
    }
}
