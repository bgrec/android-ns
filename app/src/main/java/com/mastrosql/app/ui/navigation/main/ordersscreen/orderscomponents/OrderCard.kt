package com.mastrosql.app.ui.navigation.main.ordersscreen.orderscomponents

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
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mastrosql.app.R
import com.mastrosql.app.ui.navigation.main.ordersscreen.model.Metadata
import com.mastrosql.app.ui.navigation.main.ordersscreen.model.Order
import com.mastrosql.app.ui.theme.MastroAndroidTheme

@Composable
fun OrderCard(
    order: Order,
    modifier: Modifier,
    navController: NavController,
    navigateToOrderDetails: (Int, String) -> Unit

) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.padding(4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow
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
                    OrderExpandButton(
                        expanded = expanded,
                        onClick = { expanded = !expanded },
                    )
                }
                //Spacer(Modifier.weight(0.5f))
                Column(
                    modifier = Modifier.weight(1f), horizontalAlignment = Alignment.Start
                ) {
                    OrderDescriptionAndId(
                        id = order.id, description = order.description
                    )
                    if (expanded) {
                        OrderInfo(
                            id = order.id,
                            sku = order.clientId.toString(),
                            vendorSku = order.businessName,
                            description = order.description,
                            vat = order.date,
                            measureUnit = order.city,
                            price = order.weight
                        )
                    }
                }
                //Spacer(Modifier.weight(1f))
                Column(
                    modifier = Modifier.widthIn(60.dp),
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {
                    OrderDetailsEditButton(
                        orderId = order.id,
                        orderDescription = order.description,
                        onEditClick = navigateToOrderDetails
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
private fun OrderExpandButton(
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
private fun OrderDetailsEditButton(
    orderId: Int,
    orderDescription: String,
    onEditClick: (Int, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(onClick = { onEditClick(orderId, orderDescription ) }) {
        Icon(
            Icons.Default.Edit,
            tint = MaterialTheme.colorScheme.secondary,
            contentDescription = stringResource(R.string.new_order),
            modifier = Modifier.fillMaxSize()
        )
    }
}

/**
 * Composable that displays a order business name and address.
 *
 * @param description is the resource ID for the string of the order description
 * @param id is the Int that represents the order id
 * @param modifier modifiers to set to this composable
 */

@Composable
fun OrderDescriptionAndId(
    id: Int, description: String?, modifier: Modifier = Modifier
) {
    Column {
        Text(
            text = stringResource(R.string.order_id, id),
            style = MaterialTheme.typography.bodySmall,
        )
        Text(
            text = description?.take(50) ?: "",
            style = MaterialTheme.typography.titleLarge,
            modifier = modifier.padding(top = 4.dp)
        )
    }
}

/**
 * Composable that displays a order info
 * @param id is the Int that represents the order id
 * @param sku is the String that represents the order sku
 */
@Composable
fun OrderInfo(
    id: Int,
    sku: String?,
    vendorSku: String?,
    description: String?,
    vat: String?,
    measureUnit: String?,
    price: Double,
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
                text = description ?: "", style = MaterialTheme.typography.bodySmall
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$id $sku $measureUnit $price", style = MaterialTheme.typography.bodySmall
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


@Preview(apiLevel = 33)
@Composable
fun OrderCardPreview() {
    MastroAndroidTheme {
        OrderCard(
            order = Order(
            id = 1,
            clientId = 1,
            businessName = "businessName",
            street = "street",
            postalCode = "postalCode",
            city = "city",
            province = "province",
            nation = "nation",
            destinationId = 1,
            destinationName = "destinationName",
            description = "description",
            sequence = 1,
            insertDate = "",
            agent = "agent",
            transportHandler = "transportHandler",
            parcels = 1,
            carrierId = 1,
            carrierName = "carrierName",
            weight = 1.0,
            port = "port",
            date = "2023-01-01",
            notes = "notes",
            deliveryDate = "2023-01-0",
            deliveryDeadline = true,
            deliveryType = 1,
            deliveryState = 1,
            urgent = true,
            partial = 1,
            number = 1,

            links = emptyList(),
            metadata = Metadata("etag"),
            page = 0,
            lastUpdated = System.currentTimeMillis()
        ), modifier = Modifier, navController = NavController(LocalContext.current)
        ) { _, _ -> }
    }

}

@Preview(apiLevel = 33)
@Composable
fun OrderInfoPreview() {
    MastroAndroidTheme {
        OrderInfo(
            id = 1,
            sku = "sku",
            vendorSku = "vendorSku",
            description = "description",
            vat = "vat",
            measureUnit = "measureUnit",
            price = 1.0,
            modifier = Modifier
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