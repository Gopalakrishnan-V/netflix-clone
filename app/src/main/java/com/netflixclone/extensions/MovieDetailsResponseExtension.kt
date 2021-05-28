package com.netflixclone.extensions

import com.netflixclone.network.models.MovieDetailsResponse
import java.util.concurrent.TimeUnit

fun MovieDetailsResponse.getRunTime(): String? {
    if(this.runtime == null){
        return null;
    }
    val timeUnit = TimeUnit.MINUTES
    val day: Long = timeUnit.toDays(this.runtime.toLong())
    val hour: Long = timeUnit.toHours(this.runtime.toLong()) % 24
    val minute: Long = timeUnit.toMinutes(this.runtime.toLong()) % 60
    val second: Long = timeUnit.toSeconds(this.runtime.toLong()) % 60
    return when {
        day > 0 -> {
            String.format("%ddayd %02dh %02dm %02ds", day, hour, minute, second)
        }
        hour > 0 -> {
            String.format("%dh %02dm", hour, minute)
        }
        minute > 0 -> {
            String.format("%dm %02ds", minute, second)
        }
        else -> {
            String.format("%02ds", second)
        }
    }
}