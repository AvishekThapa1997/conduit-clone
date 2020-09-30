package com.harry.example.conduitclone.pojos

sealed class Response
data class Success<T>(val data: T) : Response()
data class Failure(val message: String) : Response()
