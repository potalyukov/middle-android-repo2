package ru.yandex.praktikumchatapp.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.yandex.praktikumchatapp.data.ChatRepository

class ChatViewModel(
    val isWithReplies: Boolean = true
) : ViewModel() {

    private val repository = ChatRepository()

    private val _messages = MutableStateFlow(emptyList<Message>())
    val messages = _messages

    init {
        viewModelScope.launch {
            while (isWithReplies) {
                repository.getReplyMessage()
                    .catch { cause ->
                            Log.e("mainLog", "beda " + cause.message)
                    }
                    .collect { response ->
                    _messages.update {
                        it + Message.OtherMessage(response)
                    }
                }
            }
        }
    }

    fun sendMyMessage(messageText: String) =
        _messages.update {
            it + Message.MyMessage(messageText)
        }

}