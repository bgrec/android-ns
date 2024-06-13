package com.mastrosql.app.ui.navigation.intro.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mastrosql.app.R
import com.mastrosql.app.ui.AppViewModelProvider
import com.mastrosql.app.ui.navigation.intro.IntroNavOption
import com.mastrosql.app.ui.navigation.main.MainNavOption
import com.mastrosql.app.ui.navigation.main.NavRoutes
import com.mastrosql.app.ui.navigation.main.settingsscreen.UserPreferencesViewModel
import com.mastrosql.app.ui.theme.MastroAndroidTheme
import kotlinx.coroutines.launch
import java.util.EnumMap

@ExperimentalMaterial3Api
@ExperimentalFoundationApi
@Composable
fun IntroScreen(
    navController: NavController,
    viewModel: UserPreferencesViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val focusManager = LocalFocusManager.current
    val pageCount = 7
    val pagerState = rememberPagerState(pageCount = { pageCount })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = {
            Text(
                text = stringResource(R.string.intro_title)
            )
        })
    }, modifier = Modifier.pointerInput(Unit) {
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
                    1 -> ConfigContent(viewModel, focusManager)//configurazione impostazioni
                    2 -> LoginContent()//introduzione login
                    3 -> HomeContent()//introduzione home
                    4 -> ArchivesContent()//introduzione Archivi(clienti e articoli)
                    5 -> OrdersContent()//introduzione documenti(ordini)
                    6 -> DrawerContent(navController)//introduzione menù a tendina
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
                        }, modifier = Modifier.padding(start = 16.dp)
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
                        }, modifier = Modifier.padding(start = 16.dp)
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.NavigateNext, null)
                    }
                } else {
                    TextButton(
                        onClick = {
                            navController.navigate(MainNavOption.LoginScreen.name) {
                                popUpTo(NavRoutes.MainRoute.name)
                            }
                            viewModel.onBoardingCompleted(true)
                        }, modifier = Modifier.padding(start = 16.dp)
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
    index: Int, currentPage: Int, onClick: (Int) -> Unit, activeColor: Color, deactiveColor: Color
) {
    Box(modifier = Modifier
        .padding(2.dp)
        .clip(RoundedCornerShape(10.dp))
        .background(if (index == currentPage) activeColor else deactiveColor)
        .size(16.dp)
        .clickable { onClick(index) })
}

//inizio contenuti dei pager (aggiungere Spacer alla fine di ogni content per evitare che il contenuto si sovrapponga al PageIndicator)

@Composable
fun WelcomeContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Spacer(Modifier.weight(0.5f))

        Text(
            stringResource(R.string.welcome_content_title),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Box(
            modifier = Modifier.offset(y = 30.dp)
        ) {
            WelcomeLogo()
        }

        Spacer(Modifier.weight(0.2f))

        Text(
            stringResource(R.string.welcome_content_text),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )


        //per evitare che il contenuto si sovrappone al PageIndicator
        Spacer(Modifier.weight(1f))

    }
}


@Composable
fun WelcomeLogo() {
    Image(
        painter = painterResource(R.drawable.mastroweb),
        contentDescription = "Logo Nipeservice",
        modifier = Modifier.size(150.dp)
    )
}

