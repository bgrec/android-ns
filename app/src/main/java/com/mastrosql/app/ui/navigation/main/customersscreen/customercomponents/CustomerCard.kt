package com.mastrosql.app.ui.navigation.main.customersscreen.customercomponents

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mastrosql.app.R
import com.mastrosql.app.ui.components.ShowToast
import com.mastrosql.app.ui.navigation.main.customersscreen.model.CustomerMasterData
import com.mastrosql.app.ui.navigation.main.customersscreen.model.Metadata
import com.mastrosql.app.ui.theme.MastroAndroidPreviewTheme
import com.mastrosql.app.ui.theme.MastroAndroidTheme

@Composable
fun CustomerCard(
    customerMasterData: CustomerMasterData,
    onCustomerSelected: ((CustomerMasterData) -> Unit)? = null,
    modifier: Modifier,
    navController: NavController? = null
) {
    var showToast by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    if (showToast) {
        ShowToast(context, stringResource(R.string.customer_error_toast))
        // Reset the showToast value after showing the toast
        showToast = false
    }

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
                        pIva = customerMasterData.vat ?: "",
                        businessName = customerMasterData.businessName
                    )
                    if (expanded) {
                        CustomerAddress(
                            businessName2 = customerMasterData.businessName2,
                            taxId = customerMasterData.taxId,
                            street = customerMasterData.street,
                            postalCode = customerMasterData.postalCode,
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
                    //When the customer is selected, the button will navigate to the customer destinations
                    if (onCustomerSelected != null) {
                        SelectCustomerButton(
                            onClick = {
                                onCustomerSelected(customerMasterData)
                            },
                        )
                    } else {
                        EditCustomerButton(
                            onClick = {
                                showToast = true
                                //TODO implement navigation
                                //navController?.navigate("customer/${customerMasterData.id}")
                            },
                        )
                    }
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
private fun EditCustomerButton(
    onClick: () -> Unit, modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick
    ) {
        Icon(
            Icons.Default.Edit,
            tint = MaterialTheme.colorScheme.secondary,
            contentDescription = stringResource(R.string.insert_article),
            modifier = Modifier.size(35.dp)
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

/**
 * Composable that displays a customer business name and address.
 *
 * @param businessName is the resource ID for the string of the customer business name
 * @param id is the Int that represents the customer id
 * @param modifier modifiers to set to this composable
 */

@Composable
fun CustomerNameAndId(
    id: Int,
    pIva: String,
    businessName: String?,
    modifier: Modifier = Modifier
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = stringResource(R.string.customer_id),
                style = MaterialTheme.typography.bodySmall,
            )

            Text(
                text = id.toString(),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodySmall,
            )

            if (pIva != "") {
                Spacer(modifier = Modifier.width(35.dp))

                Text(
                    text = stringResource(R.string.customer_vat),
                    style = MaterialTheme.typography.bodySmall,
                )

                Text(
                    text = pIva,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }

        Text(
            text = businessName?.take(50) ?: "",
            style = MaterialTheme.typography.titleMedium,
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
    businessName2: String?,
    taxId: String?,
    street: String?,
    postalCode: String?,
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

        if (taxId != "") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.customer_taxId),
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = taxId ?: "",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        if (street != "") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.customer_street),
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = street ?: "",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        if (postalCode != "") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.customer_postalCode),
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = postalCode ?: "",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        if (city != "") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.customer_city),
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = city ?: "",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        if (province != "") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.customer_province),
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = province ?: "",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        if (nation != "") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.customer_nation),
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = nation ?: "",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        if (businessName2 != "") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.customer_businessName2),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = businessName2 ?: "",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CustomerCardPreview() {
    MastroAndroidPreviewTheme {
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
                "taxId",
                emptyList(),
                Metadata("etag"),
                0,
                0L
            ), modifier = Modifier, navController = NavController(LocalContext.current)
        )
    }

}

@Preview(showBackground = true)
@Composable
fun CustomerAddressPreview() {
    MastroAndroidPreviewTheme {
        CustomerAddress(
            businessName2 = "businessName2",
            taxId = "taxId",
            street = "street",
            postalCode = "postalCode",
            city = "city",
            province = "province",
            nation = "nation",
            modifier = Modifier.padding(4.dp)
        )
    }
}