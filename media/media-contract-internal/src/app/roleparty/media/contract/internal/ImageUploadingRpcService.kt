package app.roleparty.media.contract.internal

import app.roleparty.media.commons.ImageFormat
import app.roleparty.media.commons.ImageId
import kotlinx.rpc.annotations.Rpc

@Rpc
interface ImageUploadingRpcService {

    suspend fun upload(content: ByteArray, format: ImageFormat): ImageId?

    suspend fun ping()
}
