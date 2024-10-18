package com.example.physio.models

sealed class FavoritePackageResult {
    data class Added(val packageId: String) : FavoritePackageResult()
    data class Removed(val packageId: String) : FavoritePackageResult()
    data class Failure(val error: Throwable) : FavoritePackageResult()
}
