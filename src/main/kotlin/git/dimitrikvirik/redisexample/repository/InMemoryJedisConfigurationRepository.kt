package git.dimitrikvirik.redisexample.repository

import git.dimitrikvirik.redisexample.cache.JedisWrapper
import git.dimitrikvirik.redisexample.enums.ConfigurationStorageType
import git.dimitrikvirik.redisexample.model.RedisServer
import org.slf4j.Logger
import redis.clients.jedis.Jedis
import java.util.concurrent.locks.ReentrantLock

/**
 * Configuration class for Spring Security.
 */

class InMemoryJedisConfigurationRepository(
    private val defaultJedis: JedisWrapper?
) : JedisConfigurationRepository {

    override val type: ConfigurationStorageType = ConfigurationStorageType.IN_MEMORY
    private val jedisMap = mutableMapOf<String, JedisWrapper>()
    private val lock = ReentrantLock()
    private val log: Logger = org.slf4j.LoggerFactory.getLogger(InMemoryJedisConfigurationRepository::class.java)

    override fun getDefault(): Jedis? {
        return defaultJedis?.jedis
    }

    override fun get(id: String): Jedis? {
        return jedisMap[id]?.jedis
    }

    override fun getAll(): List<Jedis> {

        return jedisMap.values.filter {
            it.isAlive() && it.isOverloaded().not()
        }.map { it.jedis }.plus(defaultJedis?.jedis).filterNotNull()
    }

    override fun save(server: RedisServer) {
        lock.lock()
        Jedis(server.host, server.port).use { jedis ->
            if (server.username != null) {
                jedis.auth(server.username, server.password)
            } else if (server.password != null) {
                jedis.auth(server.password)
            }
            jedisMap[server.id] = JedisWrapper(
                server.host,
                server.port,
                server.username,
                server.password,
                server.overloadingNumber
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
                .filter { it != current && (it.countReplicas() < it.replicationNumber + 1) }
                .takeIf { current.countReplicas() < current.replicationNumber }
                ?.forEach {
                    current.replicateTo(it)
                    log.info("Total replicas for ${current.host}:${current.port} is ${current.countReplicas()} ")
                }
        }


        lock.unlock()
    }


}