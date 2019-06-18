package com.melardev.spring.rest.entities

import org.springframework.data.mongodb.core.mapping.Document

@Document
data class User(var username: String? = null,
                var password: String? = null,
                var role: String? = null) : TimeStampedDocument()
