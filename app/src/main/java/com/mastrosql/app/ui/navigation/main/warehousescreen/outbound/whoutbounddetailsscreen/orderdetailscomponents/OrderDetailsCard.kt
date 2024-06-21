package com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.whoutbounddetailsscreen.orderdetailscomponents

import androidx.compose.animation.Animatable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyListState
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
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mastrosql.app.R
import com.mastrosql.app.data.local.SwipeActionsPreferences
import com.mastrosql.app.ui.theme.ColorLightBlue
import com.mastrosql.app.ui.theme.ColorOrange
import com.mastrosql.app.ui.theme.ColorRedFleryRose
import com.mastrosql.app.ui.theme.MastroAndroidPreviewTheme
import com.mastrosql.app.utils.DateHelper
import kotlinx.coroutines.launch

/**
 * OrderDetailsCard composable to display the order details card
 */
@Composable
fun OrderDetailsCard(
    modifier: Modifier,
    orderDetailsItem: com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.model.OrderDetailsItem,
    showEditDialog: MutableState<Boolean>,
    snackbarHostState: SnackbarHostState,
    listState: LazyListState,
    modifiedItemId: MutableIntState?,
    onRemove: (Int) -> Unit,
    onDuplicate: (Int) -> Unit,
    swipeActionsPreferences: SwipeActionsPreferences
) {

    // CoroutineScope to launch the snackbar
    val coroutineScope = rememberCoroutineScope()

    // MutableTransitionState to handle the visibility of the card
    val visibleState = remember { MutableTransitionState(true) }

    // Message text for the snackbar when the item is deleted
    val messageText = stringResource(R.string.deleted_item_snackbar_text)
    // Dismiss text for the snackbar
    val dismissText = stringResource(R.string.dismiss_button)

    val isDeleteDisabled = rememberSaveable {
        mutableStateOf(swipeActionsPreferences.isDeleteDisabled)
    }
    val isDuplicateDisabled = rememberSaveable {
        mutableStateOf(swipeActionsPreferences.isDuplicateDisabled)
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        AnimatedVisibility(
            visibleState = visibleState,
            enter = expandIn(animationSpec = tween(700)),
            exit = shrinkOut(animationSpec = tween(700)) + fadeOut(),
        ) {
            SwipeToDismissItem(
                modifier = modifier,
                visibleState = visibleState,
                orderDetailsItem = orderDetailsItem,
                isDeleteRowDisabled = swipeActionsPreferences.isDeleteDisabled,
                isDuplicateRowDisabled = swipeActionsPreferences.isDuplicateDisabled,
                showEditDialog = showEditDialog,
                modifiedItemId = modifiedItemId,
                onDuplicate = onDuplicate,
            )
        }
    }

    if (!visibleState.targetState && visibleState.isIdle) {
        LaunchedEffect(visibleState.targetState) {
            coroutineScope.launch {
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
                        // Ensure that removal only happens if the row is not visible
                        if (!visibleState.currentState) {
                            onRemove(orderDetailsItem.id)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToDismissItem(
    modifier: Modifier,
    visibleState: MutableTransitionState<Boolean>,
    orderDetailsItem: com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.model.OrderDetailsItem,
    isDeleteRowDisabled: Boolean,
    isDuplicateRowDisabled: Boolean,
    showEditDialog: MutableState<Boolean>,
    modifiedItemId: MutableIntState?,
    onDuplicate: (Int) -> Unit,
) {
    val dismissState = rememberSwipeToDismissBoxState(confirmValueChange = {
        //Swipe actions
        when (it) {
            SwipeToDismissBoxValue.EndToStart -> {
                visibleState.targetState = false
                true
            }

            SwipeToDismissBoxValue.StartToEnd -> {
                onDuplicate(orderDetailsItem.id)
                false
            }

            else -> false
        }

    }, positionalThreshold = { distance -> distance * 0.55f })


    SwipeToDismissBox(state = dismissState,
        modifier = Modifier,
        enableDismissFromEndToStart = !isDeleteRowDisabled,
        enableDismissFromStartToEnd = !isDuplicateRowDisabled,
        backgroundContent = {
            SwipeToDismissBackground(
                dismissState = dismissState,
            )
        },
        content = {
            OrderDetailsItemContent(
                orderDetailsItem = orderDetailsItem,
                modifier = modifier,
                showEditDialog = showEditDialog,
                modifiedItemId = modifiedItemId
            )
        })
}

@ExperimentalMaterial3Api
@Composable
private fun SwipeToDismissBackground(
    dismissState: SwipeToDismissBoxState
) {
    val direction = dismissState.dismissDirection
    val color by animateColorAsState(
        when (dismissState.targetValue) {
            SwipeToDismissBoxValue.Settled -> Color.Transparent//colorScheme.background
            SwipeToDismissBoxValue.StartToEnd -> ColorOrange.copy(alpha = 0.5f)
            SwipeToDismissBoxValue.EndToStart -> ColorRedFleryRose.copy(alpha = 0.5f)
            else -> Color.Transparent
        }, label = "swipe_color"
    )
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(color),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (direction == SwipeToDismissBoxValue.StartToEnd) {
            Icon(
                imageVector = Icons.Default.EditNote,
                tint = MaterialTheme.colorScheme.secondary,
                contentDescription = stringResource(R.string.duplicate_row),
                modifier = Modifier
                    .weight(1f)
                    .size(35.dp)
            )
        }

        Spacer(Modifier.weight(5f))

        if (direction == SwipeToDismissBoxValue.EndToStart) {
            Icon(
                Icons.Default.Delete,
                tint = MaterialTheme.colorScheme.secondary,
                contentDescription = stringResource(id = R.string.delete),
                modifier = Modifier
                    .weight(1f)
                    .size(28.dp)
            )
        }
    }
}

@Composable
private fun OrderDetailsItemContent(
    orderDetailsItem: com.mastrosql.app.ui.navigation.main.ordersscreen.ordersdetailsscreen.model.OrderDetailsItem,
    modifier: Modifier,
    showEditDialog: MutableState<Boolean>,
    modifiedItemId: MutableIntState?
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
                        articleId = orderDetailsItem.articleId ?: 0,
                        sku = orderDetailsItem.sku,
                        description = orderDetailsItem.description,
                        various = orderDetailsItem.various
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
                        batch = orderDetailsItem.batch,
                        expirationDate = DateHelper.formatDateToDisplay(orderDetailsItem.expirationDate),
                        quantity = orderDetailsItem.quantity ?: 0.0,
                        orderedQuantity = orderDetailsItem.orderedQuantity ?: 0.0,
                        shippedQuantity = orderDetailsItem.shippedQuantity ?: 0.0
                    )
                    if (expanded) {
                        OrderDetailInfo(
                            completeDescription = orderDetailsItem.completeDescription
                        )
                    }
                }

                Column(
                    modifier = Modifier.widthIn(60.dp),
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {

                    ItemEditButton(
                        modifiedItemId = modifiedItemId?.intValue,
                        orderDetailsItemId = orderDetailsItem.id,
                        onClick = {
                            showEditDialog.value = true
                            modifiedItemId?.intValue = orderDetailsItem.id
                        },
                        modifier = modifier
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
    modifier: Modifier = Modifier,
    modifiedItemId: Int?,
    orderDetailsItemId: Int,
    onClick: () -> Unit,
) {
    val defaultTint = MaterialTheme.colorScheme.secondary
    //val itemEditButtonTint = remember { mutableStateOf(defaultTint) }

    val itemEditButtonTint = remember { Animatable(defaultTint) }

    LaunchedEffect(modifiedItemId, orderDetailsItemId) {
        // Update the tint color based on the comparison between modifiedItemId and itemId
        //val tint = if (modifiedItemId == orderDetailsItemId) Color.Red else defaultTint

        // Update the state of the tint color
        //itemEditButtonTint.value = tint

        itemEditButtonTint.animateTo(if (modifiedItemId == orderDetailsItemId) Color.Red else defaultTint)
    }

    IconButton(onClick = {
        onClick()
    }) {
        Icon(
            Icons.Default.Edit,
            tint = itemEditButtonTint.value,
            contentDescription = stringResource(R.string.edit_button_order_item),
            modifier = Modifier.fillMaxSize()
        )
    }
}


/**
 * Composable that displays the article id, sku and description of the order detail.
 */
@Composable
private fun OrderDetailDescriptionAndId(
    modifier: Modifier = Modifier,
    articleId: Int,
    sku: String?,
    description: String?,
    various: String?
) {
    // Determine the color based on the value of `various`
    val descriptionColor = if (various == "NEW") Color.Red else Color.Black

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
                text = description?.take(50) ?: "",
                style = MaterialTheme.typography.bodyLarge,
                color = descriptionColor
            )
        }
    }
}

/**
 * Composable that displays the batch, expiration date, quantity, ordered quantity and shipped
 */
@Composable
private fun OrderDetailDescriptionAndId2(
    modifier: Modifier = Modifier,
    batch: String?,
    expirationDate: String?,
    quantity: Double,
    orderedQuantity: Double,
    shippedQuantity: Double,

    ) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start
        ) {
            Column(
                Modifier.weight(0.45f)
            ) {
                Row {
                    Text(
                        text = stringResource(R.string.order_detail_batch),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (batch != null) {
                        Text(
                            text = batch,
                            color = ColorLightBlue,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(0.1f))

            Column(
                Modifier.weight(0.45f)
            ) {
                Column {
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
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        QuantityTable(quantity, orderedQuantity, shippedQuantity)
    }
}

/**
 * Composable that displays the complete description of the order detail.
 */
@Composable
private fun OrderDetailInfo(
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


/**
 * Composable that displays a table with the quantity, ordered quantity and shipped quantity.
 */
@Composable
private fun QuantityTable(
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
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.order_detail_orderedQuantity),
                    isBold = false
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

                QuantityText(
                    text = quantity.toString(),
                    true,
                    Modifier.weight(1f),
                    color = if (quantity > orderedQuantity) Color.Red else Color.Black
                )

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

/**
 * Composable that displays a text with a specific style.
 */
@Composable
fun QuantityText(
    text: String,
    isBold: Boolean,
    modifier: Modifier = Modifier,
    color: Color = Color.Black
) {
    Text(
        modifier = modifier,
        text = text,
        color = color,
        fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center
    )
}

//private fun QuantityText(
//    text: String, bold: Boolean = false, modifier: Modifier
//) {
//    Text(
//        modifier = modifier,
//        text = text,
//        fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
//        style = MaterialTheme.typography.bodyLarge,
//        textAlign = TextAlign.Center
//    )
//}

/**
 * Preview for [OrderDetailInfo]
 */
@Preview(showBackground = true)
@Composable
fun OrderDetailInfoPreview() {
    MastroAndroidPreviewTheme {
        OrderDetailInfo(
            completeDescription = "completeDescription", modifier = Modifier
        )
    }
}

/**
 * Preview for [QuantityTable]
 */
@Preview(showBackground = true)
@Composable
fun QuantityTablePreview() {
    MastroAndroidPreviewTheme {
        QuantityTable(1.0, 1.0, 1.0)
    }
}

/**
 * Preview for [QuantityText]
 */
@Preview(showBackground = true)
@Composable
fun QuantityTextPreview() {
    MastroAndroidPreviewTheme {
        QuantityText("1.0", true, Modifier)
    }
}

/**
 * Preview for [OrderDetailDescriptionAndId]
 */
@Preview(showBackground = true)
@Composable
fun OrderDetailDescriptionAndIdPreview() {
    MastroAndroidPreviewTheme {
        OrderDetailDescriptionAndId(Modifier, 123, "sku", "description", "various")
    }
}

/**
 * Preview for [OrderDetailDescriptionAndId2]
 */
@Preview(showBackground = true)
@Composable
fun OrderDetailDescriptionAndId2Preview() {
    MastroAndroidPreviewTheme {
        OrderDetailDescriptionAndId2(
            batch = "batch",
            expirationDate = "2023-01-01",
            quantity = 1.0,
            orderedQuantity = 1.0,
            shippedQuantity = 1.0
        )
    }
}

/**
 * Preview for [OrderDetailExpandButton]
 */
@Preview(showBackground = true)
@Composable
fun OrderDetailExpandButtonPreview() {
    MastroAndroidPreviewTheme {
        OrderDetailExpandButton(false, {})
    }
}

/**
 * Preview for [OrderDetailsItemContent]
 */
@ExperimentalMaterial3Api
@Preview(showBackground = true)
@Composable
fun SwipeToDismissBackgroundPreview() {
    MastroAndroidPreviewTheme {
        SwipeToDismissBackground(
            dismissState = rememberSwipeToDismissBoxState()
        )
    }
}

