package git.dimitrikvirik.redisexample.repository

import git.dimitrikvirik.redisexample.enums.ConfigurationStorageType
import git.dimitrikvirik.redisexample.model.RedisServer
import redis.clients.jedis.Jedis

interface JedisConfigurationRepository {
    val type: ConfigurationStorageType

    fun getDefault(): Jedis?

    fun get(id: String): Jedis?

    fun save(server: RedisServer)

    fun delete(id: String)
}