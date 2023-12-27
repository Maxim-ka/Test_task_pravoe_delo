package reschikov.test.pravoedelo.ui.screens.code

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetTokenViewModel @Inject constructor(private val iRepository: IRepository) : ViewModel() {

    private val uiState = mutableStateOf(UiState())
    private val tokenState  = mutableStateOf<Pair<String, String>?>(null)
    private var tel : String = ""

    fun getUiState() = uiState

    fun getToken() = tokenState

    fun sendPhone(phone : String) {
        if (phone.isEmpty()) return
        tel = "7$phone"
        uiState.value = uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            iRepository.getSMSCode(tel).run {
                uiState.value = uiState.value.copy(isLoading = false, error = second)
            }
        }
    }

    fun sendPhone() {
        if (tel.isEmpty()) return
        uiState.value = uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            iRepository.getSMSCode(tel).run {
                uiState.value = uiState.value.copy(isLoading = false, error = second)
            }
        }
    }

    fun sendCode(code: String) {
        if (code.isEmpty()) return
        uiState.value = uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            iRepository.getToken(tel, code).run {
                uiState.value = uiState.value.copy(isLoading = false, error = second)
                first?.let { tokenState.value = Pair(tel, it) }
            }
        }
    }
}