package git.dimitrikvirik.redisexample.cache

import git.dimitrikvirik.redisexample.enums.CacheProviderType

interface CacheProvider<K, V> : MutableMap<K, V> {

    val type: CacheProviderType
}