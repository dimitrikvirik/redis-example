package git.dimitrikvirik.redisexample.service

import git.dimitrikvirik.redisexample.model.RedisServer
import git.dimitrikvirik.redisexample.repository.JedisConfigurationRepository
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class RedisConfigRegisterService(
    val jedisRepository: JedisConfigurationRepository
) {

    fun register(server: RedisServer) {
        jedisRepository.save(server)
    }

    fun unregister(id: String) {
        jedisRepository.delete(id)
    }

    @EventListener(ApplicationReadyEvent::class)
    fun test() {
        register(RedisServer("2", "localhost", 6001, null, null))
//        register(RedisServer("3", "localhost", 6002, null, null))
//        register(RedisServer("4", "localhost", 6003, null, null))

    }


}