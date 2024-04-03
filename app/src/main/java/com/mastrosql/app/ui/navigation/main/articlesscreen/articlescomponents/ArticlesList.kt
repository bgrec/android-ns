package com.mastrosql.app.ui.navigation.main.articlesscreen.articlescomponents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mastrosql.app.ui.navigation.main.articlesscreen.model.Article
import com.mastrosql.app.ui.navigation.main.articlesscreen.model.Metadata
import com.mastrosql.app.ui.theme.MastroAndroidTheme

@Composable
fun ArticlesList(
    articlesList: List<Article>,
    documentId: Int?,
    documentType: String?,
    state: MutableState<TextFieldValue>,
    modifier: Modifier = Modifier,
    navController: NavController,
    onInsertArticleClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
        //.focusable()
    )
    {
        val filteredList: List<Article>
        val searchedText = state.value.text

        filteredList = if (searchedText.isEmpty()) {
            articlesList
        } else {
            //update this for fields to search
            articlesList.filter {
                it.description.contains(searchedText, ignoreCase = true)
                        ||
                        it.sku.contains(searchedText, ignoreCase = true)
                        || it.vendorSku.contains(searchedText, ignoreCase = true)
            }
        }

        items(filteredList) { article ->
            ArticleCard(
                article = article,
                documentId = documentId,
                documentType = documentType,
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(),
                //.focusable(),
                navController = navController,
                onInsertArticleClick = onInsertArticleClick
            )
        }
    }
}

@Preview
@Composable
fun ItemsListPreview() {
    MastroAndroidTheme {
        ArticlesList(
            articlesList = listOf(
                Article(
                    id = 1,
                    sku = "sku",
                    vendorSku = "vendorSku",
                    description = "description",
                    cost = 1.0,
                    vat = "vat",
                    measureUnit = "measureUnit",
                    department = "department",
                    subDepartment = "subDepartment",
                    family = "family",
                    subFamily = "subFamily",
                    price = 1.0,
                    group = "group",
                    ean8 = "ean8",
                    ean13 = "ean13",
                    eanAlt = "eanAlt",
                    links = emptyList(),
                    metadata = Metadata("etag"),
                    page = 1,
                    lastUpdated = 1L
                ),
                Article(
                    id = 2,
                    sku = "sku",
                    vendorSku = "vendorSku",
                    description = "description",
                    cost = 1.0,
                    vat = "vat",
                    measureUnit = "measureUnit",
                    department = "department",
                    subDepartment = "subDepartment",
                    family = "family",
                    subFamily = "subFamily",
                    price = 1.0,
                    group = "group",
                    ean8 = "ean8",
                    ean13 = "ean13",
                    eanAlt = "eanAlt",
                    links = emptyList(),
                    metadata = Metadata("etag"),
                    page = 1,
                    lastUpdated = 1L
                )
            ),
            documentId = null,
            documentType = null,
            state = remember { mutableStateOf(TextFieldValue("")) },
            modifier = Modifier.padding(8.dp),
            navController = NavController(LocalContext.current),
            onInsertArticleClick = { }
        )
    }
}