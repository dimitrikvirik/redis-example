package git.dimitrikvirik.redisexample.model

data class RedisServer(
    val id: String,
    val host: String,
    val port: Int,
    val username: String?,
    val password: String?,
    val replicationNumber: Int = 2,
    val overloadingNumber: Double = 0.75
)
