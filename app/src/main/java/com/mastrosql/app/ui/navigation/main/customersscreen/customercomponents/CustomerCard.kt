package com.mastrosql.app.ui.navigation.main.customersscreen.customercomponents

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.ShoppingCart
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mastrosql.app.R
import com.mastrosql.app.ui.navigation.main.customersscreen.model.CustomerMasterData
import com.mastrosql.app.ui.navigation.main.customersscreen.model.Metadata
import com.mastrosql.app.ui.theme.MastroAndroidTheme

@Composable
fun CustomerCard(
    customerMasterData: CustomerMasterData, modifier: Modifier, navController: NavController
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
                    CustomerExpandButton(
                        expanded = expanded,
                        onClick = { expanded = !expanded },
                    )
                }
                //Spacer(Modifier.weight(0.5f))
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.Start
                ) {
                    CustomerNameAndId(
                        id = customerMasterData.id,
                        businessName = customerMasterData.businessName
                    )
                    if (expanded) {
                        CustomerAddress(
                            street = customerMasterData.street,
                            postalCode = customerMasterData.postalCode,
                            vat = customerMasterData.vat,
                            city = customerMasterData.city,
                            province = customerMasterData.province,
                            nation = customerMasterData.nation,
                        )
                    }
                }
                //Spacer(Modifier.weight(1f))
                Column(
                    modifier = Modifier.widthIn(60.dp),
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {
                    CustomerNewOrderButton(
                        onClick = { //TO-DO
                        },
                    )
                }
            }
        }
    }
}

/**
 * Composable that displays a button that is clickable and displays an expand more or an expand less
 * icon.
 *
 * @param expanded represents whether the expand more or expand less icon is visible
 * @param onClick is the action that happens when the button is clicked
 * @param modifier modifiers to set to this composable
 */

@Composable
private fun CustomerExpandButton(
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
private fun CustomerNewOrderButton(
    onClick: () -> Unit, modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick
    ) {
        Icon(
            Icons.Default.ShoppingCart,
            tint = MaterialTheme.colorScheme.secondary,
            contentDescription = stringResource(R.string.new_order),
            modifier = Modifier.fillMaxSize()
        )
    }
}

/**
 * Composable that displays a customer business name and address.
 *
 * @param businessName is the resource ID for the string of the customer business name
 * @param id is the Int that represents the customer id
 * @param modifier modifiers to set to this composable
 */

@Composable
fun CustomerNameAndId(
    id: Int, businessName: String?, modifier: Modifier = Modifier
) {
    Column {
        Text(
            text = stringResource(R.string.customer_id, id),
            style = MaterialTheme.typography.bodySmall,
        )
        Text(
            text = businessName?.take(50) ?: "",
            style = MaterialTheme.typography.titleLarge,
            modifier = modifier.padding(top = 4.dp)
        )

    }
}

/**
 * Composable that displays a customer address.
 *
 * @param street is the string of the customer street address
 * @param modifier modifiers to set to this composable
 */
@Composable
fun CustomerAddress(
    street: String?,
    postalCode: String?,
    vat: String?,
    city: String?,
    province: String?,
    nation: String?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(
            top = 4.dp
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = street ?: "", style = MaterialTheme.typography.bodySmall
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$postalCode $city $province $nation",
                style = MaterialTheme.typography.bodySmall
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.vat, vat ?: ""),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}


@Preview
@Composable
fun CustomerCardPreview() {
    MastroAndroidTheme {
        CustomerCard(
            customerMasterData = CustomerMasterData(
                1,
                "businessName -----------------------------------",
                "street",
                "postalCode",
                "vat",
                "city",
                "province",
                "nation",
                "businessName2",
                emptyList(),
                "taxId",
                Metadata("etag"),
                0,
                0L
            ), modifier = Modifier, navController = NavController(LocalContext.current)
        )
    }

}

@Preview
@Composable
fun CustomerAddressPreview() {
    MastroAndroidTheme {
        CustomerAddress(
            street = "street",
            postalCode = "postalCode",
            vat = "vat",
            city = "city",
            province = "province",
            "nation",
            modifier = Modifier.padding(4.dp)
        )
    }
}

/*
@Composable
fun AffirmationCard(affirmation: Affirmation, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column {
            Image(
                painter = painterResource(affirmation.imageResourceId),
                contentDescription = stringResource(affirmation.stringResourceId),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(194.dp),
                contentScale = ContentScale.Crop
            )
            Text(
                text = LocalContext.current.getString(affirmation.stringResourceId),
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}
 */

/*/**
 * Composable that displays a photo of a dog.
 *
 * @param dogIcon is the resource ID for the image of the dog
 * @param modifier modifiers to set to this composable
 */
@Composable
fun DogIcon(@DrawableRes dogIcon: Int, modifier: Modifier = Modifier) {
    Image(
        modifier = modifier
            .size(64.dp)
            .padding(8.dp)
            .clip(RoundedCornerShape(50)),
        contentScale = ContentScale.Crop,
        painter = painterResource(dogIcon),
        /*
         * Content Description is not needed here - image is decorative, and setting a null content
         * description allows accessibility services to skip this element during navigation.
         */
        contentDescription = null
    )
}*/