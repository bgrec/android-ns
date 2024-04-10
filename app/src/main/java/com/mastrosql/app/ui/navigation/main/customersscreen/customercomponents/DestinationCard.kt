package com.mastrosql.app.ui.navigation.main.customersscreen.customercomponents

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mastrosql.app.R
import com.mastrosql.app.ui.navigation.main.customersscreen.model.destinations.DestinationData
import com.mastrosql.app.ui.navigation.main.customersscreen.model.destinations.Metadata

@Composable
fun DestinationCard(
    destinationData: DestinationData,
    onDestinationSelected: ((DestinationData) -> Unit)? = null,
    modifier: Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.padding(4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.widthIn(60.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    DestinationExpandButton(
                        expanded = expanded,
                        onClick = { expanded = !expanded },
                    )
                }
                //Spacer(Modifier.weight(0.5f))
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(text = destinationData.destinationName ?: "")


                    if (expanded) {
                        Text(text = destinationData.street ?: "")
                        Text(text = destinationData.postalCode ?: "")
                        Text(text = destinationData.city ?: "")
                        Text(text = destinationData.province ?: "")
                    }
                }
                //Spacer(Modifier.weight(1f))
                Column(
                    modifier = Modifier.widthIn(60.dp),
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {

                    SelectCustomerButton(
                        onClick = {
                            if (onDestinationSelected != null) {
                                onDestinationSelected(destinationData)
                            }
                        },
                    )

                }
            }
        }
    }
}

@Composable
private fun DestinationExpandButton(
    expanded: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick
    ) {
        Icon(
            imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
            tint = MaterialTheme.colorScheme.secondary,
            contentDescription = stringResource(R.string.expand_button_content_description),
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun SelectCustomerButton(
    onClick: () -> Unit, modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick
    ) {
        Icon(
            Icons.AutoMirrored.Filled.Send,
            tint = MaterialTheme.colorScheme.secondary,
            contentDescription = stringResource(R.string.insert_article),
            modifier = Modifier.size(35.dp)
        )
    }
}

@Preview
@Composable
fun DestinationsCardPreview() {
    DestinationCard(
        destinationData = DestinationData(
            1,
            123,
            "destinationName",
            "street",
            "postalCode",
            "city",
            "province",
            emptyList(),
            Metadata("aaa"),
            1,
            0L
        ),
        onDestinationSelected = { },
        modifier = Modifier
    )
}
