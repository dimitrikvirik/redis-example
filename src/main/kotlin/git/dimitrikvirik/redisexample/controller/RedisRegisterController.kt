package git.dimitrikvirik.redisexample.controller

import RedisRegisterApi
import git.dimitrikvirik.redisexample.model.RedisServer
import git.dimitrikvirik.redisexample.service.RedisConfigRegisterService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class RedisRegisterController(val service: RedisConfigRegisterService): RedisRegisterApi {

    override fun register(server: RedisServer): ResponseEntity<Unit> {
        service.register(server)
        return ResponseEntity.ok().build()
    }

    override fun unregister(id: String): ResponseEntity<Unit> {
        service.unregister(id)
        return ResponseEntity.ok().build()
    }

}