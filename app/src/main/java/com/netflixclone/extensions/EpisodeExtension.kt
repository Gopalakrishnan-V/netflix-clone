package com.netflixclone.extensions

import com.netflixclone.constants.IMAGE_BASE_URL
import com.netflixclone.constants.ImageSize
import com.netflixclone.data_models.Episode

fun Episode.getStillUrl(size: ImageSize = ImageSize.ORIGINAL): String {
    return "$IMAGE_BASE_URL${size.value}${this.stillPath}"
}
