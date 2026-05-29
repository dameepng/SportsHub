package com.example.sportshub.core.utils

import java.util.concurrent.Executor
import java.util.concurrent.Executors

class AppExecutors(
    private val diskIO: Executor = Executors.newSingleThreadExecutor()
) {

    fun diskIO(): Executor = diskIO
}
