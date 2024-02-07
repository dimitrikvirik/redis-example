package git.dimitrikvirik.redisexample.cache

import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig

class JedisWrapper(
    val host: String,
    val port: Int,
    val user: String?,
    val password: String?,
    val partitioningNumber: Double = 0.75
) {
    private val pool by lazy {
        JedisPool(JedisPoolConfig(), host, port)
    }

    val jedis by lazy {
        pool.resource
    }

    private fun memoryUsage(): Long {
        return jedis.info("memory").split("\n").filter { it.startsWith("used_memory") }[0].split(":")[1].toLong()
    }

    private fun maxMemory(): Long {
        return jedis.info("memory").split("\n").filter { it.startsWith("maxmemory") }[0].split(":")[1].toLong()
    }

    fun isOverloaded(): Boolean {
        return memoryUsage() > maxMemory() * partitioningNumber
    }

    fun isAlive(): Boolean {
        return jedis.ping() == "PONG"
    }

    fun countReplicas(): Int {
        return jedis.info("replication").split("\n").filter { it.startsWith("slave") }.size
    }

    fun replicateTo(slave: JedisWrapper): Boolean {
        if (slave.isAlive()) {
            return false
        } else {
            slave.jedis.replicaof(host, port)
        }
        return true
    }

}