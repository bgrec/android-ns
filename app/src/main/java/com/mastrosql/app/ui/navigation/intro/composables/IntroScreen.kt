package com.mastrosql.app.ui.navigation.intro.composables

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.mastrosql.app.R
import com.mastrosql.app.ui.navigation.main.MainNavOption
import com.mastrosql.app.ui.navigation.main.NavRoutes
import kotlinx.coroutines.launch

//Used for intro screens, to show a text and a button, with a back button on top
//and a next button on bottom (or a different text)
//All the screens are the same, except for the text and the button text

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun IntroScreen(navController: NavController) {

    val focusManager = LocalFocusManager.current

    val pageCount = 7
    val pagerState = rememberPagerState(pageCount = { pageCount })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.intro_title)
                    )
                })
        },
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }) {
        Box(modifier = Modifier.fillMaxSize()) {
            HorizontalPager(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .padding(it),
                state = pagerState,
                beyondBoundsPageCount = 2
            ) { page ->
                when (page) {
                    0 -> WelcomeContent()//schermata benvenuto
                    1 -> ConfigContent()//configurazione impostazioni
                    2 -> LoginContent()//introduzione login
                    3 -> HomeContent()//introduzione home
                    4 -> ArchivesContent()//introduzione Archivi(clienti e articoli)
                    5 -> OrdersContent()//introduzione documenti(ordini)
                    6 -> DrawerContent()//introduzione menù a tendina
                }
            }

            Row(
                Modifier
                    .height(50.dp)
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                val lastPage = pagerState.pageCount - 1

                if (pagerState.currentPage != lastPage) {
                    TextButton(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(lastPage)
                            }
                        },
                        modifier = Modifier.padding(start = 16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.skip),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(Modifier.weight(1f))
                } else {
                    Spacer(Modifier.weight(2.8f))
                }

                val color1 = MaterialTheme.colorScheme.primary
                val color2 = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)

                repeat(pageCount) { index ->
                    PagerIndicator(
                        index = index,
                        currentPage = pagerState.currentPage,
                        onClick = { clickedIndex ->
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(clickedIndex)
                            }
                        },
                        activeColor = color1,
                        deactiveColor = color2
                    )
                }

                Spacer(Modifier.weight(1f))

                if (pagerState.currentPage != lastPage) {
                    IconButton(
                        onClick = {
                            val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(nextPage)
                            }
                        },
                        modifier = Modifier.padding(start = 16.dp)
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.NavigateNext, null)
                    }
                } else {
                    TextButton(
                        onClick = {
                            navController.navigate(MainNavOption.LoginScreen.name) {
                                popUpTo(NavRoutes.MainRoute.name)
                            }
                        },
                        modifier = Modifier.padding(start = 16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.start_app),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PagerIndicator(
    index: Int,
    currentPage: Int,
    onClick: (Int) -> Unit,
    activeColor: Color,
    deactiveColor: Color
) {
    Box(
        modifier = Modifier
            .padding(2.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (index == currentPage) activeColor else deactiveColor)
            .size(16.dp)
            .clickable { onClick(index) }
    )
}

//inizio contenuti dei pager (aggiungere Spacer alla fine di ogni content per evitare che il contenuto si sovrapponga al PageIndicator)

@Composable
fun WelcomeContent() {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            stringResource(R.string.welcome_content_text),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        //mettere immagine/icona


        //per evitare che il contenuto si sovrappone al PageIndicator
        Spacer(Modifier.height(16.dp))

    }
}


@Composable
fun ConfigContent() {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            stringResource(R.string.config_content_title),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            stringResource(R.string.config_content_text),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        //TextField per inserire url (da modificare)

        val focusRequester = remember { FocusRequester() }
        val focusManager = LocalFocusManager.current

        var baseUrl by remember { mutableStateOf("") }

        OutlinedTextField(
            value = baseUrl,
            singleLine = true,
            onValueChange = { baseUrl = it },
            label = { Text(stringResource(R.string.label_url)) },
            modifier = Modifier
                .focusRequester(focusRequester)
        )
        BackHandler(true) { focusManager.clearFocus() }

        //fine TextField per inserire url

        //pulsante gestione sezioni(da modificare)

        var showDialog by remember { mutableStateOf(false) }

        val hashMapButtons = remember {
            mutableStateOf(
                hashMapOf<MainNavOption, Boolean>().apply {
                    MainNavOption.entries.sortedBy { it.ordinal }.forEach {
                        this[it] = it == MainNavOption.Logout
                    }
                }
            )
        }


        val stringResMap = remember {
            mapOf(
                MainNavOption.CustomersScreen to R.string.drawer_customers,
                MainNavOption.CustomersPagedScreen to R.string.drawer_customers2,
                MainNavOption.ArticlesScreen to R.string.drawer_articles,
                MainNavOption.ItemsScreen to R.string.drawer_inventory,
                MainNavOption.OrdersScreen to R.string.drawer_orders
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.padding(top = 10.dp)
        ) {
            Button(onClick = { showDialog = true }) {
                Text(stringResource(R.string.dialog_button))
            }
        }


        if (showDialog) {
            AlertDialog(
                modifier = Modifier
                    .size(400.dp)
                    .padding(8.dp),
                onDismissRequest = { showDialog = false },
                title = { Text(stringResource(R.string.dialog_title)) },
                text = {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {

                        items(MainNavOption.entries) { option ->
                            if (
                                option != MainNavOption.LoginScreen &&
                                option != MainNavOption.NewHomeScreen &&
                                option != MainNavOption.HomeScreen &&
                                option != MainNavOption.SettingsScreen &&
                                option != MainNavOption.CartScreen &&
                                option != MainNavOption.Logout
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                ) {
                                    Text(
                                        text = stringResource(
                                            stringResMap[option] ?: R.string.label_url
                                        )
                                    )
                                    Spacer(Modifier.weight(1f))
                                    Switch(
                                        checked = hashMapButtons.value[option] ?: false,
                                        onCheckedChange = {
                                            hashMapButtons.value[option] = it
                                        }
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            // gestire salvataggio
                            showDialog = false
                        }
                    ) {
                        Text(stringResource(R.string.confirm_button))
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            // gestire annulla salvataggio
                            showDialog = false
                        }
                    ) {
                        Text(stringResource(R.string.cancel_button))
                    }
                }
            )
        }

        //fine pulsante

        //per evitare che il contenuto si sovrappone al PageIndicator
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun LoginContent() {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            stringResource(R.string.login_content_title),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            stringResource(R.string.login_content_text),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        //mettere immagine login


        //per evitare che il contenuto si sovrappone al PageIndicator
        Spacer(Modifier.height(16.dp))

    }
}

@Composable
fun HomeContent() {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            stringResource(R.string.home_content_title),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            stringResource(R.string.home_content_text),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        //mettere immagine per home


        //per evitare che il contenuto si sovrappone al PageIndicator
        Spacer(Modifier.height(16.dp))

    }
}

@Composable
fun ArchivesContent() {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            stringResource(R.string.archives_content_title),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            stringResource(R.string.archives_content_text),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Row() {

            //mettere immagini per le sezioni cliente e articoli
        }


        //per evitare che il contenuto si sovrappone al PageIndicator
        Spacer(Modifier.height(16.dp))

    }
}


@Composable
fun OrdersContent() {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            stringResource(R.string.orders_content_title),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            stringResource(R.string.orders_content_text),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

            //mettere immagine per ordini


        //per evitare che il contenuto si sovrappone al PageIndicator
        Spacer(Modifier.height(16.dp))

    }
}

@Composable
fun DrawerContent() {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            stringResource(R.string.drawer_content_title),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            stringResource(R.string.drawer_content_text),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        //mettere immagine per il menù a tendina


        //per evitare che il contenuto si sovrappone al PageIndicator
        Spacer(Modifier.height(16.dp))

    }
}

@Preview(apiLevel = 33)
@Composable
fun IntroScreenPreview() {
    IntroScreen(navController = rememberNavController())
}