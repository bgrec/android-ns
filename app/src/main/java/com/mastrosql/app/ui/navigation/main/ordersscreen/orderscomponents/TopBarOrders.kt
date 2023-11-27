package com.mastrosql.app.ui.navigation.main.ordersscreen.orderscomponents

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.mastrosql.app.R
import com.mastrosql.app.ui.theme.MastroAndroidTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarArticles() {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.orders),
                style = MaterialTheme.typography.titleMedium,
                //color = colorResource(id = R.color.white)
            )
            // }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun TopBarPreview() {
    MastroAndroidTheme {
        TopBarArticles()
    }
}