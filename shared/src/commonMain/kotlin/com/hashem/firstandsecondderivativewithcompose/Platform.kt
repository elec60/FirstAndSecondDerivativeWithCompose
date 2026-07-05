package com.hashem.firstandsecondderivativewithcompose

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform