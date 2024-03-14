package com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.orderdetailscomponents

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mastrosql.app.R
import com.mastrosql.app.ui.components.formatDate
import com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.model.OrderDetailsItem
import com.mastrosql.app.ui.theme.ColorLightBlue
import com.mastrosql.app.ui.theme.ColorOrange
import com.mastrosql.app.ui.theme.ColorRedFleryRose
import com.mastrosql.app.ui.theme.MastroAndroidTheme
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OrderDetailsItem(
    orderDetailsItem: OrderDetailsItem,
    modifier: Modifier,
    navController: NavController,
    navigateToEditItem: (Int) -> Unit,
    onRemove: (Int) -> Unit,
    showEditDialog: MutableState<Boolean>,
    snackbarHostState: SnackbarHostState,
    listState: LazyListState,
    modifiedItemId: Int?
) {

    val visibleState = remember { MutableTransitionState(true) }

    AnimatedVisibility(
        visibleState = visibleState,
        enter = slideInHorizontally(
            animationSpec = tween(durationMillis = 500),
            initialOffsetX = { distance -> distance * -2 }),
        exit = slideOutHorizontally(
            animationSpec = tween(durationMillis = 500),
            targetOffsetX = { it / -96 })
    ) {
        SwipeToDismissItem(
            visibleState = visibleState,
            orderDetailsItem = orderDetailsItem,
            modifier = modifier,
            navController = navController,
            navigateToEditItem = navigateToEditItem,
            onRemove = onRemove,
            showEditDialog = showEditDialog,
            modifiedItemId = modifiedItemId
        )
    }

    val scope = rememberCoroutineScope()

    val messageText = stringResource(R.string.deleted_item_snackbar_text)
    val dismissText = stringResource(R.string.dismiss_button)

    LaunchedEffect(visibleState.targetState) {
        if (!visibleState.targetState) {
            scope.launch {
                val result = snackbarHostState.showSnackbar(
                    message = messageText,
                    actionLabel = dismissText,
                    duration = SnackbarDuration.Short
                )
                when (result) {
                    SnackbarResult.ActionPerformed -> {
                        visibleState.targetState = true

                        if (listState.firstVisibleItemScrollOffset == 0) {
                            listState.animateScrollToItem(listState.firstVisibleItemScrollOffset)
                        }
                    }

                    SnackbarResult.Dismissed -> {
                        onRemove(orderDetailsItem.id)
                    }
                }
            }
        }
    }

}/*
// Call the SwipeToDismissItem function with an implementation of onRemove
SwipeToDismissItem(
    visibleState = visibleState,
    orderDetailsItem = orderDetailsItem,
    modifier = modifier,
    navController = navController,
    navigateToEditItem = navigateToEditItem,
    onRemove = { item ->
        // Implement the logic to remove the item
        // If the item is successfully removed, return true, otherwise return false
        // For example:
        // val removedSuccessfully = removeItemFromServer(item)
        // removedSuccessfully
    }
)
 */

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
private fun SwipeToDismissItem(
    visibleState: MutableTransitionState<Boolean>,
    orderDetailsItem: OrderDetailsItem,
    modifier: Modifier,
    navController: NavController,
    navigateToEditItem: (Int) -> Unit,
    onRemove: (Int) -> Unit,
    showEditDialog: MutableState<Boolean>,
    modifiedItemId: Int?
) {
    val dismissState = rememberSwipeToDismissBoxState(confirmValueChange = {

        //Only for end to start action
        if (it == SwipeToDismissBoxValue.EndToStart) {
            visibleState.targetState = false
            true
        } else false

    }, positionalThreshold = { distance -> distance * 0.5f })

    SwipeToDismissBox(state = dismissState,
        modifier = Modifier,
        enableDismissFromEndToStart = true,
        enableDismissFromStartToEnd = true,
        backgroundContent = {
            SwipeToDismissBackground(
                dismissState = dismissState,
            )
        },
        content = {
            OrderDetailsItemContent(
                orderDetail = orderDetailsItem,
                modifier = modifier,
                navController = navController,
                navigateToEditItem = navigateToEditItem,
                showEditDialog = showEditDialog,
                modifiedItemId = modifiedItemId
            )
        })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToDismissBackground(
    dismissState: SwipeToDismissBoxState
) {
    val direction = dismissState.dismissDirection
    val color by animateColorAsState(
        when (dismissState.targetValue) {
            SwipeToDismissBoxValue.Settled -> Color.Transparent//colorScheme.background
            SwipeToDismissBoxValue.StartToEnd -> ColorOrange
            SwipeToDismissBoxValue.EndToStart -> ColorRedFleryRose
        }, label = "swipe_color"
    )
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
            .padding(16.dp, 2.dp)
            .fillMaxHeight(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (direction == SwipeToDismissBoxValue.StartToEnd) {
            Icon(
                imageVector = Icons.Default.EditNote,
                tint = MaterialTheme.colorScheme.secondary,
                contentDescription = stringResource(id = R.string.delete),
                modifier = Modifier.fillMaxHeight()
            )
        }

        Spacer(Modifier)

        if (direction == SwipeToDismissBoxValue.EndToStart) {
            Icon(
                Icons.Default.Delete,
                tint = MaterialTheme.colorScheme.secondary,
                contentDescription = stringResource(id = R.string.order_details_edit),
                modifier = Modifier.fillMaxHeight()
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun OrderDetailsItemContent(
    orderDetail: OrderDetailsItem,
    modifier: Modifier,
    navController: NavController,
    navigateToEditItem: (Int) -> Unit,
    showEditDialog: MutableState<Boolean>,
    modifiedItemId: Int?,
) {

    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.padding(4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ), verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OrderDetailDescriptionAndId(
                        articleId = orderDetail.articleId,
                        sku = orderDetail.sku,
                        description = orderDetail.description
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.widthIn(60.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OrderDetailExpandButton(
                        expanded = expanded,
                        onClick = { expanded = !expanded },
                    )
                }
                //Spacer(Modifier.weight(0.5f))
                Column(
                    modifier = Modifier.weight(1f), horizontalAlignment = Alignment.Start
                ) {
                    OrderDetailDescriptionAndId2(
                        batch = orderDetail.batch,
                        expirationDate = formatDate(orderDetail.expirationDate),
                        quantity = orderDetail.quantity,
                        orderedQuantity = orderDetail.orderedQuantity,
                        shippedQuantity = orderDetail.shippedQuantity
                    )
                    if (expanded) {
                        OrderDetailInfo(
                            completeDescription = orderDetail.completeDescription
                        )
                    }
                }

                Column(
                    modifier = Modifier.widthIn(60.dp),
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {
                    var tint = MaterialTheme.colorScheme.secondary
                    if (modifiedItemId == orderDetail.id) {
                        tint = Color.Red
                    }

                    ItemEditButton(
                        tint = tint,
                        onClick = {
                            showEditDialog.value = true
                        },
                        modifier = modifier,
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
private fun OrderDetailExpandButton(
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
private fun ItemEditButton(
    tint: Color, onClick: () -> Unit, modifier: Modifier = Modifier
) {
    IconButton(onClick = {
        onClick()
    }) {
        Icon(
            Icons.Default.Edit,
            tint = tint,
            contentDescription = stringResource(R.string.edit_button_order_item),
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun OrderDetailDescriptionAndId(
    articleId: Int, sku: String?, description: String?, modifier: Modifier = Modifier
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start
        ) {

            Text(
                text = stringResource(R.string.order_detail_articleId),
                style = MaterialTheme.typography.bodySmall,
            )

            Text(
                text = articleId.toString(),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodySmall,
            )

            Spacer(modifier = Modifier.weight(0.5f))

            Text(
                text = stringResource(R.string.order_detail_sku),
                style = MaterialTheme.typography.bodySmall,
            )

            Text(
                text = sku ?: "",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start
        ) {

            Text(
                text = description?.take(50) ?: "", style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun OrderDetailDescriptionAndId2(
    batch: String?,
    expirationDate: String?,
    quantity: Double,
    orderedQuantity: Double,
    shippedQuantity: Double,
    modifier: Modifier = Modifier
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = stringResource(R.string.order_detail_batch),
                style = MaterialTheme.typography.bodyMedium,
            )
            if (batch != null) {
                Text(
                    text = batch,
                    color = ColorLightBlue,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Spacer(Modifier.weight(0.5f))

            Text(
                text = stringResource(R.string.order_detail_expirationDate),
                style = MaterialTheme.typography.bodyMedium,
            )

            if (expirationDate != null) {
                Text(
                    text = expirationDate,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
        QuantityTable(quantity, orderedQuantity, shippedQuantity)
    }
}

@Composable
fun OrderDetailInfo(
    completeDescription: String?, modifier: Modifier = Modifier

) {
    Column(
        modifier = modifier.padding(
            top = 4.dp
        )
    ) {
        if (completeDescription != null) {
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = stringResource(R.string.order_detail_completeDescription),
                    style = MaterialTheme.typography.bodySmall,
                )
                Text(
                    text = completeDescription,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}


@Composable
fun QuantityTable(
    quantity: Double, orderedQuantity: Double, shippedQuantity: Double
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalDivider(modifier = Modifier.fillMaxWidth(), color = Color.Black)
            Row(
                Modifier
                    .weight(1f)
                    .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                VerticalDivider(
                    color = Color.Black, modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                )
                QuantityText(
                    stringResource(R.string.order_detail_quantity), false, Modifier.weight(1f)
                )

                VerticalDivider(
                    color = Color.Black, modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                )

                QuantityText(
                    stringResource(R.string.order_detail_orderedQuantity),
                    false,
                    Modifier.weight(1f)
                )

                VerticalDivider(
                    color = Color.Black, modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                )

                QuantityText(
                    stringResource(R.string.order_detail_shippedQuantity),
                    false,
                    Modifier.weight(1f)
                )

                VerticalDivider(
                    color = Color.Black, modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                )
            }

            HorizontalDivider(modifier = Modifier.fillMaxWidth(), color = Color.Black)

            Row(
                Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                VerticalDivider(
                    color = Color.Black, modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                )

                QuantityText(quantity.toString(), true, Modifier.weight(1f))

                VerticalDivider(
                    color = Color.Black, modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                )

                QuantityText(orderedQuantity.toString(), true, Modifier.weight(1f))

                VerticalDivider(
                    color = Color.Black, modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                )

                QuantityText(shippedQuantity.toString(), true, Modifier.weight(1f))

                VerticalDivider(
                    color = Color.Black, modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                )
            }
            HorizontalDivider(modifier = Modifier.fillMaxWidth(), color = Color.Black)
        }
    }
}

@Composable
fun QuantityText(
    text: String, bold: Boolean = false, modifier: Modifier
) {
    Text(
        modifier = modifier,
        text = text,
        fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center
    )
}


/*@Preview
@Composable
fun OrderDetailCardPreview() {
    MastroAndroidTheme {
        OrderDetailCard(
            orderDetail = OrderDetailsItem(
                id = 1,
                orderId = 1,
                orderRow = 1,
                confirmed = true,
                articleId = 1,
                sku = "sku",
                vendorSku = "vendorSku",
                description = "description",
                completeDescription = "completeDescription",
                quantity = 1.0,
                tmpQuantity = 1.0,
                returnedQuantity = 1.0,
                weight = 1.0,
                cost = "1.0",
                price = "1.0",
                vat = "vat",
                vatValue = 1.0,
                discount = 1.0,
                discount1 = 1.0,
                discount2 = 1.0,
                discount3 = 1.0,
                catalogPrice = 1.0,
                measureUnit = "measureUnit",
                rowType = 1,
                packSize = "packSize",
                orderedQuantity = 1.0,
                shippedQuantity = 1.0,
                batch = "batch",
                expirationDate = "2023-01-01",
                links = listOf(),
                metadata = Metadata(
                    etag = "etag"
                ),

                page = 0,
                lastUpdated = System.currentTimeMillis()
            ), modifier = Modifier, navController = NavController(LocalContext.current)
        )
    }

}*/

@Preview(apiLevel = 33, showBackground = true)
@Composable
fun OrderDetailInfoPreview() {
    MastroAndroidTheme {
        OrderDetailInfo(
            completeDescription = "completeDescription", modifier = Modifier
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