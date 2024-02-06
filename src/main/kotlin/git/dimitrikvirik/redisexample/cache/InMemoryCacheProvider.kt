package git.dimitrikvirik.redisexample.cache

import git.dimitrikvirik.redisexample.enums.CacheProviderType

class InMemoryCacheProvider<K, V> : CacheProvider<K, V>, MutableMap<K, V> by mutableMapOf() {

    override val type: CacheProviderType = CacheProviderType.IN_MEMORY


}