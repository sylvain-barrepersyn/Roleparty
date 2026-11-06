package app.roleparty.core.commons

import java.nio.file.Path
import kotlin.io.path.isReadable
import kotlin.io.path.readText

/**
 * Resolves a configuration value, preferring a `<name>_FILE` path (Docker / Swarm secret) whose trimmed content is
 * used when it points to a readable file, otherwise the plain `<name>` entry.
 */
fun Map<String, String>.valueOrFile(name: String): String? {
    val file = this["${name}_FILE"]?.let(Path::of)
    return if (file != null && file.isReadable()) file.readText().trim() else this[name]
}

fun Map<String, String>.requireValueOrFile(name: String): String =
    valueOrFile(name) ?: error("Environment variable $name is not set")
