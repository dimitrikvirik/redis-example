package git.dimitrikvirik.redisexample

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class RedisExampleApplication

fun main(args: Array<String>) {
    runApplication<RedisExampleApplication>(*args)
}
