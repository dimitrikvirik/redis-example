package git.dimitrikvirik.redisexample.cache

import org.slf4j.Logger
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig

class JedisWrapper(
    val host: String,
    val port: Int,
    val user: String?,
    val password: String?,
    val overloadingNumber: Double = 0.75,
    val replicationNumber: Int = 2
) {
    private val pool by lazy {
        JedisPool(JedisPoolConfig(), host, port)
    }
    val log: Logger = org.slf4j.LoggerFactory.getLogger(JedisWrapper::class.java)

    val jedis by lazy {
        pool.resource
    }

    private fun memoryUsage(): Long {
        return jedis.info("memory").split("\n")
            .filter { it.startsWith("used_memory") }[0].split(":")[1].split("\r")[0].toLong()
    }

    private fun maxMemory(): Long {
        return jedis.info("memory").split("\n")
            .filter { it.startsWith("maxmemory") }[0].split(":")[1].split("\r")[0].toLong()
    }

    fun isOverloaded(): Boolean {
        return memoryUsage() > maxMemory() * overloadingNumber
    }

    fun isAlive(): Boolean {
        return jedis.isBroken.not()
    }

    fun countReplicas(): Int {
        return jedis.info("replication").split("\n").filter { it.startsWith("slave") }.size
    }

    fun dropReplica() {
        jedis.replicaofNoOne()
    }

    fun setReadOnlyFalse() {
        jedis.configSet("slave-read-only", "no")
    }

    fun replicateTo(slave: JedisWrapper): Boolean {

        if (!slave.isAlive()) {
            return false
        } else {
            log.info("Replicating to ${slave.host}:${slave.port} on ${host}:${port}")
            setReadOnlyFalse()
            slave.jedis.replicaof(host, port)
        }
        return true
    }

}