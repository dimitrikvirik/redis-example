package git.dimitrikvirik.redisexample.repository

import git.dimitrikvirik.redisexample.enums.ConfigurationStorageType
import git.dimitrikvirik.redisexample.model.RedisServer
import redis.clients.jedis.Jedis
import java.util.concurrent.locks.ReentrantLock


class InMemoryJedisConfigurationRepository(
    private val defaultJedis: Jedis?
) : JedisConfigurationRepository {

    override val type: ConfigurationStorageType = ConfigurationStorageType.IN_MEMORY
    private val jedisMap = mutableMapOf<String, Jedis>()
    private val lock = ReentrantLock()

    override fun getDefault(): Jedis? {
        return defaultJedis
    }

    override fun get(id: String): Jedis? {
        return jedisMap[id]
    }

    override fun save(server: RedisServer) {
        lock.lock()
        Jedis(server.host, server.port).use { jedis ->
            jedis.auth(server.password)
            jedisMap[server.id] = jedis
        }

        lock.unlock()
    }

    override fun delete(id: String) {
        lock.lock()
        jedisMap.remove(id)
        lock.unlock()
    }


}