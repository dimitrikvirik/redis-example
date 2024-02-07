package git.dimitrikvirik.redisexample.cache
import org.slf4j.Logger
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig

/**
 * Provides a wrapper around the Jedis client for Redis, facilitating easier management and interaction.
 *
 * @property host The host address of the Redis server.
 * @property port The port number of the Redis server.
 * @property user The username for authentication (if applicable).
 * @property password The password for authentication (if applicable).
 * @property overloadingNumber The threshold for determining if the Redis server is overloaded. Default is 0.75.
 * @property replicationNumber The number of replicas for the Redis server. Default is 2.
 */
class JedisWrapper(
    val host: String,
    val port: Int,
    val user: String?,
    val password: String?,
    val overloadingNumber: Double = 0.75,
    val replicationNumber: Int = 2
) {
    // Lazy initialization of the Jedis pool
    private val pool by lazy {
        JedisPool(JedisPoolConfig(), host, port)
    }

    // Logger for logging messages
    val log: Logger = org.slf4j.LoggerFactory.getLogger(JedisWrapper::class.java)

    /**
     * Lazily initialized Jedis instance from the pool.
     */
    val jedis by lazy {
        pool.resource
    }

    /**
     * Retrieves the current memory usage of the Redis server.
     *
     * @return The memory usage in bytes.
     */
    private fun memoryUsage(): Long {
        return jedis.info("memory").split("\n")
            .filter { it.startsWith("used_memory") }[0].split(":")[1].split("\r")[0].toLong()
    }

    /**
     * Retrieves the maximum memory allocated to the Redis server.
     *
     * @return The maximum memory in bytes.
     */
    private fun maxMemory(): Long {
        return jedis.info("memory").split("\n")
            .filter { it.startsWith("maxmemory") }[0].split(":")[1].split("\r")[0].toLong()
    }

    /**
     * Checks if the Redis server is overloaded based on memory usage.
     *
     * @return True if the server is overloaded, false otherwise.
     */
    fun isOverloaded(): Boolean {
        return memoryUsage() > maxMemory() * overloadingNumber
    }

    /**
     * Checks if the Redis connection is alive.
     *
     * @return True if the connection is alive, false otherwise.
     */
    fun isAlive(): Boolean {
        return jedis.isBroken.not()
    }

    /**
     * Counts the number of replicas connected to the Redis server.
     *
     * @return The number of replicas.
     */
    fun countReplicas(): Int {
        return jedis.info("replication").split("\n").filter { it.startsWith("slave") }.size
    }

    /**
     * Drops the replica status of the current Redis instance.
     */
    fun dropReplica() {
        jedis.replicaofNoOne()
    }

    /**
     * Sets the read-only status of the Redis instance to false.
     */
    fun setReadOnlyFalse() {
        jedis.configSet("slave-read-only", "no")
    }

    /**
     * Replicates data to a specified Redis slave.
     *
     * @param slave The JedisWrapper representing the slave Redis instance.
     * @return True if replication is successful, false otherwise.
     */
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
