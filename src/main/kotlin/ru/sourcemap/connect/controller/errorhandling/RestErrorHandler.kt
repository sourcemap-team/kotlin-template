package ru.sourcemap.connect.controller.errorhandling

import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler


@Order(Ordered.LOWEST_PRECEDENCE)
@ControllerAdvice
class RestErrorHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(ForbiddenException::class)
    fun handleForbiddenException(
        forbiddenException: ForbiddenException
    ): ResponseEntity<ErrorResponse> {
        logger.warn(forbiddenException.message, forbiddenException)
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorResponse(forbiddenException.message!!))
    }

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(
        notFoundException: NotFoundException
    ): ResponseEntity<ErrorResponse> {
        logger.warn(notFoundException.message, notFoundException)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse(notFoundException.message!!))
    }

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequestException(
        badRequestException: BadRequestException
    ): ResponseEntity<ErrorResponse> {
        logger.warn(badRequestException.message, badRequestException)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse(badRequestException.message!!))
    }
}
