package com.example.chatapp.di

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.chatapp.domain.chatUseCases.UpdateUserPresenceUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class PresenceManager(
    private val updateUserPresenceUseCase: UpdateUserPresenceUseCase
) : DefaultLifecycleObserver {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var presenceJob: Job? = null

    override fun onStart(owner: LifecycleOwner) {
        // App is in the foreground. Start periodically updating the timestamp.
        presenceJob = scope.launch {
            while (isActive) {
                updateUserPresenceUseCase()
                delay(30_000) // Update every 30 seconds
            }
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        // App is in the background. Stop the updates.
        presenceJob?.cancel()
    }
}