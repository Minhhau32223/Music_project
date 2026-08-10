package com.spotify.spotify_backend.presentation.advice;

import java.time.LocalDateTime;

import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.spotify.spotify_backend.application.dto.Response.ErrorResponse;
import com.spotify.spotify_backend.domain.exception.ConflictException;
import com.spotify.spotify_backend.domain.exception.FileUploadException;
import com.spotify.spotify_backend.domain.exception.ForbiddenException;
import com.spotify.spotify_backend.domain.exception.InvalidCredentialException;
import com.spotify.spotify_backend.domain.exception.NotFoundException;
import com.spotify.spotify_backend.domain.exception.UnauthorizedException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            NotFoundException ex,
            HttpServletRequest request
    ) {

        ErrorResponse response =
                new ErrorResponse(

                        LocalDateTime.now(),

                        HttpStatus.NOT_FOUND.value(),

                        HttpStatus.NOT_FOUND.getReasonPhrase(),

                        ex.getMessage(),

                        request.getRequestURI()
                );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(
            ConflictException ex,
            HttpServletRequest request
    ) {

        ErrorResponse response =
                new ErrorResponse(

                        LocalDateTime.now(),

                        HttpStatus.CONFLICT.value(),

                        HttpStatus.CONFLICT.getReasonPhrase(),

                        ex.getMessage(),

                        request.getRequestURI()
                );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(
            ConflictException ex,
            HttpServletRequest request
    ) {

        ErrorResponse response =
                new ErrorResponse(

                        LocalDateTime.now(),

                        HttpStatus.BAD_REQUEST.value(),

                        HttpStatus.BAD_REQUEST.getReasonPhrase(),

                        ex.getMessage(),

                        request.getRequestURI()
                );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(
            ConflictException ex,
            HttpServletRequest request
    ) {

        ErrorResponse response =
                new ErrorResponse(

                        LocalDateTime.now(),

                        HttpStatus.UNAUTHORIZED.value(),

                        HttpStatus.UNAUTHORIZED.getReasonPhrase(),

                        ex.getMessage(),

                        request.getRequestURI()
                );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(
            ConflictException ex,
            HttpServletRequest request
    ) {

        ErrorResponse response =
                new ErrorResponse(

                        LocalDateTime.now(),

                        HttpStatus.FORBIDDEN.value(),

                        HttpStatus.FORBIDDEN.getReasonPhrase(),

                        ex.getMessage(),

                        request.getRequestURI()
                );

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(response);
    }
    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<ErrorResponse> handleFileUpload(
            ConflictException ex,
            HttpServletRequest request
    ) {

        ErrorResponse response =
                new ErrorResponse(

                        LocalDateTime.now(),

                        HttpStatus.INTERNAL_SERVER_ERROR.value(),

                        HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),

                        ex.getMessage(),

                        request.getRequestURI()
                );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            ConflictException ex,
            HttpServletRequest request
    ) {

        ErrorResponse response =
                new ErrorResponse(

                        LocalDateTime.now(),

                        HttpStatus.INTERNAL_SERVER_ERROR.value(),

                        HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),

                        ex.getMessage(),

                        request.getRequestURI()
                );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
    @ExceptionHandler(InvalidCredentialException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredential(
            ConflictException ex,
            HttpServletRequest request
    ) {

        ErrorResponse response =
                new ErrorResponse(

                        LocalDateTime.now(),

                        HttpStatus.INTERNAL_SERVER_ERROR.value(),

                        HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),

                        ex.getMessage(),

                        request.getRequestURI()
                );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }



}
