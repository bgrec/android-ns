package com.mastrosql.app.ui.components

import android.content.Context
import androidx.compose.runtime.Composable
import android.widget.Toast
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import kotlinx.coroutines.launch
@Composable
fun ShowToast (context: Context, text: String){
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
        }
    }
}

@Preview
@Composable
fun ShowToastPreview(@PreviewParameter(ToastTextProvider::class) text: String = "Anteprima del toast") {
    val context = LocalContext.current
    ShowToast(context, text)
}
class ToastTextProvider : PreviewParameterProvider<String> {
    override val values: Sequence<String> = sequenceOf("Hello", "Jetpack Compose", "Preview")
}