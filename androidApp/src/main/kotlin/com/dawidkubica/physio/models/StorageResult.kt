package com.dawidkubica.physio.models

sealed class StorageResult {
    data class Added(val packageId: String) : StorageResult()
    data class Removed(val packageId: String) : StorageResult()
    data class Failure(val error: Throwable) : StorageResult()
}
