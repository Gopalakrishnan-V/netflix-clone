package com.netflixclone.extensions

import com.netflixclone.data_models.Media

fun Media.getId(): Int {
    return when (this) {
        is Media.Movie -> id
        is Media.Tv -> id
        else -> -1
    }
}