package com.mastrosql.app.ui.navigation.main.itemsScreen.itemComponents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.mastrosql.app.data.itemTest.ItemTest
import com.mastrosql.app.ui.navigation.main.MainNavOption
import com.mastrosql.app.ui.navigation.main.cartscreen.CartViewModel

@Composable
fun ItemCard(
    itemTest: ItemTest,
    orderQuantity: Double,
    onQuantityChange: (Double) -> Unit,
    modifier: Modifier,
    navController: NavController,
    viewModel: CartViewModel
) {

    Card(modifier = modifier) {
        Column(modifier = Modifier) {
            Row(modifier = Modifier) {
                itemTest.ean?.let { Text(text = it, modifier = Modifier.weight(1f)) }
                Spacer(modifier = Modifier.weight(1f))
                itemTest.description?.let { Text(text = it) }
            }
            Row {

                TextField(
                    value = orderQuantity.toString(),
                    onValueChange = {
                        /*orderQuantity =
                        if (it.isEmpty()) 0f else it.toFloat()*/
                    },
                    label = { Text("Quantity") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            // focusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
                    modifier = Modifier.weight(1f)
                    //.align(alignment = androidx.compose.ui.Alignment.CenterVertically)
                )
                IconButton(onClick = {
                    onQuantityChange(orderQuantity + 1)

                }) {
                    Icon(
                        Icons.Default.Add,
                        tint = MaterialTheme.colorScheme.onBackground,
                        contentDescription = "Add"
                    )
                }

                IconButton(onClick = { /*TODO*/
                    if (orderQuantity > 0) {
                        onQuantityChange(orderQuantity - 1)
                    }
                }) {
                    Icon(
                        Icons.Default.Delete,
                        tint = MaterialTheme.colorScheme.onBackground,
                        contentDescription = "Remove"
                    )
                }

                IconButton(onClick = { /*TODO*/
                    val cartItemTest = ItemTest(
                        id = itemTest.id,
                        ean = itemTest.ean,
                        description = itemTest.description,
                        quantity = orderQuantity
                    )
                    viewModel.addToCart(cartItemTest) // Ad
                    //navController.navigate("cartScreen")
                    navController.navigate(MainNavOption.CartScreen.name)


                }) {
                    Icon(
                        Icons.Default.ShoppingCart,
                        tint = MaterialTheme.colorScheme.onBackground,
                        contentDescription = "Send"
                    )
                }

            }
        }
    }
}


@Preview
@Composable
fun ItemCardPreview() {
    var orderQuantity = 0.0
    ItemCard(
        itemTest = ItemTest(1, "ean", "description"), 1.00, { newQuantity ->
            orderQuantity = newQuantity
        }, modifier = Modifier, navController = NavController(LocalContext.current),
        viewModel = CartViewModel()
    )

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