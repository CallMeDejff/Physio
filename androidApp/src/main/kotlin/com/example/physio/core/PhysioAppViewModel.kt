package com.example.physio.core

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.FirebaseFirestoreException.Code
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

open class PhysioAppViewModel : ViewModel() {

    protected val _message = MutableStateFlow<String?>(null)
    val message = _message.asStateFlow()

    protected val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun launchCatching(
        block: suspend CoroutineScope.() -> Unit,
        tag: String? = ERROR_TAG,
        errorMessage: String = "Ups! Coś poszło nie tak",
        onError: suspend (String) -> Unit = {}
    ) = viewModelScope.launch(
        CoroutineExceptionHandler { _, throwable ->
            var messageToEmit = errorMessage

            if (throwable is FirebaseFirestoreException) {
                messageToEmit = when (throwable.code) {
                    Code.UNAVAILABLE -> "Brak połączenia z siecią. Sprawdź swoje połączenie i spróbuj ponownie."
                    Code.DEADLINE_EXCEEDED -> "Połączenie z serwerem aplikacji wygasło. Spróbuj ponownie później."
                    else -> "Wystąpił błąd w komunikacji z serwerem."
                }
            }
            Log.d(tag, throwable.message.orEmpty())

            viewModelScope.launch {
                onError(messageToEmit)
            }
        },
        block = block
    )

    fun toggleItem(
        itemId: String,
        selectedItemsFlow: MutableStateFlow<Set<String>>,
        allowMultipleSelection: Boolean = true,
        itemType: String = "Item",
        tag: String = ""
    ) {
        selectedItemsFlow.update { selectedItems ->
            val newSet = if (allowMultipleSelection) {
                selectedItems.toMutableSet().apply {
                    if (contains(itemId)) {
                        remove(itemId)
                        Log.d(tag, "$itemType removed: $itemId")
                    } else {
                        add(itemId)
                        Log.d(tag, "$itemType added: $itemId")
                    }
                }
            } else {
                if (selectedItems.contains(itemId)) {
                    emptySet()
                } else {
                    setOf(itemId)
                }
            }
            Log.d(tag, "Selected $itemType: ${newSet.toList()}")
            newSet
        }
    }

    fun clearMessage() {
        _message.update { null }
    }

    companion object {
        const val ERROR_TAG = "PhysioApp error"
    }
}
