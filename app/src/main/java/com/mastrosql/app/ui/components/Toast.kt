package com.mastrosql.app.ui.components

import android.content.Context
import androidx.compose.runtime.Composable
import android.widget.Toast
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
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

