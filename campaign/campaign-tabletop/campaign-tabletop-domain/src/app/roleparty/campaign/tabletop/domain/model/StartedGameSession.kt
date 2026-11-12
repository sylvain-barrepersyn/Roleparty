package app.roleparty.campaign.tabletop.domain.model

import kotlin.time.Instant

data class StartedGameSession(val gameSession: GameSession, val startedAt: Instant)
