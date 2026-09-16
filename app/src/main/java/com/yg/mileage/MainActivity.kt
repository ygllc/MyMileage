/*
 * MyMileage – Your Smart Vehicle Mileage Tracker
 * Copyright (C) 2026 Yojit Ghadi
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

@file:OptIn(ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalMaterial3Api::class
)

package com.yg.mileage

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuOpen
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.GroupAdd
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Money
import androidx.compose.material3.Button
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShortNavigationBar
import androidx.compose.material3.ShortNavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.animateIcon
import androidx.compose.material3.WideNavigationRail
import androidx.compose.material3.WideNavigationRailItem
import androidx.compose.material3.WideNavigationRailState
import androidx.compose.material3.WideNavigationRailValue
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.material3.rememberWideNavigationRailState
import androidx.compose.material3.toShape
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.metadata
import androidx.navigation3.ui.NavDisplay
import coil3.compose.AsyncImage
import com.yg.mileage.auth.FirebaseAuthClient
import com.yg.mileage.auth.SignInScreen
import com.yg.mileage.auth.SignUpScreen
import com.yg.mileage.auth.UserData
import com.yg.mileage.data.Repository
import com.yg.mileage.navigation.NavigationState
import com.yg.mileage.navigation.Navigator
import com.yg.mileage.navigation.Screen
import com.yg.mileage.navigation.bottomNavItems
import com.yg.mileage.navigation.icon
import com.yg.mileage.navigation.rememberNavigationState
import com.yg.mileage.navigation.toEntries
import com.yg.mileage.ui.accountsScreen.screens.AccountScreen
import com.yg.mileage.ui.accountsScreen.screens.PersonalInfoScreen
import com.yg.mileage.ui.accountsScreen.screens.SecuritySettingsScreen
import com.yg.mileage.ui.theme.MyMileageTheme
import com.yg.mileage.ui.theme.robotoFlexTopAppBar
import com.yg.mileage.ui.tripsScreen.screens.MileageCalculatorScreen
import com.yg.mileage.ui.tripsScreen.screens.TripsScreen
import com.yg.mileage.ui.vehiclesScreen.screens.AddVehicleScreen
import com.yg.mileage.ui.vehiclesScreen.screens.CurrencySettingsScreen
import com.yg.mileage.ui.vehiclesScreen.screens.ProfileScreen
import com.yg.mileage.ui.vehiclesScreen.viewModel.Vehicle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private enum class AdaptiveNavigationType {
    BottomBar,
    NavigationRail,
    PermanentDrawer
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
class MainActivity : ComponentActivity() {
    private lateinit var carViewModel: CarViewModel
    private lateinit var firebaseAuthClient: FirebaseAuthClient
    private var phoneVerificationId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = Repository.getRepository(applicationContext)
        firebaseAuthClient = FirebaseAuthClient(applicationContext, repository = repository)

        val carViewModelFactory = CarViewModelFactory(repository)

        setContent {
            carViewModel = viewModel(factory = carViewModelFactory)
            val currentUser by carViewModel.currentUser.collectAsState()
            var showLoading by remember { mutableStateOf(true) }

            LaunchedEffect(Unit) {
                carViewModel.updateSignInState(firebaseAuthClient.getSignedInUser())
                showLoading = false
            }

            if (showLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    ContainedLoadingIndicator(
                        polygons = listOf(
                            MaterialShapes.SoftBurst,
                            MaterialShapes.Cookie9Sided,
                            MaterialShapes.Pentagon,
                            MaterialShapes.Pill,
                            MaterialShapes.Sunny,
                            MaterialShapes.Cookie4Sided,
                            MaterialShapes.Oval,
                            MaterialShapes.VerySunny,
                            MaterialShapes.Ghostish,
                            MaterialShapes.Cookie12Sided
                        )
                    )
                }
            } else {
                MyMileageTheme( {
                    val topLevelRoutes = remember {
                        setOf<NavKey>(Screen.SignIn, Screen.Vehicles, Screen.Trips, Screen.Activities)
                    }
                    val startRoute = if (currentUser == null) Screen.SignIn else Screen.Trips
                    val navigationState = rememberNavigationState(startRoute = startRoute, topLevelRoutes = topLevelRoutes)
                    val navigator = remember(navigationState) { Navigator(navigationState) }
                    val coroutineScope = rememberCoroutineScope()
                    val windowSizeClass = calculateWindowSizeClass(this@MainActivity)
                    var isNavCollapsed by rememberSaveable { mutableStateOf(false) }
                    val railState = rememberWideNavigationRailState()

                    // Compact: Bottom Bar
                    // Medium & Expanded: Navigation Rail (Targeting tablets and foldables)
                    // Large & ExtraLarge: Permanent Navigation Drawer (Targeting desktop)
                    val adaptiveNavigationType = when (windowSizeClass.widthSizeClass) {
                        WindowWidthSizeClass.Compact -> AdaptiveNavigationType.BottomBar
                        WindowWidthSizeClass.Medium, WindowWidthSizeClass.Expanded -> AdaptiveNavigationType.NavigationRail
                        else -> AdaptiveNavigationType.PermanentDrawer
                    }

                    val currentStack = navigationState.backStacks[navigationState.topLevelRoute]
                    val currentScreenTitle by remember(currentStack, navigationState.topLevelRoute) {
                        derivedStateOf { (currentStack?.lastOrNull() as? Screen)?.label ?: "MyMileage" }
                    }
                    val canNavigateBack by remember(currentStack, navigationState.topLevelRoute) {
                        derivedStateOf {
                            if (currentStack != null) {
                                currentStack.size > 1 || navigationState.topLevelRoute != startRoute
                            } else {
                                false
                            }
                        }
                    }
                    LaunchedEffect(Unit) {
                        carViewModel.signInCompleted.collectLatest {
                            navigator.navigate(Screen.Trips)
                        }
                    }

                    val topBar: @Composable () -> Unit = {
                        if (currentScreenTitle != Screen.SignIn.label && currentScreenTitle != Screen.SignUp.label) {
                            AppTopBar(
                                currentScreenTitle = currentScreenTitle,
                                canNavigateBack = canNavigateBack,
                                navigator = navigator,
                                currentUser = currentUser,
                                showNavToggle = currentUser != null && adaptiveNavigationType != AdaptiveNavigationType.BottomBar,
                                isNavCollapsed = isNavCollapsed,
                                railState = railState,
                                onToggleNav = { isNavCollapsed = !isNavCollapsed }
                            )
                        }
                    }

                    val appContent: @Composable (Modifier) -> Unit = { contentModifier ->
                        AppContent(
                            navigator = navigator,
                            navigationState = navigationState,
                            modifier = contentModifier,
                            windowWidthSizeClass = windowSizeClass.widthSizeClass,
                            carViewModel = carViewModel,
                            firebaseAuthClient = firebaseAuthClient,
                            coroutineScope = coroutineScope,
                            onGoogleSignInClick = {
                                lifecycleScope.launch {
                                    val signInResult = firebaseAuthClient.signInWithGoogle(this@MainActivity)
                                    if (signInResult.errorMessage != null) {
                                        Toast.makeText(this@MainActivity, signInResult.errorMessage, Toast.LENGTH_LONG).show()
                                    }
                                    carViewModel.onSignInResult(signInResult)
                                }
                            },
                            onMicrosoftSignInClick = {
                                lifecycleScope.launch {
                                    val signInResult = firebaseAuthClient.signInWithMicrosoft(this@MainActivity)
                                    if (signInResult.errorMessage != null) {
                                        Toast.makeText(this@MainActivity, signInResult.errorMessage, Toast.LENGTH_LONG).show()
                                    }
                                    carViewModel.onSignInResult(signInResult)
                                }
                            },
                            onEmailSignInClick = { email, password ->
                                lifecycleScope.launch {
                                    val result = firebaseAuthClient.signInWithEmailPassword(email, password)
                                    carViewModel.onSignInResult(result)
                                    if (result.errorMessage != null) {
                                        Toast.makeText(this@MainActivity, result.errorMessage, Toast.LENGTH_LONG).show()
                                    }
                                }
                            },
                            onEmailSignUpClick = { email, password ->
                                lifecycleScope.launch {
                                    val result = firebaseAuthClient.createUserWithEmailPassword(email, password)
                                    carViewModel.onSignInResult(result)
                                    if (result.errorMessage != null) {
                                        Toast.makeText(this@MainActivity, result.errorMessage, Toast.LENGTH_LONG).show()
                                    } else {
                                        navigator.navigate(Screen.Trips)
                                    }
                                }
                            },
                            onSendOtpClick = { phoneNumber ->
                                firebaseAuthClient.verifyPhoneNumber(
                                    activity = this@MainActivity,
                                    phoneNumber = phoneNumber,
                                    onCodeSent = { verificationId ->
                                        this@MainActivity.phoneVerificationId = verificationId
                                        Toast.makeText(this@MainActivity, "Code sent!", Toast.LENGTH_SHORT).show()
                                    },
                                    onVerificationFailed = { e ->
                                        Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG).show()
                                    }
                                )
                            },
                            onVerifyOtpClick = { otp ->
                                this@MainActivity.phoneVerificationId?.let { verificationId ->
                                    lifecycleScope.launch {
                                        val result = firebaseAuthClient.signInWithPhoneCredential(verificationId, otp)
                                        carViewModel.onSignInResult(result)
                                        if (result.errorMessage != null) {
                                            Toast.makeText(this@MainActivity, result.errorMessage, Toast.LENGTH_LONG).show()
                                        }
                                    }
                                } ?: Toast.makeText(this@MainActivity, "Please send code first.", Toast.LENGTH_LONG).show()
                            }
                        )
                    }

                    if (currentUser != null) {
                        when (adaptiveNavigationType) {
                            AdaptiveNavigationType.BottomBar -> {
                                Scaffold(
                                    topBar = topBar,
                                    bottomBar = { AppBottomNavigationBar(navigationState, navigator) }
                                ) { innerPadding ->
                                    appContent(Modifier.padding(innerPadding))
                                }
                            }

                            AdaptiveNavigationType.NavigationRail -> {
                                if (isNavCollapsed) {
                                    Scaffold(topBar = topBar) { innerPadding ->
                                        appContent(Modifier.padding(innerPadding))
                                    }
                                } else {
                                    Row(modifier = Modifier.fillMaxSize()) {
                                        AppNavigationRail(navigationState = navigationState, navigator = navigator, railState = railState)
                                        Scaffold(topBar = topBar) { innerPadding ->
                                            appContent(Modifier.padding(innerPadding))
                                        }
                                    }
                                }
                            }

                            AdaptiveNavigationType.PermanentDrawer -> {
                                if (isNavCollapsed) {
                                    Scaffold(topBar = topBar) { innerPadding ->
                                        appContent(Modifier.padding(innerPadding))
                                    }
                                } else {
                                    PermanentNavigationDrawer(
                                        drawerContent = {
                                            PermanentDrawerSheet {
                                                AppNavigationDrawerContent(navigationState, navigator)
                                            }
                                        }
                                    ) {
                                        Scaffold(topBar = topBar) { innerPadding ->
                                            appContent(Modifier.padding(innerPadding))
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Scaffold(topBar = topBar) { innerPadding ->
                            appContent(Modifier.padding(innerPadding))
                        }
                    }
                })
            }
        }
    }
}

@Composable
fun AppTopBar(
    currentScreenTitle: String,
    canNavigateBack: Boolean,
    navigator: Navigator,
    currentUser: UserData?,
    showNavToggle: Boolean,
    isNavCollapsed: Boolean,
    railState: WideNavigationRailState,
    onToggleNav: () -> Unit
) {
    val scope = rememberCoroutineScope()
    LargeFlexibleTopAppBar(
        title = { Text(currentScreenTitle, fontFamily = robotoFlexTopAppBar) },
        navigationIcon = {
            if (showNavToggle) {
                IconButton(onClick = {
                    scope.launch {
                        if (railState.currentValue == WideNavigationRailValue.Collapsed) {
                            railState.expand()
                        } else {
                            railState.collapse()
                        }
                    }
                    onToggleNav()
                }) {
                    Icon(
                        imageVector = if (isNavCollapsed) Icons.Default.Menu else Icons.AutoMirrored.Filled.MenuOpen,
                        contentDescription = if (isNavCollapsed) "Open navigation" else "Close navigation"
                    )
                }
            } else if (canNavigateBack && !bottomNavItems.any { it.label == currentScreenTitle }) {
                IconButton(onClick = dropUnlessResumed { navigator.goBack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                }
            }
        },
        actions = {
            if (currentUser?.username != null) {
                Text(
                    "Welcome back, ${currentUser.username}!",
                    modifier = Modifier.padding(end = 8.dp),
                    style = MaterialTheme.typography.bodySmall.copy(textAlign = TextAlign.Start)
                )
            }
            IconButton(onClick = { navigator.navigate(Screen.Account) }) {
                if (currentUser?.profilePictureUrl != null) {
                    AsyncImage(
                        model = currentUser.profilePictureUrl,
                        contentDescription = "Vehicles Picture",
                        modifier = Modifier
                            .size(32.dp)
                            .clip(MaterialShapes.Cookie12Sided.toShape()),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Default Vehicles Picture",
                        modifier = Modifier
                            .size(32.dp)
                            .clip(MaterialShapes.Cookie12Sided.toShape())
                    )
                }
            }
        }
    )
}

@Composable
fun AppBottomNavigationBar(navigationState: NavigationState, navigator: Navigator) {
    ShortNavigationBar {
        bottomNavItems.forEach { screen ->
            ShortNavigationBarItem(
                icon = { Icon(screen.icon!!, contentDescription = screen.description) },
                label = { Text(screen.label) },
                selected = navigationState.topLevelRoute == screen,
                onClick = dropUnlessResumed {
                    navigator.navigate(screen)
                }
            )
        }
    }
}

@Composable
@ExperimentalMaterial3ExpressiveApi
fun AppNavigationRail(
    navigationState: NavigationState,
    navigator: Navigator,
    railState: WideNavigationRailState
) {
    var fabMenuExpanded by rememberSaveable { mutableStateOf(false) }
    var showAddCurrencyDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val colors = colorScheme

    // Use a Box to allow the Menu to OVERLAY the content layer
    Box(modifier = Modifier.fillMaxHeight()) {
        WideNavigationRail(
            state = railState,
            header = {
                IconButton(
                    onClick = { 
                        scope.launch { 
                            if (railState.currentValue == WideNavigationRailValue.Collapsed) {
                                railState.expand()
                            } else {
                                railState.collapse()
                            }
                        } 
                    }
                ) {
                    Icon(
                        painter = rememberVectorPainter(Icons.Rounded.Menu),
                        contentDescription = "Menu",
                        modifier = Modifier.size(24.dp)
                    )
                }
                ToggleFloatingActionButton(
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .semantics {
                            stateDescription = if (fabMenuExpanded) "Expanded" else "Collapsed"
                            contentDescription = "Toggle menu"
                        }
                        .animateFloatingActionButton(visible = true, alignment = Alignment.Center),
                    checked = fabMenuExpanded,
                    containerColor = { colors.primaryContainer },
                    onCheckedChange = { fabMenuExpanded = !fabMenuExpanded }
                ) {
                    val imageVector by remember {
                        derivedStateOf {
                            if (fabMenuExpanded) Icons.Filled.Close else Icons.Filled.Add
                        }
                    }
                    Icon(
                        painter = rememberVectorPainter(imageVector),
                        contentDescription = null,
                        modifier = Modifier.animateIcon({ if (fabMenuExpanded) 1f else 0f })
                    )
                }
            },

        ) {
            bottomNavItems.forEach { screen ->
                WideNavigationRailItem(
                    icon = { Icon(screen.icon!!, contentDescription = screen.description) },
                    label = { Text(screen.label) },
                    selected = navigationState.topLevelRoute == screen,
                    onClick = dropUnlessResumed {
                        navigator.navigate(screen)
                    },
                    railExpanded = true,
                )
            }
        }

        // FloatingActionButtonMenu is outside the Rail but inside the Box.
        // This ensures it overlays the main content screen.
        FloatingActionButtonMenu(
            modifier = Modifier
                .align(Alignment.TopStart)
                // Offset horizontally by the standard Rail width (approx 80dp)
                // and vertically to match the FAB position.
                .padding(start = 80.dp, top = 8.dp),
            expanded = fabMenuExpanded,
            button = { /* Leave empty: Trigger is in the Rail Header */ }
        ) {
            FloatingActionButtonMenuItem(
                onClick = { fabMenuExpanded = false },
                icon = { Icon(Icons.Rounded.Add, contentDescription = null) },
                text = { Text(text = "New Trip") },
            )
            FloatingActionButtonMenuItem(
                onClick = { fabMenuExpanded = false },
                icon = { Icon(Icons.Rounded.GroupAdd, contentDescription = null) },
                text = { Text(text = "New Group Trip") },
            )
            FloatingActionButtonMenuItem(
                onClick = { fabMenuExpanded = false },
                icon = { Icon(Icons.Rounded.DirectionsCar, contentDescription = null) },
                text = { Text(text = "New Vehicle") },
            )
            FloatingActionButtonMenuItem(
                onClick = { fabMenuExpanded = false; showAddCurrencyDialog = true},
                icon = { Icon(Icons.Rounded.Money, contentDescription = null) },
                text = { Text(text = "New Currency") },
            )
        }
    }
}
@Composable
fun AppNavigationDrawerContent(navigationState: NavigationState, navigator: Navigator) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        bottomNavItems.forEach { screen ->
            NavigationDrawerItem(
                icon = { Icon(screen.icon!!, contentDescription = screen.description) },
                label = { Text(screen.label) },
                selected = navigationState.topLevelRoute == screen,
                onClick = dropUnlessResumed {
                    navigator.navigate(screen)
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppContent(
    navigator: Navigator,
    navigationState: NavigationState,
    modifier: Modifier = Modifier,
    windowWidthSizeClass: WindowWidthSizeClass,
    carViewModel: CarViewModel,
    firebaseAuthClient: FirebaseAuthClient,
    coroutineScope: CoroutineScope,
    onGoogleSignInClick: () -> Unit,
    onMicrosoftSignInClick: () -> Unit,
    onEmailSignInClick: (String, String) -> Unit,
    onEmailSignUpClick: (String, String) -> Unit,
    onSendOtpClick: (String) -> Unit,
    onVerifyOtpClick: (String) -> Unit
) {
    val savedVehicles by carViewModel.savedVehicles.collectAsState()
    val isGoogleUser = carViewModel.isGoogleUser()
    val context = LocalContext.current

    val entryProvider = entryProvider<NavKey> {
        entry<Screen.SignIn> {
            SignInScreen(
                onEmailSignInClick = onEmailSignInClick,
                onGoogleSignInClick = onGoogleSignInClick,
                onMicrosoftSignInClick = onMicrosoftSignInClick,
                onSignUpClick = dropUnlessResumed { navigator.navigate(Screen.SignUp) },
                onForgotPasswordClick = { email ->
                    if (email.isBlank()) {
                        Toast.makeText(context, "Please enter your email address to reset password", Toast.LENGTH_LONG).show()
                    } else {
                        coroutineScope.launch {
                            val result = firebaseAuthClient.sendPasswordResetEmail(email)
                            if (result.isSuccess) {
                                Toast.makeText(context, "Password reset email sent", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(context, "Failed to send reset email: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            )
        }
        entry<Screen.SignUp>(
            metadata = metadata {
                put(NavDisplay.TransitionKey) {
                    slideInHorizontally(tween(300)) { it } togetherWith ExitTransition.KeepUntilTransitionsFinished
                }
                put(NavDisplay.PopTransitionKey) {
                    EnterTransition.None togetherWith slideOutHorizontally(tween(300)) { it }
                }
                put(NavDisplay.PredictivePopTransitionKey) {
                    EnterTransition.None togetherWith slideOutHorizontally(tween(300)) { it }
                }
            }
        ) {
            SignUpScreen(
                onSignUpClick = onEmailSignUpClick,
                onSignInClick = dropUnlessResumed { navigator.goBack() },
                onBackClick = dropUnlessResumed { navigator.goBack() }
            )
        }
        entry<Screen.Vehicles> {
            ProfileScreen(
                savedVehicles = savedVehicles,
                onAddVehicleClick = dropUnlessResumed { navigator.navigate(Screen.AddVehicle()) },
                onEditVehicleClick = { vehicleName ->
                    navigator.navigate(Screen.AddVehicle(vehicleName))
                },
                onCurrencySettingsClick = dropUnlessResumed { navigator.navigate(Screen.CurrencySettings) },
                onAddVehicle = { vehicle: Vehicle ->
                    coroutineScope.launch {
                        carViewModel.addVehicle(
                            vehicle
                        )
                    }
                },
                onEditVehicle = { oldName: String, newVehicle: Vehicle ->
                    coroutineScope.launch {
                        carViewModel.updateVehicle(
                            newVehicle
                        )
                    }
                },
                onDeleteVehicle = { vehicleId: String ->
                    coroutineScope.launch {
                        val success = carViewModel.deleteVehicle(vehicleId)
                        if (!success) {
                            Toast.makeText(
                                context,
                                "Vehicle has trip data, profile cannot be deleted !!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                },
                canDeleteVehicle = { vehicleId: String ->
                    !carViewModel.vehicleIdsWithTrips.value.contains(vehicleId)
                }
            )
        }
        entry<Screen.Trips> {
            TripsScreen(
                windowWidthSizeClass = windowWidthSizeClass,
                carViewModel = carViewModel,
                onNavigateToTripDetails = dropUnlessResumed { navigator.navigate(Screen.TripDetails) }
            )
        }
        entry<Screen.TripDetails>(
            metadata = metadata {
                put(NavDisplay.TransitionKey) {
                    slideInHorizontally(tween(300)) { it } togetherWith ExitTransition.KeepUntilTransitionsFinished
                }
                put(NavDisplay.PopTransitionKey) {
                    EnterTransition.None togetherWith slideOutHorizontally(tween(300)) { it }
                }
                put(NavDisplay.PredictivePopTransitionKey) {
                    EnterTransition.None togetherWith slideOutHorizontally(tween(300)) { it }
                }
            }
        ) {
            MileageCalculatorScreen(
                carViewModel = carViewModel
            )
        }
        entry<Screen.AddVehicle>(
            metadata = metadata {
                put(NavDisplay.TransitionKey) {
                    slideInHorizontally(tween(300)) { it } togetherWith ExitTransition.KeepUntilTransitionsFinished
                }
                put(NavDisplay.PopTransitionKey) {
                    EnterTransition.None togetherWith slideOutHorizontally(tween(300)) { it }
                }
                put(NavDisplay.PredictivePopTransitionKey) {
                    EnterTransition.None togetherWith slideOutHorizontally(tween(300)) { it }
                }
            }
        ) { key ->
            val vehicleToEdit = savedVehicles.find { it.name == key.vehicleName }
            AddVehicleScreen(
                onSaveVehicle = { newVehicle: Vehicle ->
                    vehicleToEdit?.let { oldVehicle ->
                        coroutineScope.launch { carViewModel.updateVehicle(newVehicle) }
                    } ?: coroutineScope.launch { carViewModel.addVehicle(newVehicle) }
                },
                onBack = dropUnlessResumed { navigator.goBack() },
                vehicleToEdit = vehicleToEdit,
                windowWidthSizeClass = windowWidthSizeClass
            )
        }
        entry<Screen.Account>(
            metadata = metadata {
                put(NavDisplay.TransitionKey) {
                    slideInHorizontally(tween(300)) { it } togetherWith ExitTransition.KeepUntilTransitionsFinished
                }
                put(NavDisplay.PopTransitionKey) {
                    EnterTransition.None togetherWith slideOutHorizontally(tween(300)) { it }
                }
                put(NavDisplay.PredictivePopTransitionKey) {
                    EnterTransition.None togetherWith slideOutHorizontally(tween(300)) { it }
                }
            }
        ) {
            AccountScreen(
                currentUser = carViewModel.currentUser.collectAsState().value,
                onSignOutClick = {
                    coroutineScope.launch {
                        firebaseAuthClient.signOut()
                        carViewModel.updateSignInState(null)
                        navigator.navigate(Screen.SignIn)
                        Log.d("MainActivity", "User signed out.")
                    }
                },
                onNavigateToPersonalInfo = dropUnlessResumed { navigator.navigate(Screen.PersonalInfo) },
                onNavigateToSecurity = dropUnlessResumed { navigator.navigate(Screen.SecuritySettings) },
                windowWidthSizeClass = windowWidthSizeClass
            )
            // Google Drive backup section:
            if (isGoogleUser) {
                Button(
                    onClick = {
                        carViewModel.backupTripsToDrive { success, msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("Backup trips to Google Drive")
                }
            } else {
                Text(
                    text = "Sign in with Google to enable Drive backup.",
                    color = colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        entry<Screen.PersonalInfo>(
            metadata = metadata {
                put(NavDisplay.TransitionKey) {
                    slideInHorizontally(tween(300)) { it } togetherWith ExitTransition.KeepUntilTransitionsFinished
                }
                put(NavDisplay.PopTransitionKey) {
                    EnterTransition.None togetherWith slideOutHorizontally(tween(300)) { it }
                }
                put(NavDisplay.PredictivePopTransitionKey) {
                    EnterTransition.None togetherWith slideOutHorizontally(tween(300)) { it }
                }
            }
        ) { PersonalInfoScreen(carViewModel = carViewModel) }
        entry<Screen.SecuritySettings>(
            metadata = metadata {
                put(NavDisplay.TransitionKey) {
                    slideInHorizontally(tween(300)) { it } togetherWith ExitTransition.KeepUntilTransitionsFinished
                }
                put(NavDisplay.PopTransitionKey) {
                    EnterTransition.None togetherWith slideOutHorizontally(tween(300)) { it }
                }
                put(NavDisplay.PredictivePopTransitionKey) {
                    EnterTransition.None togetherWith slideOutHorizontally(tween(300)) { it }
                }
            }
        ) { SecuritySettingsScreen(carViewModel = carViewModel) }
        entry<Screen.CurrencySettings>(
            metadata = metadata {
                put(NavDisplay.TransitionKey) {
                    slideInHorizontally(tween(300)) { it } togetherWith ExitTransition.KeepUntilTransitionsFinished
                }
                put(NavDisplay.PopTransitionKey) {
                    EnterTransition.None togetherWith slideOutHorizontally(tween(300)) { it }
                }
                put(NavDisplay.PredictivePopTransitionKey) {
                    EnterTransition.None togetherWith slideOutHorizontally(tween(300)) { it }
                }
            }
        ) { CurrencySettingsScreen(carViewModel = carViewModel) }
        entry<Screen.Activities> { ActivitiesScreen(carViewModel = carViewModel) }
    }

    NavDisplay(
        entries = navigationState.toEntries(entryProvider),
        onBack = { navigator.goBack() },
        modifier = modifier,
        transitionSpec = {
            slideInHorizontally(tween(280)) { it / 4 } togetherWith fadeOut(tween(220))
        },
        popTransitionSpec = {
            fadeIn(tween(220)) togetherWith slideOutHorizontally(tween(280)) { it / 4 }
        },
        predictivePopTransitionSpec = {
            fadeIn(tween(220)) togetherWith slideOutHorizontally(tween(280)) { it / 4 }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyMileageTheme({
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("App Preview Root")
        }
    })
}
