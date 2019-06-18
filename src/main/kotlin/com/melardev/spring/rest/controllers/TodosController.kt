package com.melardev.spring.rest.controllers

import com.melardev.spring.rest.dtos.responses.ErrorResponse
import com.melardev.spring.rest.entities.Todo
import com.melardev.spring.rest.repositories.TodosRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import javax.validation.Valid

@CrossOrigin
@RestController
@RequestMapping("todos")
class TodosController(@Autowired
                      private val todosRepository: TodosRepository) {

    @GetMapping
    fun index(): Flux<Todo> {
        return this.todosRepository.findAllHqlSummary()
    }

    @GetMapping("/pending")
    fun getPending(): Flux<Todo> {
        return this.todosRepository.findByCompletedFalseHql()
    }

    @GetMapping("/completed")
    fun getCompleted(): Flux<Todo> {
        return todosRepository.findByCompletedIsTrueHql()
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable("id") id: String): Mono<ResponseEntity<*>> {
        return this.todosRepository.findById(id)
                .map { ResponseEntity.ok(it) as ResponseEntity<*> }
                .defaultIfEmpty(ResponseEntity(ErrorResponse("Todo not found"), HttpStatus.NOT_FOUND))
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    fun create(@Valid @RequestBody todo: Todo): Mono<ResponseEntity<Todo>> {
        return todosRepository.save(todo).map { ResponseEntity(it, HttpStatus.CREATED) }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun update(@PathVariable("id") id: String,
               @RequestBody todoInput: Todo): Mono<ResponseEntity<*>> {
        return todosRepository.findById(id)
                .flatMap { todoFromDb ->

                    val title = todoInput.title
                    todoFromDb.title = title

                    val description = todoInput.description
                    if (description != null)
                        todoFromDb.description = description

                    todoFromDb.isCompleted = todoInput.isCompleted
                    todosRepository.save(todoFromDb).map { ResponseEntity.ok(it) as ResponseEntity<*> }
                }.defaultIfEmpty(ResponseEntity(ErrorResponse("Todo not found"), HttpStatus.NOT_FOUND))
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun delete(@PathVariable("id") id: String): Mono<ResponseEntity<*>> {
        return todosRepository.findById(id)
                .flatMap { ot ->
                    todosRepository.delete(ot)
                            .then(Mono.just(ResponseEntity("", HttpStatus.NO_CONTENT) as ResponseEntity<*>))
                }
                .defaultIfEmpty(ResponseEntity(ErrorResponse("Todo not found"), HttpStatus.NOT_FOUND))
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteAll(): Mono<ResponseEntity<Void>> {
        return todosRepository.deleteAll().then(Mono.just(ResponseEntity(HttpStatus.NO_CONTENT)))
    }

}
