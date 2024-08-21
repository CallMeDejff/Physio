package com.physio

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform