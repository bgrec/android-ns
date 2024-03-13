package com.mastrosql.app.ui.navigation.main.articlesscreen.articlescomponents

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
import androidx.compose.material.icons.filled.AddShoppingCart
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
import com.mastrosql.app.ui.navigation.main.articlesscreen.model.Article
import com.mastrosql.app.ui.theme.MastroAndroidTheme

@Composable
fun ArticleCard(
    article: Article,
    documentId: Int?,
    documentType: String?,
    modifier: Modifier,
    navController: NavController,
    onInsertArticleClick: (Int) -> Unit
) {
    var showToast by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    if (showToast) {
        ShowToast(context, stringResource(R.string.article_error_toast))
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
                    ArticleExpandButton(
                        expanded = expanded,
                        onClick = { expanded = !expanded },
                    )
                }
                //Spacer(Modifier.weight(0.5f))
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.Start
                ) {
                    ArticleDescriptionAndId(
                        id = article.id,
                        sku = article.sku,
                        description = article.description
                    )
                    if (expanded) {
                        ArticleInfo(
                            department = article.department,
                            family = article.family,
                            vendorSku = article.vendorSku,
                            measureUnit = article.measureUnit,
                            price = article.price
                        )
                    }
                }
                //Spacer(Modifier.weight(1f))
                Column(
                    modifier = Modifier.widthIn(60.dp),
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {
                    if (documentId != null && documentType != null && documentId > 0) {
                        ArticleInsertIntoDocumentButton(
                            onClick = {
                                onInsertArticleClick(article.id)
                            }
                        )
                    } else {
                        ArticleEditButton(
                            onClick = {
                                showToast = true
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
private fun ArticleExpandButton(
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
private fun ArticleEditButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
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
private fun ArticleInsertIntoDocumentButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick
    ) {
        Icon(
            Icons.Default.AddShoppingCart,
            tint = MaterialTheme.colorScheme.secondary,
            contentDescription = stringResource(R.string.insert_article),
            modifier = Modifier.size(35.dp)
        )
    }
}

/**
 * Composable that displays a article business name and address.
 *
 * @param description is the resource ID for the string of the article description
 * @param id is the Int that represents the article id
 * @param modifier modifiers to set to this composable
 */

@Composable
fun ArticleDescriptionAndId(
    id: Int, sku: String?, description: String?, modifier: Modifier = Modifier
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = stringResource(R.string.article_id),
                style = MaterialTheme.typography.bodySmall,
            )

            Text(
                text = id.toString(),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodySmall,
            )

            Spacer(modifier = Modifier.width(40.dp))

            Text(
                text = stringResource(R.string.article_sku),
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = sku?.take(20) ?: "",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Text(
            text = description?.take(50) ?: "",
            style = MaterialTheme.typography.titleMedium,
            modifier = modifier.padding(top = 4.dp)
        )
    }
}

/**
 * Composable that displays a article info
 * @param department is the Int that represents the article department
 * @param family is the String that represents the article family
 */
@Composable
fun ArticleInfo(
    department: String,
    family: String,
    vendorSku: String,
    measureUnit: String,
    price: Double,
    modifier: Modifier = Modifier

) {
    Column(
        modifier = modifier
            .padding(top = 4.dp)
    ) {

        if (department != "") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.article_department),
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = department,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        if (family != "") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.article_family),
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = family,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        if (vendorSku != "") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.article_vendorSku),
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = vendorSku,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        if (measureUnit != "") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.article_measureUnit),
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = measureUnit,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        if (price.toString() != "") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.article_price),
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = price.toString(),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}


//@Preview
//@Composable
//fun ArticleCardPreview() {
//    MastroAndroidTheme {
//        ArticleCard(
//            article = Article(
//                id = 1,
//                sku = "sku",
//                vendorSku = "vendorSku",
//                description = "description",
//                cost = 1.0,
//                vat = "vat",
//                measureUnit = "measureUnit",
//                department = "department",
//                subDepartment = "subDepartment",
//                family = "family",
//                subFamily = "subFamily",
//                price = 1.0,
//                group = "group",
//                ean8 = "ean8",
//                ean13 = "ean13",
//                eanAlt = "eanAlt",
//                links = emptyList(),
//                metadata = Metadata("etag"),
//                page = 0,
//                lastUpdated = System.currentTimeMillis()
//            ), modifier = Modifier, navController = NavController(LocalContext.current)
//        )
//    }
//
//}

@Preview(apiLevel = 33)
@Composable
fun ArticleInfoPreview() {
    MastroAndroidTheme {
        ArticleInfo(
            department = "department",
            family = "sku",
            vendorSku = "vendorSku",
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