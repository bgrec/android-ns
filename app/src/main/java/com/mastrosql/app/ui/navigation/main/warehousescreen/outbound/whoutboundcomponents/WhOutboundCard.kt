package com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.whoutboundcomponents

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
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mastrosql.app.R
import com.mastrosql.app.ui.navigation.main.warehousescreen.outbound.model.WarehouseOutbound
import com.mastrosql.app.utils.DateHelper

@Composable
fun WhOutboundCard(
    whOutbound: WarehouseOutbound,
    modifier: Modifier,
    navigateToWhOutboundDetails: (Int, String?) -> Unit,
    modifiedWhOutboundId: MutableIntState?,
    showEditWhOutboundDataDialog: MutableState<Boolean>
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
                    WhOutboundExpandButton(
                        expanded = expanded,
                        onClick = { expanded = !expanded },
                    )
                }
                //Spacer(Modifier.weight(0.5f))
                Column(
                    modifier = Modifier.weight(1f), horizontalAlignment = Alignment.Start
                ) {
                    WhOutboundDescriptionAndId(operationId = whOutbound.id,
                        description = "",
                        insertDate = DateHelper.formatDateToDisplay(whOutbound.lastUpdated.toString()),
                        //TODO add the correct insert date
                        businessName = whOutbound.businessName,
                        onRowClick = {
                            modifiedWhOutboundId?.intValue = whOutbound.id
                        })
                    if (expanded) {
                        WhOutboundInfo(notes = "",//whOutbound.businessName2,
                            // TODO: Add notes
                            onWhOutboundInfoClick = {
                                modifiedWhOutboundId?.intValue = whOutbound.id
                                showEditWhOutboundDataDialog.value = true
                            })
                    }
                }
                //Spacer(Modifier.weight(1f))
                Column(
                    modifier = Modifier.widthIn(60.dp),
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {

                    WhOutDetailsEditButton(
                        operationId = whOutbound.id,
                        whOutboundDescription = whOutbound.businessName,
                        onEditClick = { operationId, operationDescription ->
                            //Setting the modifiedWhOutboundId to the id of the warehouse outbound
                            //operation that was clicked
                            //this will change the color of the edit button
                            modifiedWhOutboundId?.intValue = operationId
                            //Navigate to the warehouse outbound details screen
                            navigateToWhOutboundDetails(operationId, operationDescription)
                        },
                        modifiedWhOutboundId = modifiedWhOutboundId?.intValue
                    )
                }
            }
        }
    }
}

@Composable
private fun WhOutboundExpandButton(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    onClick: () -> Unit,
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
private fun WhOutDetailsEditButton(
    modifier: Modifier = Modifier,
    operationId: Int,
    modifiedWhOutboundId: Int?,
    whOutboundDescription: String?,
    onEditClick: (Int, String?) -> Unit
) {
    val defaultTint = MaterialTheme.colorScheme.secondary
    val whOutboundEditButtonTint = remember { mutableStateOf(defaultTint) }

    LaunchedEffect(modifiedWhOutboundId, operationId) {
        // Animate the color change when modifiedWhOutboundId equals operationId
        if (modifiedWhOutboundId == operationId) {
            whOutboundEditButtonTint.value = Color.Red
        } else {
            whOutboundEditButtonTint.value = defaultTint
        }
    }

    IconButton(onClick = {
        // Change the button color to red when clicked
        whOutboundEditButtonTint.value = Color.Red
        onEditClick(operationId, whOutboundDescription)

    }) {
        Icon(
            Icons.Default.Edit,
            //tint = MaterialTheme.colorScheme.secondary,
            tint = whOutboundEditButtonTint.value,
            contentDescription = stringResource(R.string.insert_article),
            modifier = modifier.fillMaxSize()
        )
    }
}

@Composable
fun WhOutboundDescriptionAndId(
    modifier: Modifier = Modifier,
    operationId: Int,
    insertDate: String?,
    businessName: String?,
    description: String?,
    onRowClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = stringResource(R.string.warehouse_operation_id),
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                text = operationId.toString(),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodySmall,
            )

            Spacer(modifier = Modifier.weight(0.5f))

            Text(
                text = stringResource(R.string.document_insertDate),
                style = MaterialTheme.typography.bodySmall,
            )

            Text(
                text = insertDate ?: "",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodySmall,
            )
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

@Composable
fun WhOutboundInfo(
    modifier: Modifier = Modifier,
    notes: String? = null,
    onWhOutboundInfoClick: () -> Unit = {}

) {
    val focusRequester = remember { FocusRequester() }
    Column(
        modifier = modifier
            .padding(top = 4.dp)
            .clickable(onClick = {
                onWhOutboundInfoClick()
                focusRequester.requestFocus()
            })
            .focusRequester(focusRequester)
    ) {
        if (notes != null) {
            Row(
                modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.document_notes),
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
        } else {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}


