package git.dimitrikvirik.redisexample.repository

import git.dimitrikvirik.redisexample.enums.ConfigurationStorageType
import git.dimitrikvirik.redisexample.model.RedisServer
import redis.clients.jedis.Jedis

/**
 * Interface for managing Jedis configuration repositories.
 */
interface JedisConfigurationRepository {
    // Indicates the type of configuration storage
    val type: ConfigurationStorageType

    /**
     * Retrieves the default Jedis configuration.
     *
     * @return The default Jedis configuration, or null if not found.
     */
    fun getDefault(): Jedis?

    /**
     * Retrieves the Jedis configuration with the specified ID.
     *
     * @param id The ID of the Jedis configuration to retrieve.
     * @return The Jedis configuration with the specified ID, or null if not found.
     */
    fun get(id: String): Jedis?

    /**
     * Retrieves all Jedis configurations stored in the repository.
     *
     * @return A list of all Jedis configurations stored in the repository.
     */
    fun getAll(): List<Jedis>

    /**
     * Saves a new Redis server configuration to the repository.
     *
     * @param server The Redis server configuration to save.
     */
    fun save(server: RedisServer)

    /**
     * Deletes the Jedis configuration with the specified ID from the repository.
     *
     * @param id The ID of the Jedis configuration to delete.
     */
    fun delete(id: String)

    /**
     * Initiates replication among Jedis configurations in the repository.
     */
    fun doReplication()
}
