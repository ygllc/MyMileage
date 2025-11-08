@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.yg.mileage

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShortNavigationBar
import androidx.compose.material3.ShortNavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.yg.mileage.auth.FirebaseAuthClient
import com.yg.mileage.auth.SignInScreen
import com.yg.mileage.data.Repository
import com.yg.mileage.navigation.Screen
import com.yg.mileage.navigation.bottomNavItems
import com.yg.mileage.ui.theme.MileageCalculatorTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
class MainActivity : ComponentActivity() {
    private lateinit var carViewModel: CarViewModel
    private lateinit var firebaseAuthClient: FirebaseAuthClient

    private var phoneVerificationId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuthClient = FirebaseAuthClient(applicationContext)

        val repository = Repository.getRepository(applicationContext)
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
                    CircularProgressIndicator()
                }
            } else {
                MileageCalculatorTheme {
                    val navController = rememberNavController()
                    val startDestination = if (currentUser == null) Screen.SignIn.route else Screen.TripLog.route
                    var currentScreenTitle by remember { mutableStateOf(Screen.TripLog.label) }
                    var canNavigateBack by remember { mutableStateOf(false) }
                    val coroutineScope = rememberCoroutineScope()

                    LaunchedEffect(navController) {
                        navController.currentBackStackEntryFlow.collect { backStackEntry ->
                            val screen = when (val route = backStackEntry.destination.route) {
                                Screen.TripDetails.route -> Screen.TripDetails
                                Screen.Profile.route -> Screen.Profile
                                Screen.TripLog.route -> Screen.TripLog
                                Screen.AddVehicle.route -> Screen.AddVehicle
                                Screen.Account.route -> Screen.Account
                                Screen.PersonalInfo.route -> Screen.PersonalInfo
                                Screen.SecuritySettings.route -> Screen.SecuritySettings
                                Screen.SignIn.route -> Screen.SignIn
                                else -> null
                            }
                            currentScreenTitle = screen?.label ?: "App"
                            canNavigateBack = navController.previousBackStackEntry != null
                        }
                    }
                    LaunchedEffect(Unit) {
                        carViewModel.signInCompleted.collectLatest {
                            navController.navigate(Screen.TripLog.route) {
                                popUpTo(0)
                                launchSingleTop = true
                            }
                        }
                    }

                    Scaffold(
                        topBar = {
                            if (currentScreenTitle != Screen.SignIn.label) {
                                LargeFlexibleTopAppBar(
                                    title = { Text(currentScreenTitle) },
                                    navigationIcon = {
                                        if (canNavigateBack && !bottomNavItems.any { it.route == currentScreenTitle.lowercase() }) {
                                            IconButton(onClick = { navController.navigateUp() }) {
                                                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                                            }
                                        }
                                    },
                                    actions = {
                                        if (currentUser != null) {
                                            Text(
                                                currentUser?.username?.take(10) ?: "",
                                                modifier = Modifier.padding(end = 8.dp),
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                        IconButton(onClick = { navController.navigate(Screen.Account.route) }) {
                                            Icon(Icons.Default.AccountCircle, contentDescription = "Account")
                                        }
                                    }
                                )
                            }
                        },
                        bottomBar = {
                            if (currentUser != null) {
                                AppBottomNavigationBar(navController)
                            }
                        }
                    ) { innerPadding ->
                        AppNavHost(
                            navController = navController,
                            modifier = Modifier.padding(innerPadding),
                            carViewModel = carViewModel,
                            firebaseAuthClient = firebaseAuthClient,
                            coroutineScope = coroutineScope,
                            startDestination = startDestination,
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
                }
            }
        }
    }
}

@Composable
fun AppBottomNavigationBar(navController: NavHostController) {
    ShortNavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        bottomNavItems.forEach { screen ->
            ShortNavigationBarItem(
                icon = { Icon(screen.icon!!, contentDescription = screen.label) },
                label = { Text(screen.label) },
                selected = currentDestination?.route == screen.route,
                onClick = {
                    if (currentDestination?.route != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    carViewModel: CarViewModel,
    firebaseAuthClient: FirebaseAuthClient,
    coroutineScope: CoroutineScope,
    startDestination: String,
    onGoogleSignInClick: () -> Unit,
    onMicrosoftSignInClick: () -> Unit,
    onEmailSignInClick: (String, String) -> Unit,
    onEmailSignUpClick: (String, String) -> Unit,
    onSendOtpClick: (String) -> Unit,
    onVerifyOtpClick: (String) -> Unit
) {
    val savedVehicles by carViewModel.savedVehicles.collectAsState()
    var backupResultMsg by remember { mutableStateOf<String?>(null) }
    val isGoogleUser = carViewModel.isGoogleUser()
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.SignIn.route) {
            SignInScreen(
                onEmailSignInClick = onEmailSignInClick,
                onGoogleSignInClick = onGoogleSignInClick,
                onMicrosoftSignInClick = onMicrosoftSignInClick
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                navController = navController,
                savedVehicles = savedVehicles,
                onAddVehicle = { vehicle: Vehicle -> coroutineScope.launch { carViewModel.addVehicle(vehicle) } },
                onEditVehicle = { oldName: String, newVehicle: Vehicle -> coroutineScope.launch { carViewModel.updateVehicle(newVehicle) } },
                onDeleteVehicle = { vehicleId: String ->
                    coroutineScope.launch {
                        val success = carViewModel.deleteVehicle(vehicleId)
                        if (!success) {
                            Toast.makeText(context, "Vehicle has trip data, profile cannot be deleted !!", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                canDeleteVehicle = { vehicleId: String -> runBlocking { carViewModel.canDeleteVehicle(vehicleId) } }
            )
        }
        composable(Screen.TripLog.route) {
            TripLogScreen(
                carViewModel = carViewModel,
                onNavigateToTripDetails = { navController.navigate("trip_details") }
            )
        }
        composable("trip_details") {
            MileageCalculatorScreen(
                carViewModel = carViewModel
            )
        }
        composable(Screen.AddVehicle.route) {
            AddVehicleScreen(
                navController = navController,
                onSaveVehicle = { vehicle: Vehicle -> coroutineScope.launch { carViewModel.addVehicle(vehicle) } }
            )
        }
        composable(
            route = "add_vehicle/{vehicleName}",
            arguments = listOf(
                navArgument("vehicleName") { type = NavType.StringType; nullable = true; defaultValue = null }
            )
        ) { backStackEntry ->
            val vehicleName = backStackEntry.arguments?.getString("vehicleName")
            val vehicleToEdit = savedVehicles.find { it.name == vehicleName }
            AddVehicleScreen(
                navController = navController,
                onSaveVehicle = { newVehicle: Vehicle ->
                    vehicleToEdit?.let { oldVehicle ->
                        coroutineScope.launch { carViewModel.updateVehicle(newVehicle) }
                    } ?: coroutineScope.launch { carViewModel.addVehicle(newVehicle) }
                },
                vehicleToEdit = vehicleToEdit
            )
        }
        composable(Screen.Account.route) {
            AccountScreen(
                currentUser = carViewModel.currentUser.collectAsState().value,
                onSignOutClick = {
                    coroutineScope.launch {
                        firebaseAuthClient.signOut()
                        carViewModel.updateSignInState(null)
                        navController.navigate(Screen.SignIn.route) {
                            popUpTo(0)
                        }
                        Log.d("MainActivity", "User signed out.")
                    }
                },
                onGoogleSignInClick = onGoogleSignInClick,
                onEmailSignInClick = onEmailSignInClick,
                onEmailSignUpClick = onEmailSignUpClick,
                onSendOtpClick = onSendOtpClick,
                onVerifyOtpClick = onVerifyOtpClick,
                onNavigateToPersonalInfo = { navController.navigate(Screen.PersonalInfo.route) },
                onNavigateToSecurity = { navController.navigate(Screen.SecuritySettings.route) }
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
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        composable(Screen.PersonalInfo.route) { PersonalInfoScreen(carViewModel = carViewModel) }
        composable(Screen.SecuritySettings.route) { SecuritySettingsScreen(carViewModel = carViewModel) }
        composable(Screen.CurrencySettings.route) { CurrencySettingsScreen(carViewModel = carViewModel) }
        composable(Screen.Activities.route) {  }
        }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MileageCalculatorTheme {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("App Preview Root")
        }
    }
}
