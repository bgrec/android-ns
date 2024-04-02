package com.mastrosql.app.ui.navigation.main.ordersscreen.orderscomponents

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mastrosql.app.R
import com.mastrosql.app.ui.navigation.main.ordersscreen.model.Metadata
import com.mastrosql.app.ui.navigation.main.ordersscreen.model.Order
import com.mastrosql.app.ui.theme.ColorGreen
import com.mastrosql.app.ui.theme.ColorOrange
import com.mastrosql.app.ui.theme.ColorRed
import com.mastrosql.app.ui.theme.MastroAndroidTheme
import com.mastrosql.app.utils.DateHelper

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OrderCard(
    order: Order,
    modifier: Modifier,
    navController: NavController,
    navigateToOrderDetails: (Int, String?) -> Unit,
    modifiedOrderId: MutableState<Int>,
    showDeliveryDialog: MutableState<Boolean>
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .padding(4.dp),
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
                        orderId = order.id,
                        description = order.description,
                        insertDate = DateHelper.formatDateToDisplay(order.insertDate),
                        businessName = order.businessName,
                        deliveryState = order.deliveryState,
                        showDeliveryDialog = showDeliveryDialog,
                        modifiedOrderId = modifiedOrderId
                    )
                    if (expanded) {
                        OrderInfo(
                            destinationName = order.destinationName,
                            deliveryDate = DateHelper.formatDateToDisplay(order.deliveryDate),
                            carrierName = order.carrierName,
                            notes = order.notes
                        )
                    }
                }
                //Spacer(Modifier.weight(1f))
                Column(
                    modifier = Modifier.widthIn(60.dp),
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {
                    var tint = MaterialTheme.colorScheme.secondary
                    if (modifiedOrderId.value == order.id) {
                        tint = Color.Red
                    }
                    //Log.d("modifiedOrderId", "modifiedOrderId: $modifiedOrderId")
                    //Log.d("order.id", "order.id: ${order.id}")
                    OrderDetailsEditButton(
                        orderId = order.id,
                        orderDescription = order.description,
                        onEditClick = navigateToOrderDetails,
                        modifiedOrderId = modifiedOrderId,
                        tint = tint
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
    orderDescription: String?,
    onEditClick: (Int, String?) -> Unit,
    modifier: Modifier = Modifier,
    modifiedOrderId: MutableState<Int>,
    tint: Color
) {
    IconButton(onClick = {
        onEditClick(orderId, orderDescription)
        modifiedOrderId.value = orderId

    }) {

        Icon(
            Icons.Default.Edit,
            //tint = MaterialTheme.colorScheme.secondary,
            tint = tint,
            contentDescription = stringResource(R.string.insert_article),
            modifier = Modifier.fillMaxSize()
        )
    }
}

/**
 * Composable that displays a order business name and address.
 *
 * @param description is the resource ID for the string of the order description
 * @param orderId is the Int that represents the order id
 * @param modifier modifiers to set to this composable
 */

@Composable
fun OrderDescriptionAndId(
    orderId: Int,
    insertDate: String?,
    deliveryState: Int?,
    businessName: String?,
    description: String?,
    modifier: Modifier = Modifier,
    showDeliveryDialog: MutableState<Boolean>,
    modifiedOrderId: MutableState<Int>
) {

    Column(
        modifier = Modifier
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = stringResource(R.string.order_id),
                style = MaterialTheme.typography.bodySmall,
            )

            Text(
                text = orderId.toString(),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodySmall,
            )

            Spacer(modifier = Modifier.weight(0.5f))

            Text(
                text = stringResource(R.string.order_insertDate),
                style = MaterialTheme.typography.bodySmall,
            )

            Text(
                text = insertDate ?: "",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodySmall,
            )
        }


        Row(
            modifier = Modifier
                .clickable(
                    onClick = {
                        //Setting the modifiedOrderId to the id of the order that was clicked
                        //and showing the delivery dialog
                        modifiedOrderId.value = orderId
                        showDeliveryDialog.value = true
                    }),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = stringResource(R.string.order_deliveryType),
                style = MaterialTheme.typography.bodySmall,
            )

            when (deliveryState) {
                0 -> Text(
                    text = stringResource(R.string.order_deliveryState_value0),
                    color = ColorRed,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodySmall,
                )

                1 -> Text(
                    text = stringResource(R.string.order_deliveryState_value1),
                    color = ColorGreen,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodySmall,
                )

                2 -> Text(
                    text = stringResource(R.string.order_deliveryState_value2),
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodySmall,
                )

                3 -> Text(
                    text = stringResource(R.string.order_deliveryState_value3),
                    color = ColorOrange,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }

        Text(
            text = businessName?.take(50) ?: "",
            style = MaterialTheme.typography.titleSmall,
            modifier = modifier.padding(top = 4.dp)
        )

        Text(
            text = description?.take(50) ?: "",
            style = MaterialTheme.typography.titleMedium,
            modifier = modifier.padding(top = 4.dp)
        )
    }
}

/**
 * Composable that displays a order info
 * @param  is the Int that represents the order id
 * @param  is the String that represents the order sku
 */
@Composable
fun OrderInfo(
    destinationName: String?,
    deliveryDate: String?,
    carrierName: String?,
    notes: String?,
    modifier: Modifier = Modifier

) {
    Column(
        modifier = modifier.padding(
            top = 4.dp
        )
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.order_destinationName),
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = destinationName ?: "",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.order_deliveryDate),
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = deliveryDate ?: "",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodySmall
            )
        }


        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.order_carrierName),
                style = MaterialTheme.typography.bodySmall
            )
        }

        if (carrierName != "") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = carrierName ?: "",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        if (notes != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.order_notes),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = notes,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
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
                insertDate = "insertDate",
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
            ),
            modifier = Modifier,
            navController = NavController(LocalContext.current),
            navigateToOrderDetails = { _, _ -> },
            modifiedOrderId = remember { mutableIntStateOf(0) },
            showDeliveryDialog = remember { mutableStateOf(false) }
        )
    }
}


@Preview(apiLevel = 33)
@Composable
fun OrderInfoPreview() {
    MastroAndroidTheme {
        OrderInfo(
            destinationName = "destinationName",
            deliveryDate = "deliveryDate",
            carrierName = "carrierName",
            notes = "notes",
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