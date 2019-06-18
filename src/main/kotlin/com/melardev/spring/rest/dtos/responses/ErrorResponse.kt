package com.melardev.spring.rest.dtos.responses

import com.melardev.spring.rest.dtos.responses.AppResponse

class ErrorResponse(errorMessage: String) : AppResponse(false) {

    init {
        addFullMessage(errorMessage)
    }

}
