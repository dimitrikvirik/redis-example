package git.dimitrikvirik.redisexample.cache

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import git.dimitrikvirik.redisexample.enums.CacheProviderType
import redis.clients.jedis.Jedis

/**
 * Provides a cache implementation using Redis for storing JSON serialized values.
 *
 * @param jedis The Jedis instance used for interacting with Redis.
 * @param klass The class type parameter indicating the type of values to be stored in the cache.
 */
class RedisJsonCacheProvider<V>(
    private val jedis: Jedis,
    private val klass: Class<V>
) : CacheProvider<String, V> {

    // ObjectMapper for serializing/deserializing JSON
    private val objectMapper: ObjectMapper = jacksonObjectMapper()

    // Indicates the type of cache provider
    override val type: CacheProviderType = CacheProviderType.REDIS

    override fun get(key: String): V? {
        // Retrieve JSON string from Redis
        val json = jedis.get(key) ?: return null

        // Deserialize JSON string to object of type V
        if (klass.simpleName == "String")
            return json as V
        return objectMapper.readValue(json, klass)
    }

    override fun put(key: String, value: V): V? {
        // Serialize value to JSON and store in Redis
        jedis.set(key, objectMapper.writeValueAsString(value))
        return value
    }

    override fun remove(key: String): V? {
        // Remove key from Redis and return the associated value
        jedis.del(key)
        return get(key)
    }

    override fun clear() {
        // Clear all keys/values from Redis
        jedis.flushAll()
    }

    override val entries: MutableSet<MutableMap.MutableEntry<String, V>>
        get() {
            // Retrieve all keys from Redis and construct entries set
            val keys = jedis.keys("*")
            val entries = mutableSetOf<MutableMap.MutableEntry<String, V>>()
            for (key in keys) {
                // Retrieve value for each key and construct entry
                val value = get(key) ?: continue
                entries.add(object : MutableMap.MutableEntry<String, V> {
                    override val key: String = key
                    override val value: V = value
                    override fun setValue(newValue: V): V {
                        // Update value in Redis
                        val json = objectMapper.writeValueAsString(newValue)
                        jedis.set(key, json)
                        return newValue
                    }
                })
            }
            return entries
        }

    override val keys: MutableSet<String>
        get() {
            // Retrieve all keys from Redis
            return jedis.keys("*").toMutableSet()
        }

    override val size: Int
        get() = jedis.keys("*").size

    override val values: MutableCollection<V>
        get() {
            // Retrieve all values from Redis
            val keys = jedis.keys("*")
            val values = mutableListOf<V>()
            for (key in keys) {
                val json = jedis.get(key) ?: continue
                val value = objectMapper.readValue(json, klass)
                values.add(value)
            }
            return values
        }

    override fun containsKey(key: String): Boolean = jedis.exists(key)

    override fun containsValue(value: V): Boolean {
        // Check if Redis contains the specified value
        val keys = jedis.keys("*")
        for (key in keys) {
            val json = jedis.get(key) ?: continue
            val v = objectMapper.readValue(json, klass)
            if (v == value) return true
        }
        return false
    }

    override fun isEmpty(): Boolean = jedis.keys("*").isEmpty()

    override fun putAll(from: Map<out String, V>) {
        // Store multiple key-value pairs in Redis
        for ((key, value) in from) {
            val json = objectMapper.writeValueAsString(value)
            jedis.set(key, json)
        }
    }

    override fun getOrDefault(key: String, defaultValue: V): V {
        // Retrieve value from Redis or return default value if not found
        val json = jedis.get(key) ?: return defaultValue
        return objectMapper.readValue(json, klass)
    }

    override fun remove(key: String, value: V): Boolean {
        // Remove key-value pair from Redis if the value matches
        val json = jedis.get(key) ?: return false
        val v = objectMapper.readValue(json, klass)
        if (v == value) {
            jedis.del(key)
            return true
        }
        return false
    }

    override fun replace(key: String, oldValue: V, newValue: V): Boolean {
        // Replace value in Redis if the old value matches
        val json = jedis.get(key) ?: return false
        val v = objectMapper.readValue(json, klass)
        if (v == oldValue) {
            val json = objectMapper.writeValueAsString(newValue)
            jedis.set(key, json)
            return true
        }
        return false
    }
}
