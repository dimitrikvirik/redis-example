package git.dimitrikvirik.redisexample.cache

import git.dimitrikvirik.redisexample.enums.CacheProviderType

/**
 * A generic cache template that combines multiple cache providers.
 *
 * @param V the type of values stored in the cache
 * @property getCaches a function that returns a list of cache providers
 */
class CacheTemplate<V>(
    private val getCaches: () -> List<CacheProvider<String, V>>,
) : CacheProvider<String, V> {

    /**
     * A map that holds keys and their corresponding list of cache providers.
     */
    private val keyInCaches: Map<String, List<CacheProvider<String, V>>>
        get() {
            val map = mutableMapOf<String, List<CacheProvider<String, V>>>()
            for (cacheProvider in getCaches()) {
                for (key in cacheProvider.keys) {
                    map[key] = map[key]?.plus(cacheProvider) ?: listOf(cacheProvider)
                }
            }
            return map
        }

    /**
     * Retrieves the value associated with the given key.
     *
     * @param key the key whose associated value is to be retrieved
     * @return the value associated with the key, or null if the key is not present in any cache provider
     */
    override fun get(key: String): V? {
        return keyInCaches[key]?.firstOrNull()?.get(key)
    }

    /**
     * Associates the specified value with the specified key in the cache.
     *
     * @param key the key with which the specified value is to be associated
     * @param value the value to be associated with the specified key
     * @return the previous value associated with the key, or null if there was no mapping for the key
     */
    override fun put(key: String, value: V): V? {
        return getCaches().firstOrNull()?.put(key, value)
    }


    /**
     * Retrieves a mutable set view of the entries contained in the cache.
     */
    override val entries: MutableSet<MutableMap.MutableEntry<String, V>>
        get() = keyInCaches.keys.map { key ->
            object : MutableMap.MutableEntry<String, V> {
                override val key: String
                    get() = key
                override val value: V
                    get() = get(key)!!

                override fun setValue(newValue: V): V {
                    return put(key, newValue)!!
                }
            }
        }.toMutableSet()

    /**
     * Retrieves a mutable set view of the keys contained in the cache.
     */
    override val keys: MutableSet<String>
        get() = keyInCaches.keys.toMutableSet()

    /**
     * Retrieves the number of entries in the cache.
     */
    override val size: Int
        get() = keyInCaches.keys.size

    /**
     * Retrieves a mutable collection view of the values contained in the cache.
     */
    override val values: MutableCollection<V>
        get() = keyInCaches.keys.map { get(it)!! }.toMutableSet()


    /**
     * Removes the mapping for the specified key from the cache if present.
     *
     * @param key the key whose mapping is to be removed from the cache
     * @return the previous value associated with the key, or null if there was no mapping for the key
     */
    override fun remove(key: String): V? {
        return getCaches().map { it.remove(key) }.firstOrNull { it != null }
    }

    /**
     * Removes all mappings from the cache.
     */
    override fun clear() {
        getCaches().forEach { it.clear() }
    }

    /**
     * Returns true if the cache contains no key-value mappings.
     */
    override fun isEmpty(): Boolean {
        return getCaches().all { it.isEmpty() }
    }

    /**
     * Copies all of the mappings from the specified map to the cache.
     *
     * @param from the map whose mappings are to be stored in the cache
     */
    override fun putAll(from: Map<out String, V>) {
        from.forEach { (key, value) -> put(key, value) }
    }

    /**
     * Returns true if the cache contains a mapping for the specified value.
     *
     * @param value the value whose presence in the cache is to be tested
     * @return true if the cache contains a mapping for the specified value
     */
    override fun containsValue(value: V): Boolean {
        return getCaches().any { it.containsValue(value) }
    }

    /**
     * Returns true if the cache contains a mapping for the specified key.
     *
     * @param key the key whose presence in the cache is to be tested
     * @return true if the cache contains a mapping for the specified key
     */
    override fun containsKey(key: String): Boolean {
        return keyInCaches.containsKey(key)
    }


    /**
     * Retrieves the type of the cache provider.
     */
    override val type: CacheProviderType
        get() = CacheProviderType.REDIS
}
