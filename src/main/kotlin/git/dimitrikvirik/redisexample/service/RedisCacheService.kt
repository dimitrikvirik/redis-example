package git.dimitrikvirik.redisexample.service

import git.dimitrikvirik.redisexample.cache.CacheTemplate
import org.springframework.stereotype.Service

@Service
class RedisCacheService(
    val redisCacheTemplate: CacheTemplate<String>
) {
    fun getAll(): Map<String, String> {
        redisCacheTemplate.values
        return redisCacheTemplate
    }

    fun set(key: String, value: String) {
        redisCacheTemplate.set(key, value)
    }


}