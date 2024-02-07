package git.dimitrikvirik.redisexample.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.NoOpPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
class SecurityConfig {


    fun userDetailsService(): UserDetailsService {
        val user = User.withUsername("user")
            .password(passwordEncoder().encode("user"))
            .roles("USER")
            .build()
        val admin = User.withUsername("admin")
            .password(passwordEncoder().encode("admin"))
            .roles("ADMIN")
            .build()
        return InMemoryUserDetailsManager(user, admin)
    }

    @Throws(Exception::class)
    @Bean
    fun filterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
        return httpSecurity.authorizeHttpRequests {
            it.anyRequest().permitAll()
        }
            .userDetailsService(userDetailsService())

            .httpBasic {  }
            .exceptionHandling {
                it.accessDeniedHandler {
                    a, response, b ->
                    println(b.message)
                }
            }.cors { it.disable() }
            .csrf { it.disable() }
            .build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }


}