@Composable
fun ConfigContent(
    viewModel: UserPreferencesViewModel, focusManager: FocusManager
) {
    Column(
        modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
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

        Spacer(Modifier.height(32.dp))

        Text(
            stringResource(R.string.config_url_text),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(10.dp))

        // Collect the state from the view model and update the UI
        val userPreferencesUiState by viewModel.uiState.collectAsStateWithLifecycle()

        val focusRequester = remember { FocusRequester() }

        var urlState by remember { mutableStateOf(userPreferencesUiState.baseUrl) }

        // Update the local state when the base URL changes
        LaunchedEffect(userPreferencesUiState.baseUrl) {
            urlState = userPreferencesUiState.baseUrl
        }

        OutlinedTextField(value = urlState,
            singleLine = false,
            onValueChange = { newValue -> urlState = newValue },
            leadingIcon = { Icon(painterResource(R.drawable.bring_your_own_ip), null) },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text, imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                viewModel.setBaseUrl(urlState)
                focusManager.clearFocus()
            }),
            label = { Text(stringResource(R.string.label_url)) },
            modifier = Modifier
                .focusRequester(focusRequester)
                .onFocusChanged {
                    if (!it.isFocused) {
                        viewModel.setBaseUrl(urlState)
                    }
                })

        //fine TextField per inserire url

        Spacer(Modifier.height(32.dp))

        //pulsante gestione sezioni


        Text(
            stringResource(R.string.config_section_text),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        var showDialog by remember { mutableStateOf(false) }

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
                Text(stringResource(R.string.dialog_button_settings))
            }
        }


        if (showDialog) {
            AlertDialog(modifier = Modifier.wrapContentSize(),
                onDismissRequest = { showDialog = false },
                title = { Text(stringResource(R.string.dialog_title)) },
                text = {
                    LazyColumn(modifier = Modifier.wrapContentSize()) {

                        items(MainNavOption.entries.toList()) {
                            if ((stringResMap[it] != null)) {
                                Row(
                                    modifier = Modifier.clickable(onClick = {
                                        val isChecked =
                                            !(userPreferencesUiState.activeButtons[it] ?: false)
                                        val updatedState =
                                            EnumMap(userPreferencesUiState.activeButtons)
                                        updatedState[it] = isChecked
                                        viewModel.updateActiveButtons(updatedState)
                                    }),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Text(stringResource(stringResMap[it] ?: R.string.null_text))

                                    Spacer(Modifier.weight(1f))

                                    Switch(checked = userPreferencesUiState.activeButtons[it]
                                        ?: false,
                                        onCheckedChange = { isChecked ->
                                            val updatedState =
                                                EnumMap(userPreferencesUiState.activeButtons)
                                            updatedState[it] = isChecked
                                            viewModel.updateActiveButtons(updatedState)
                                        })
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        showDialog = false
                    }) {
                        Text(stringResource(R.string.close_button))
                    }
                })
        }

        //fine pulsante

        //per evitare che il contenuto si sovrappone al PageIndicator
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun LoginContent() {
    Column(
        modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
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

        Box(
            modifier = Modifier.offset(y = 30.dp)
        ) {
            WelcomeLogo()
        }

        Spacer(Modifier.weight(0.1f))

        Text(
            stringResource(R.string.login_content_text2),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        //per evitare che il contenuto si sovrappone al PageIndicator
        Spacer(Modifier.weight(1f))

    }
}

@Composable
fun HomeContent() {
    Column(
        modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
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

        Box(
            modifier = Modifier.offset(y = 30.dp)
        ) {
            WelcomeLogo()
        }


        //per evitare che il contenuto si sovrappone al PageIndicator
        Spacer(Modifier.weight(1f))

    }
}

@Composable
fun ArchivesContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
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

        Spacer(Modifier.weight(0.2f))

        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    stringResource(R.string.archives_client_title),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Box(
                    modifier = Modifier.offset(y = 20.dp)
                ) {
                    WelcomeLogo()//sostituire con immagine sezione clienti
                }
            }

            Spacer(Modifier.weight(0.5f))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    stringResource(R.string.archives_articles_title),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Box(
                    modifier = Modifier.offset(y = 20.dp)
                ) {
                    WelcomeLogo()//sostituire con immagine sezione articoli
                }
            }

        }

        Spacer(Modifier.weight(0.1f))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.archives_content_expand),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )

                IconButton({}, enabled = false) {
                    Icon(Icons.Filled.ExpandMore, null)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.archives_content_reduce), modifier = Modifier
                )

                IconButton({}, enabled = false) {
                    Icon(Icons.Filled.ExpandLess, null)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.archives_content_edit), modifier = Modifier
                )

                IconButton({}, enabled = false) {
                    Icon(Icons.Filled.Edit, null)
                }
            }
        }

        //per evitare che il contenuto si sovrappone al PageIndicator
        Spacer(Modifier.weight(1f))

    }
}


@Composable
fun OrdersContent() {
    Column(
        modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
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


        Box(
            modifier = Modifier.offset(y = 30.dp)
        ) {
            WelcomeLogo()
        }
        //per evitare che il contenuto si sovrappone al PageIndicator
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun DrawerContent(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
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

        Box(
            modifier = Modifier.offset(y = 30.dp)
        ) {
            WelcomeLogo()
        }

        Spacer(Modifier.weight(1f))

        Text(
            stringResource(R.string.drawer_content_text2),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.weight(4f))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.padding(top = 10.dp)
        ) {
            Button(onClick = {
                navController.navigate(IntroNavOption.NewsScreen.name) {
                    popUpTo(IntroNavOption.NewsScreen.name)
                }
            }) {
                Text(stringResource(R.string.news_button))
            }
        }

        //per evitare che il contenuto si sovrappone al PageIndicator
        Spacer(Modifier.weight(1f))

    }
}

@Preview(showBackground = true)
@Composable
fun IntroScreenPreview() {
    MastroAndroidTheme {
        ArchivesContent()
    }

}