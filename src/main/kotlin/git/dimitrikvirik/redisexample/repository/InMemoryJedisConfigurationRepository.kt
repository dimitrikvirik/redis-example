package git.dimitrikvirik.redisexample.repository

import git.dimitrikvirik.redisexample.cache.JedisWrapper
import git.dimitrikvirik.redisexample.enums.ConfigurationStorageType
import git.dimitrikvirik.redisexample.model.RedisServer
import redis.clients.jedis.Jedis
import java.util.concurrent.locks.ReentrantLock


class InMemoryJedisConfigurationRepository(
    private val defaultJedis: JedisWrapper?,
    private val replicationNumber: Int = 2
) : JedisConfigurationRepository {

    override val type: ConfigurationStorageType = ConfigurationStorageType.IN_MEMORY
    private val jedisMap = mutableMapOf<String, JedisWrapper>()
    private val lock = ReentrantLock()

    override fun getDefault(): Jedis? {
        return defaultJedis?.jedis
    }

    override fun get(id: String): Jedis? {
        return jedisMap[id]?.jedis
    }

    override fun getAll(): List<Jedis> {
        return jedisMap.values.map { it.jedis }.plus(defaultJedis?.jedis).filterNotNull()
    }

    override fun save(server: RedisServer) {
        lock.lock()
        Jedis(server.host, server.port).use { jedis ->
            jedis.auth(server.password)
            jedisMap[server.id] = JedisWrapper(
                server.host,
                server.port,
                server.username,
                server.password
            )
        }

        lock.unlock()
    }

    override fun delete(id: String) {
        lock.lock()
        jedisMap.remove(id)
        lock.unlock()
    }

    override fun doReplication() {
        lock.lock()
        //replicate each jedis to max two other
        val jedisList = jedisMap.values.toList()
        for (i in jedisList.indices) {
            val current = jedisList[i]
            jedisList.filter { it.isAlive() }.sortedBy { it.countReplicas() }
                .take(replicationNumber).forEach {
                    current.replicateTo(it)
                }
        }


        lock.unlock()
    }


}