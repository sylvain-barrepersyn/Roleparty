package app.roleparty.security.domain.model

data class PasswordHashingCost(
    val iterations: Int = 3,
    val memoryKib: Int = 65536,
    val parallelism: Int = 1,
) {
    companion object {
        val MINIMAL = PasswordHashingCost(iterations = 1, memoryKib = 1024, parallelism = 1)
    }
}