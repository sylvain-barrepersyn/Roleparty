package app.roleparty.security.frontend.container.data

import eu.anifantakis.lib.ksafe.KSafe

data class StoredSession(val accessToken: String, val refreshToken: String)

interface SessionStore {

    suspend fun load(): StoredSession?

    suspend fun save(session: StoredSession)

    suspend fun clear()
}

private const val STORE_NAME = "roleparty_session"
private const val ACCESS_TOKEN = "accessToken"
private const val REFRESH_TOKEN = "refreshToken"

internal class KSafeSessionStore(private val safe: KSafe = KSafe(fileName = STORE_NAME)) : SessionStore {

    override suspend fun load(): StoredSession? {
        val accessToken = safe.get<String?>(ACCESS_TOKEN, null) ?: return null
        val refreshToken = safe.get<String?>(REFRESH_TOKEN, null) ?: return null
        return StoredSession(accessToken, refreshToken)
    }

    override suspend fun save(session: StoredSession) {
        safe.put(ACCESS_TOKEN, session.accessToken)
        safe.put(REFRESH_TOKEN, session.refreshToken)
    }

    override suspend fun clear() {
        safe.delete(ACCESS_TOKEN)
        safe.delete(REFRESH_TOKEN)
    }
}
