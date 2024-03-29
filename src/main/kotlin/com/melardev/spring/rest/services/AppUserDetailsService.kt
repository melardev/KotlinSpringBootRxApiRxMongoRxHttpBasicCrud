package com.melardev.spring.rest.services

import com.melardev.spring.rest.entities.SecurityUser
import com.melardev.spring.rest.entities.User
import com.melardev.spring.rest.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class AppUserDetailsService : ReactiveUserDetailsService {

    @Autowired
    private val userRepository: UserRepository? = null
    @Autowired
    internal var passwordEncoder: PasswordEncoder? = null

    override fun findByUsername(username: String): Mono<UserDetails> {
        return userRepository!!.findByUsername(username)
                .switchIfEmpty(
                        Mono.defer { Mono.error<User> { UsernameNotFoundException("User not Found") } }
                )
                .map { userDb -> SecurityUser(userDb) }

    }

    fun countSync(): Long {
        return userRepository!!.count().block()!!
    }

    fun save(user: User): Mono<User> {
        user.password = passwordEncoder!!.encode(user.password)
        if (user.role == null)
            user.role = "ROLE_USER"

        return userRepository!!.save(user)
    }

    fun saveAll(vararg users: User): Flux<User> {
        if (users.isEmpty())
            return Flux.empty<User>()

        var savedUsers = Flux.empty<User>()
        for (i in users.indices) {
            savedUsers = savedUsers.concatWith(save(users[i]))
        }
        return savedUsers
    }
}
