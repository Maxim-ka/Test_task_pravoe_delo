package reschikov.test.pravoedelo.ui.screens.code

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import reschikov.test.pravoedelo.domain.AppException

private const val PHONE_FORMAT_NUMBER_LEN: Int = 18
private const val PHONE_NUMBER_LEN: Int = 10
private const val PHONE_PREFIX: String = "+7 ("

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GetTokenScreen(
    viewModel: GetTokenViewModel = hiltViewModel(),
    gotoNext: (String, String) -> Unit
){

    val fieldPhone = rememberSaveable { mutableStateOf("") }
    val fieldCode = rememberSaveable { mutableStateOf("") }
    val isErrorCode = rememberSaveable { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    var isRequested by rememberSaveable { mutableStateOf(false) }
    var isVisibleButton by rememberSaveable { mutableStateOf(false) }
    val uiState = viewModel.getUiState()
    val token = viewModel.getToken()

    LaunchedEffect(key1 = uiState.value.error) {
        if (uiState.value.error != null && uiState.value.error !is AppException.ValidationError) {
            snackbarHostState.showSnackbar(
                message = if (uiState.value.error == AppException.NoInternet) {
                    "Нет интернета"
                } else {
                    uiState.value.error?.message ?: "Ошибка"
                },
                actionLabel = "Закрыть",
                withDismissAction = true,
                duration = SnackbarDuration.Indefinite
            )
        }
        isErrorCode.value = uiState.value.error is AppException.ValidationError
    }

    LaunchedEffect(key1 = token.value) {
        token.value?.let {
            gotoNext(it.first, it.second)
            token.value = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(text = "Получение СМС кода", modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                    style = MaterialTheme.typography.titleMedium)
            })
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                ShowMessage(data = data) { viewModel.sendPhone(fieldPhone.value) }
            }
        }
    ) { pv ->
        Box(contentAlignment = Alignment.Center) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(pv)
                    .padding(horizontal = 16.dp)) {
                InputPhone(field = fieldPhone, modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)) {
                    viewModel.sendPhone(it)
                    isRequested = true
                }
                if (isRequested) {
                    InputTextNumber(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        field = fieldCode,
                        isError = isErrorCode) { viewModel.sendCode(it) }
                    if (isErrorCode.value || isVisibleButton) {
                        Button(
                            onClick = {
                                viewModel.sendPhone()
                                fieldCode.value = ""
                                isVisibleButton = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Text(text = "Запросить новый код")
                        }
                        isVisibleButton = true
                    }
                }
            }
            if (uiState.value.isLoading) {
                CircularProgressIndicator()
            }
        }
    }

}

@Composable
fun InputPhone(
    modifier: Modifier = Modifier,
    field: MutableState<String>,
    done: (String) -> Unit
) {

    val focusManager = LocalFocusManager.current
    val sb = remember { StringBuilder(PHONE_FORMAT_NUMBER_LEN) }
    var isError by rememberSaveable { mutableStateOf(false) }

    TextField(
        value = field.value,
        onValueChange = { s ->
            field.value = s.checkNumber(PHONE_NUMBER_LEN)
            isError = field.value.isEmpty() || field.value.length < PHONE_NUMBER_LEN
        },
        modifier = modifier,
        label = { Text(text = "введите свой номер телефона") },
        isError = isError,
        visualTransformation = { str ->
            sb.replace(0, sb.length, str.text)
            sb.indexOf('+').takeIf { it != -1 }?.let {
                if (it == 0) sb.clear()
                else sb.replace(0, sb.length, sb.substring(0, it))
            }
            if (sb.isNotEmpty()) sb.replace(0, sb.length, sb.replace("\\D".toRegex(), ""))
            checkFormatPhoneNumber(sb)
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Phone,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                if (!isError) {
                    done(field.value)
                    focusManager.clearFocus()
                }
            }
        )
    )
    if (isError) {
        Text(text = if (field.value.isEmpty()) "Поле не заполнено" else "Номер не полный", color = MaterialTheme.colorScheme.error)
    }
}

private fun checkFormatPhoneNumber(sb: StringBuilder): TransformedText {
    if (sb.isNotEmpty()) {
        sb.insert(0, PHONE_PREFIX)
        if (sb.length >= 7) sb.insert(7, ") ")
        if (sb.length >= 12) sb.insert(12, "-")
        if (sb.length >= 15) sb.insert(15, "-")
    }
    val phone =
        if (sb.length > PHONE_FORMAT_NUMBER_LEN) sb.substring(0,PHONE_FORMAT_NUMBER_LEN)
        else sb.toString()
    return TransformedText(
        AnnotatedString.Builder(phone).toAnnotatedString(), offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset == 0) return 0
                if (offset <= 2) return offset + 4
                if (offset <= 5) return offset + 6
                if (offset <= 7) return offset + 7
                if (offset <= PHONE_NUMBER_LEN) return offset + 8
                return PHONE_FORMAT_NUMBER_LEN
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 4) return 0
                if (offset <= 6) return offset - 4
                if (offset <= 7) return offset - 6
                if (offset <= 14) return offset - 7
                if (offset <= PHONE_FORMAT_NUMBER_LEN) return offset - 8
                return PHONE_NUMBER_LEN
            }
        }
    )
}

fun String.checkNumber(limit: Int? = null): String {
    if(this == PHONE_PREFIX) return ""
    val sb = ((if (endsWith(PHONE_PREFIX)) substring(0, indexOf(PHONE_PREFIX)) else this).checkNumber())
    return limit?.let {
        if (sb.length <= it) sb else sb.substring(0, it)
    } ?: sb
}

fun String.checkNumber(): String = filter { ch -> ch.isDigit() }

@Composable
fun ShowMessage(data: SnackbarData, onClose: () -> Unit){

    Snackbar(
        modifier = Modifier.padding(12.dp),
        action = {
            TextButton(
                onClick = {
                    onClose()
                    data.performAction()
                }
            ) { Text(data.visuals.actionLabel ?: "") }
        }
    ) {
        Text(data.visuals.message)
    }
}

@Composable
fun InputTextNumber(
    modifier: Modifier = Modifier,
    field: MutableState<String>,
    isError: MutableState<Boolean>,
    takeCode: (String) -> Unit
) {

    val focusManager = LocalFocusManager.current

    TextField(
        value = field.value,
        onValueChange = { str ->
            field.value = str.checkNumber()
            if (isError.value) isError.value = false
        },
        modifier = modifier,
        isError = isError.value,
        label = { Text(text = "введите полученный код") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {
            if (field.value.isNotEmpty() && !isError.value) {
                takeCode(field.value)
                focusManager.clearFocus()
            }
        })        
    )
    if (isError.value) {
        Text(text = "неверный код", color = MaterialTheme.colorScheme.error)
    }
}