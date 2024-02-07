package git.dimitrikvirik.redisexample.cache

import git.dimitrikvirik.redisexample.enums.CacheProviderType

class CacheTemplate<V>(
    private val getCaches: () -> List<CacheProvider<String, V>>,
) : CacheProvider<String, V> {


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


    override fun get(key: String): V? {
        return keyInCaches[key]?.firstOrNull()?.get(key)
    }

    override fun put(key: String, value: V): V? {
        return getCaches().firstOrNull()?.put(key, value)
    }

    override fun remove(key: String): V? {
        return getCaches().map { it.remove(key) }.firstOrNull { it != null }
    }

    override fun clear() {
        getCaches().forEach { it.clear() }
    }

    override fun isEmpty(): Boolean {
        return getCaches().all { it.isEmpty() }
    }

    override fun putAll(from: Map<out String, V>) {
        from.forEach { (key, value) -> put(key, value) }
    }

    override fun containsValue(value: V): Boolean {
        return getCaches().any { it.containsValue(value) }
    }

    override fun containsKey(key: String): Boolean {
        return keyInCaches.containsKey(key)
    }

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

    override val keys: MutableSet<String>
        get() = keyInCaches.keys.toMutableSet()

    override val size: Int
        get() = keyInCaches.keys.size

    override val values: MutableCollection<V>
        get() = keyInCaches.keys.map { get(it)!! }.toMutableSet()

    override val type: CacheProviderType
        get() = CacheProviderType.REDIS
}