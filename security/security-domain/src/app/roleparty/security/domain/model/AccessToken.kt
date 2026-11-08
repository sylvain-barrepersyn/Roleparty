package app.roleparty.security.domain.model

@JvmInline
value class AccessToken private constructor(private val value: String) {
    override fun toString() = value

    companion object {
        fun from(value: String) = AccessToken(value)
    }
}
