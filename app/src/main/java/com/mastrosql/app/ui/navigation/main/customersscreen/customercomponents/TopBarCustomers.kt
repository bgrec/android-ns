package com.mastrosql.app.ui.navigation.main.customersscreen.customercomponents

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.mastrosql.app.R
import com.mastrosql.app.ui.theme.MastroAndroidPreviewTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarCustomers() {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.drawer_customers),
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
    MastroAndroidPreviewTheme {
        TopBarCustomers()
    }
}