package com.mastrosql.app.ui.navigation.main.loadingscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mastrosql.app.R

/**
 * The home screen displaying the loading message.
 */

@Composable
fun LoadingScreen(
    modifier: Modifier = Modifier,
    loading: Boolean
) {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    )
    {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center

        ) {
            if (loading) {
                // Display a circular progress indicator while loading
                IndeterminateCircularIndicator()
            } else {
                Image(
                    modifier = modifier.size(200.dp),
                    painter = painterResource(R.drawable.loading_img),
                    contentDescription = stringResource(R.string.loading)
                )

            }
        }
    }
}


@Composable
fun IndeterminateCircularIndicator() {
    CircularProgressIndicator(
        modifier = Modifier.width(64.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        trackColor = MaterialTheme.colorScheme.secondary,
    )
}

@Preview(showBackground = true)
@Composable
fun LoadingScreenPreview() {
    val modifier = Modifier

    LoadingScreen(modifier, true)
}
