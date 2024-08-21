package com.example.physio.service.services

import com.example.physio.service.User
import kotlinx.coroutines.flow.Flow

interface StorageService {
    //val notes: Flow<List<Note>>
    suspend fun createUser(user: User)
    //suspend fun readNote(userId: String): User?
    //suspend fun updateNote(user: User)
    //suspend fun deleteNote(noteId: String)
}
