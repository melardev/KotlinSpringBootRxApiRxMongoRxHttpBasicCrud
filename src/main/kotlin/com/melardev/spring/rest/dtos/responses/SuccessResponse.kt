package com.melardev.spring.rest.dtos.responses

import com.melardev.spring.rest.dtos.responses.AppResponse

class SuccessResponse @JvmOverloads constructor(message: String) : AppResponse(true) {

    init {
        addFullMessage(message)
    }
}
