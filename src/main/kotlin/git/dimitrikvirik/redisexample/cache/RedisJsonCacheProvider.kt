package git.dimitrikvirik.redisexample.cache

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import git.dimitrikvirik.redisexample.enums.CacheProviderType
import redis.clients.jedis.Jedis
import javax.swing.tree.TreeNode

class RedisJsonCacheProvider<V>(
    private val jedis: Jedis,
    private val klass: Class<V>
) : CacheProvider<String, V> {
    private val objectMapper: ObjectMapper = jacksonObjectMapper()
    override val type: CacheProviderType = CacheProviderType.REDIS


    override fun get(key: String): V? {
        val json = jedis.get(key) ?: return null

        if(klass.simpleName == "String")
            return json as V
        return objectMapper.readValue(json, klass)
    }

    override fun put(key: String, value: V): V? {
        jedis.set(key, objectMapper.writeValueAsString(value))
        return value
    }

    override fun remove(key: String): V? {
        jedis.del(key.hashCode().toString())
        return get(key)
    }

    override fun clear() {
        jedis.flushAll()
    }

    override val entries: MutableSet<MutableMap.MutableEntry<String, V>>
        get() {
            val keys = jedis.keys("*")
            val entries = mutableSetOf<MutableMap.MutableEntry<String, V>>()
            for (key in keys) {

                val value = get(key) ?: continue
                entries.add(object : MutableMap.MutableEntry<String, V> {
                    override val key: String
                        get() = key
                    override val value: V
                        get() = value

                    override fun setValue(newValue: V): V {
                        val json = objectMapper.writeValueAsString(newValue)
                        jedis.set(key.hashCode().toString(), json)
                        return newValue
                    }
                })
            }
            return entries
        }

    override val keys: MutableSet<String>
        get() {
            val keys = jedis.keys("*")
            return keys.toMutableSet()
        }

    override val size: Int
        get() = jedis.keys("*").size

    override val values: MutableCollection<V>
        get() {
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
        for ((key, value) in from) {
            val json = objectMapper.writeValueAsString(value)
            jedis.set(key.hashCode().toString(), json)
        }
    }

    override fun getOrDefault(key: String, defaultValue: V): V {
        val json = jedis.get(key.hashCode().toString()) ?: return defaultValue
        return objectMapper.readValue(json, klass)
    }

    override fun remove(key: String, value: V): Boolean {
        val json = jedis.get(key) ?: return false
        val v = objectMapper.readValue(json, klass)
        if (v == value) {
            jedis.del(key)
            return true
        }
        return false
    }


    override fun replace(key: String, oldValue: V, newValue: V): Boolean {
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