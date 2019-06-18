package com.melardev.spring.rest.config

import com.mongodb.MongoClient
import org.springframework.data.mongodb.config.AbstractMongoConfiguration

// @Configuration
// @EnableReactiveMongoRepositories
class MongoConfig : AbstractMongoConfiguration() {


    override fun getDatabaseName(): String {
        return "sboot_reactive_todos"
    }

    override fun mongoClient(): MongoClient {
        return MongoClient("mongodb://localhost")
    }
}
