package git.dimitrikvirik.redisexample.controller

import RedisCacheApi
import git.dimitrikvirik.redisexample.service.RedisCacheService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class RedisCacheController(val cacheService: RedisCacheService) : RedisCacheApi {

    override fun getAllValues(): ResponseEntity<Map<String, String>> {
        return ResponseEntity.ok(cacheService.getAll())
    }

    override fun setValue(key: String, value: String): ResponseEntity<Unit> {
        cacheService.set(key, value)
        return ResponseEntity.ok().build()
    }
}