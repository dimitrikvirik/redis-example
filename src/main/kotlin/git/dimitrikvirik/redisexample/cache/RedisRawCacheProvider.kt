package git.dimitrikvirik.redisexample.cache

import git.dimitrikvirik.redisexample.enums.CacheProviderType
import redis.clients.jedis.Jedis

class RedisRawCacheProvider(
    private val jedis: Jedis
) : CacheProvider<String, String> {


    override val type: CacheProviderType
        get()  = CacheProviderType.REDIS

    override fun get(key: String): String? {
        return jedis.get(key)
    }

    override fun put(key: String, value: String): String? {
        val previous = jedis.get(key)
        jedis.set(key, value)
        return previous
    }

    override fun remove(key: String): String? {
        val previous = jedis.get(key)
        jedis.del(key)
        return previous
    }

    override fun clear() {
        jedis.flushAll()
    }

    override val entries: MutableSet<MutableMap.MutableEntry<String, String>>
        get() {
            val keys = jedis.keys("*")
            val entries = mutableSetOf<MutableMap.MutableEntry<String, String>>()
            for (key in keys) {
                val value = jedis.get(key) ?: continue
                entries.add(object : MutableMap.MutableEntry<String, String> {
                    override val key: String
                        get() = key
                    override val value: String
                        get() = value
                    override fun setValue(newValue: String): String {
                        val previous = jedis.get(key)
                        jedis.set(key, newValue)
                        return previous
                    }
                })
            }
            return entries
        }

    override val keys: MutableSet<String>
        get() {
            return jedis.keys("*").toMutableSet()
        }

    override val values: MutableCollection<String>
        get() {
            val keys = jedis.keys("*")
            val values = mutableListOf<String>()
            for (key in keys) {
                val value = jedis.get(key) ?: continue
                values.add(value)
            }
            return values
        }

    override val size: Int
        get() = jedis.keys("*").size

    override fun containsKey(key: String): Boolean {
        return jedis.exists(key)
    }

    override fun containsValue(value: String): Boolean {
        val keys = jedis.keys("*")
        for (key in keys) {
            val v = jedis.get(key) ?: continue
            if (v == value) return true
        }
        return false
    }

    override fun putAll(from: Map<out String, String>) {
        for ((key, value) in from) {
            jedis.set(key, value)
        }
    }

    override fun isEmpty(): Boolean {
        return jedis.keys("*").isEmpty()
    }


}