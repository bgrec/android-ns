package com.mastrosql.app.ui.navigation.main.ordersscreen.orderscomponents

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mastrosql.app.R
import com.mastrosql.app.ui.navigation.main.ordersscreen.model.Metadata
import com.mastrosql.app.ui.navigation.main.ordersscreen.model.Order
import com.mastrosql.app.ui.theme.MastroAndroidTheme
import com.mastrosql.app.utils.DateHelper

/**
 * Order card composable that displays the order information.
 */
@Composable
fun OrderCard(
    order: Order,
    modifier: Modifier,
    navigateToOrderDetails: (Int, String?) -> Unit,
    modifiedOrderId: MutableIntState?,
    showDeliveryDialog: MutableState<Boolean>
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
                    OrderDescriptionAndId(orderId = order.id,
                        description = order.description,
                        insertDate = DateHelper.formatDateToDisplay(order.insertDate),
                        businessName = order.businessName,
                        deliveryState = order.deliveryState,
                        onRowClick = {
                            modifiedOrderId?.intValue = order.id
                            showDeliveryDialog.value = true
                        })
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

                    OrderDetailsEditButton(
                        orderId = order.id,
                        orderDescription = order.description,
                        onEditClick = { orderId, orderDescription ->
                            //Setting the modifiedOrderId to the id of the order that was clicked
                            //this will change the color of the edit button
                            modifiedOrderId?.intValue = orderId

                            //Navigate to the order details screen
                            navigateToOrderDetails(orderId, orderDescription)

                        },
                        modifiedOrderId = modifiedOrderId?.intValue
                    )
                }
            }
        }
    }
}

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
    modifier: Modifier = Modifier,
    orderId: Int,
    orderDescription: String?,
    onEditClick: (Int, String?) -> Unit,
    modifiedOrderId: Int?
) {
    val defaultTint = MaterialTheme.colorScheme.secondary
    val orderEditButtonTint = remember { mutableStateOf(defaultTint) }

    LaunchedEffect(modifiedOrderId, orderId) {
        // Animate the color change when modifiedOrderId equals orderId
        if (modifiedOrderId == orderId) {
            orderEditButtonTint.value = Color.Red
        } else {
            orderEditButtonTint.value = defaultTint
        }
    }

    IconButton(onClick = {
        // Change the button color to red when clicked
        orderEditButtonTint.value = Color.Red
        onEditClick(orderId, orderDescription)

    }) {
        Icon(
            Icons.Default.Edit,
            //tint = MaterialTheme.colorScheme.secondary,
            tint = orderEditButtonTint.value,
            contentDescription = stringResource(R.string.insert_article),
            modifier = modifier.fillMaxSize()
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
    onRowClick: () -> Unit = {}
) {
    val deliveryStateObj = DeliveryStates.deliveryStates.find { it.state == deliveryState }

    Column(
        modifier = Modifier
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start
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
            modifier = Modifier.clickable(
                onClick = onRowClick
            ), horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = stringResource(R.string.order_deliveryType),
                style = MaterialTheme.typography.bodySmall,
            )
            deliveryStateObj?.let { state ->
                Text(
                    text = stringResource(state.nameState),
                    color = state.color,
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
 * @param   destinationName Int that represents the order id
 * @param  deliveryDate the String that represents the order sku
 */
@Composable
fun OrderInfo(
    modifier: Modifier = Modifier,
    destinationName: String?,
    deliveryDate: String?,
    carrierName: String? = "",
    notes: String? = null

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
            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
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
            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.order_carrierName),
                style = MaterialTheme.typography.bodySmall
            )
        }

        if (carrierName != "") {
            Row(
                modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
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
                modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.order_notes),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
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

/**
 * Preview for [OrderCard]
 */
@Preview(showBackground = true)
@Composable
fun OrderCardPreview() {
    MastroAndroidTheme {
        OrderCard(order = Order(
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
            // navController = NavController(LocalContext.current),
            navigateToOrderDetails = { _, _ -> },
            modifiedOrderId = remember { mutableIntStateOf(0) },
            showDeliveryDialog = remember { mutableStateOf(false) })
    }
}


/**
 * Preview for [OrderInfo]
 */
@Preview(showBackground = true)
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

