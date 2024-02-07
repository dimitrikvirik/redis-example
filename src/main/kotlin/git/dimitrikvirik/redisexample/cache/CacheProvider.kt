package git.dimitrikvirik.redisexample.cache

import git.dimitrikvirik.redisexample.enums.CacheProviderType

/**
 * Interface for cache providers.
 *
 * @param K The type of keys stored in the cache.
 * @param V The type of values stored in the cache.
 */
interface CacheProvider<K, V> : MutableMap<K, V> {
    // Indicates the type of cache provider
    val type: CacheProviderType
}
