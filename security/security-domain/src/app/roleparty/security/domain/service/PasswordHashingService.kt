package app.roleparty.security.domain.service

import app.roleparty.security.domain.model.PasswordHash
import app.roleparty.security.domain.model.PasswordHashingCost
import de.mkammerer.argon2.Argon2Factory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext

private const val MAX_CONCURRENT_HASHES = 4

internal class PasswordHashingService(private val cost: PasswordHashingCost) {

    private val hasher = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id)
    private val concurrentHashes = Semaphore(MAX_CONCURRENT_HASHES)

    suspend fun hash(password: String): PasswordHash = hashing {
        PasswordHash.from(hasher.hash(cost.iterations, cost.memoryKib, cost.parallelism, password.toCharArray()))
    }

    suspend fun verify(password: String, hash: PasswordHash): Boolean = hashing {
        hasher.verify(hash.toString(), password.toCharArray())
    }

    private suspend fun <T> hashing(block: () -> T): T =
        concurrentHashes.withPermit { withContext(Dispatchers.IO) { block() } }
}
