package com.harry.example.conduitclone.utility

class Resource<T> private constructor(val status: Status, var data: T?, val message: String?) {
    companion object {
        fun <T> success(data: T): Resource<T> = Resource(Status.SUCCESS, data, null)
        fun <T> error(message: String?): Resource<T> = Resource(Status.ERROR, null, message)
    }
}

enum class Status {
    SUCCESS,
    ERROR
}