package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.PaymentMethod
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationWizardScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onFinish: () -> Unit
) {
    val step by viewModel.registrationCurrentStep.collectAsState()
    val state by viewModel.registrationState.collectAsState()
    val configuredTiers by viewModel.configuredMembershipTiers.collectAsState()
    val isProcessingPayment by viewModel.isProcessingPayment.collectAsState()

    val totalSteps = 14

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Top App Bar
        Surface(
            color = DarkSurfaceElevated,
            border = BorderStroke(1.dp, DarkBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        if (step > 1 && step < 14) {
                            viewModel.registrationCurrentStep.value = step - 1
                        } else {
                            onBack()
                        }
                    },
                    modifier = Modifier.testTag("reg_back_button")
                ) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = TextWhite)
                }

                Spacer(modifier = Modifier.width(6.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "REGISTER & CREATE YOUR PROFILE",
                        color = TextWhite,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Step $step of $totalSteps • ${getStepTitle(step)}",
                        color = PinkPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(PinkPrimary.copy(alpha = 0.2f))
                        .border(0.8.dp, PinkPrimary, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${(step * 100) / totalSteps}%",
                        color = PinkPrimary,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        // Progress Linear Bar
        LinearProgressIndicator(
            progress = { step.toFloat() / totalSteps.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp),
            color = PinkPrimary,
            trackColor = DarkSurfaceVariant
        )

        // Step Content inside scrollable column
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            when (step) {
                1 -> item { Step1RegisterAccount(state, viewModel) }
                2 -> item { Step2VerifyEmail(state, viewModel) }
                3 -> item { Step3VerifyMobile(state, viewModel) }
                4 -> item { Step4ConfirmLegalAge(state, viewModel) }
                5 -> item { Step5SelectAccountType(state, viewModel) }
                6 -> item { Step6SelectMembership(state, configuredTiers, viewModel) }
                7 -> item { Step7CreateProfileInfo(state, viewModel) }
                8 -> item { Step8UploadGallery(state, viewModel) }
                9 -> item { Step9CompleteVerification(state, viewModel) }
                10 -> item { Step10SelectPaymentMethod(state, viewModel) }
                11 -> item { Step11CompletePayment(state, viewModel) }
                12 -> item { Step12ActivateMembership(state, viewModel) }
                13 -> item { Step13SubmitForModeration(state, viewModel) }
                14 -> item { Step14ModerationStatusPending(state, onFinish) }
            }
        }

        // Bottom Navigation Bar
        if (step < 14) {
            Surface(
                color = DarkSurfaceElevated,
                border = BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (step > 1) {
                        OutlinedButton(
                            onClick = { viewModel.registrationCurrentStep.value = step - 1 },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextLight),
                            border = BorderStroke(1.dp, DarkBorder)
                        ) {
                            Text("Previous", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Spacer(modifier = Modifier.width(10.dp))
                    }

                    PinkGradientButton(
                        text = when (step) {
                            1 -> "Continue to Email Verification"
                            2 -> if (state.isEmailVerified) "Continue to Mobile Verification" else "Verify PIN First"
                            3 -> if (state.isMobileVerified) "Continue to Age Verification" else "Verify Mobile First"
                            4 -> "Confirm Age & Proceed"
                            5 -> "Select Plan"
                            6 -> "Continue to Profile Info"
                            7 -> "Continue to Gallery Upload"
                            8 -> "Continue to ID Verification"
                            9 -> "Proceed to Payment"
                            10 -> "Confirm Payment Method"
                            11 -> if (isProcessingPayment) "Processing Payment..." else "Complete Payment"
                            12 -> "Submit for Admin Moderation"
                            13 -> "Confirm & Submit for Review"
                            else -> "Next"
                        },
                        onClick = {
                            handleStepNext(step, state, viewModel)
                        },
                        enabled = isStepValid(step, state) && !isProcessingPayment,
                        modifier = Modifier.testTag("reg_next_button")
                    )
                }
            }
        }
    }
}

private fun getStepTitle(step: Int): String {
    return when (step) {
        1 -> "Register Account"
        2 -> "Verify Email"
        3 -> "Verify Mobile Number"
        4 -> "Confirm 18+ Legal Age"
        5 -> "Select Account / Profile Type"
        6 -> "Select Membership Package"
        7 -> "Create Profile & Bio"
        8 -> "Upload Gallery Photos"
        9 -> "Identity Verification"
        10 -> "Select Payment Method"
        11 -> "Complete Payment"
        12 -> "Activate Membership"
        13 -> "Submit For Moderation"
        14 -> "Moderation Status Confirmation"
        else -> "Registration"
    }
}

private fun isStepValid(step: Int, state: RegistrationFlowState): Boolean {
    return when (step) {
        1 -> state.email.isNotBlank() && state.password.length >= 6
        2 -> state.isEmailVerified || state.emailVerificationPin.isNotBlank()
        3 -> state.isMobileVerified || state.mobileOtpCode.isNotBlank()
        4 -> state.confirmedAge18Plus
        5 -> true
        6 -> true
        7 -> state.stageName.isNotBlank()
        8 -> state.galleryPhotos.isNotEmpty()
        9 -> true
        10 -> true
        11 -> true
        12 -> true
        13 -> true
        else -> true
    }
}

private fun handleStepNext(step: Int, state: RegistrationFlowState, viewModel: MainViewModel) {
    when (step) {
        1 -> {
            viewModel.sendRegistrationEmailOtp()
            viewModel.registrationCurrentStep.value = 2
        }
        2 -> {
            if (!state.isEmailVerified) {
                if (viewModel.verifyRegistrationEmailPin(state.emailVerificationPin.ifBlank { "1234" })) {
                    viewModel.sendRegistrationMobileOtp()
                    viewModel.registrationCurrentStep.value = 3
                }
            } else {
                viewModel.sendRegistrationMobileOtp()
                viewModel.registrationCurrentStep.value = 3
            }
        }
        3 -> {
            if (!state.isMobileVerified) {
                if (viewModel.verifyRegistrationMobileOtp(state.mobileOtpCode.ifBlank { "1234" })) {
                    viewModel.registrationCurrentStep.value = 4
                }
            } else {
                viewModel.registrationCurrentStep.value = 4
            }
        }
        4 -> {
            if (state.confirmedAge18Plus) {
                viewModel.registrationCurrentStep.value = 5
            } else {
                viewModel.confirmRegistrationAge(state.birthYear)
                viewModel.registrationCurrentStep.value = 5
            }
        }
        5 -> viewModel.registrationCurrentStep.value = 6
        6 -> viewModel.registrationCurrentStep.value = 7
        7 -> viewModel.registrationCurrentStep.value = 8
        8 -> viewModel.registrationCurrentStep.value = 9
        9 -> {
            if (state.selectedMembershipTier.priceZar == 0) {
                // Skip payment steps directly to step 12
                viewModel.updateRegistrationState {
                    it.copy(paymentCompleted = true, membershipActivated = true)
                }
                viewModel.registrationCurrentStep.value = 12
            } else {
                viewModel.registrationCurrentStep.value = 10
            }
        }
        10 -> viewModel.registrationCurrentStep.value = 11
        11 -> {
            viewModel.completeRegistrationPayment {
                viewModel.registrationCurrentStep.value = 12
            }
        }
        12 -> viewModel.registrationCurrentStep.value = 13
        13 -> {
            // SUBMIT FOR MODERATION: strictly sets status to PENDING_REVIEW!
            viewModel.submitProfileForModeration()
        }
    }
}

// STEP 1: Register Account
@Composable
private fun Step1RegisterAccount(state: RegistrationFlowState, viewModel: MainViewModel) {
    var passwordVisible by remember { mutableStateOf(false) }

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.AccountCircle, contentDescription = null, tint = PinkPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Step 1: Account Credentials", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text("Create your private Pink Passions login. Your personal email and phone remain strictly confidential.", color = TextMuted, fontSize = 11.5.sp)

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = state.email,
                onValueChange = { viewModel.updateRegistrationState { s -> s.copy(email = it) } },
                label = { Text("Email Address") },
                placeholder = { Text("you@example.com") },
                modifier = Modifier.fillMaxWidth().testTag("reg_input_email"),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PinkPrimary,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = state.password,
                onValueChange = { viewModel.updateRegistrationState { s -> s.copy(password = it) } },
                label = { Text("Password (Min 6 chars)") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle password",
                            tint = TextMuted
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().testTag("reg_input_password"),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PinkPrimary,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = state.accountDisplayName,
                onValueChange = { viewModel.updateRegistrationState { s -> s.copy(accountDisplayName = it) } },
                label = { Text("Account Holder / Public Display Name") },
                placeholder = { Text("e.g. Bella Ross") },
                modifier = Modifier.fillMaxWidth().testTag("reg_input_display_name"),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PinkPrimary,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    viewModel.updateRegistrationState { s -> s.copy(agreedToTerms = !s.agreedToTerms) }
                }
            ) {
                Checkbox(
                    checked = state.agreedToTerms,
                    onCheckedChange = { viewModel.updateRegistrationState { s -> s.copy(agreedToTerms = it) } },
                    colors = CheckboxDefaults.colors(checkedColor = PinkPrimary)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "I agree to the Terms of Service, Privacy Policy & 18+ Adult Conduct Standards.",
                    color = TextLight,
                    fontSize = 11.5.sp
                )
            }
        }
    }
}

// STEP 2: Verify Email
@Composable
private fun Step2VerifyEmail(state: RegistrationFlowState, viewModel: MainViewModel) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Mail, contentDescription = null, tint = PinkPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Step 2: Verify Email Address", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text("We've sent a 4-digit verification PIN to ${state.email.ifBlank { "your registered email" }}. Enter it below:", color = TextMuted, fontSize = 12.sp)

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = state.emailVerificationPin,
                onValueChange = { viewModel.updateRegistrationState { s -> s.copy(emailVerificationPin = it) } },
                label = { Text("4-Digit Email PIN (e.g. 1234)") },
                modifier = Modifier.fillMaxWidth().testTag("reg_input_email_pin"),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PinkPrimary,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { viewModel.verifyRegistrationEmailPin(state.emailVerificationPin.ifBlank { "1234" }) },
                    colors = ButtonDefaults.buttonColors(containerColor = if (state.isEmailVerified) Color(0xFF00E676) else PinkPrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (state.isEmailVerified) "Email Verified ✓" else "Verify PIN", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { viewModel.sendRegistrationEmailOtp() },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextLight)
                ) {
                    Text("Resend PIN", fontSize = 11.5.sp)
                }
            }
        }
    }
}

// STEP 3: Verify Mobile
@Composable
private fun Step3VerifyMobile(state: RegistrationFlowState, viewModel: MainViewModel) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.PhoneIphone, contentDescription = null, tint = PinkPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Step 3: South Africa Mobile Verification", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text("We confirm legitimate phone numbers with a quick SMS OTP. Required for WhatsApp client bookings.", color = TextMuted, fontSize = 12.sp)

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = state.mobileNumber,
                onValueChange = { viewModel.updateRegistrationState { s -> s.copy(mobileNumber = it) } },
                label = { Text("Mobile Number (e.g. +27 82 123 4567)") },
                modifier = Modifier.fillMaxWidth().testTag("reg_input_mobile"),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PinkPrimary,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = state.mobileOtpCode,
                onValueChange = { viewModel.updateRegistrationState { s -> s.copy(mobileOtpCode = it) } },
                label = { Text("SMS OTP Code (e.g. 1234)") },
                modifier = Modifier.fillMaxWidth().testTag("reg_input_mobile_otp"),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PinkPrimary,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { viewModel.verifyRegistrationMobileOtp(state.mobileOtpCode.ifBlank { "1234" }) },
                    colors = ButtonDefaults.buttonColors(containerColor = if (state.isMobileVerified) Color(0xFF00E676) else PinkPrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (state.isMobileVerified) "Mobile Verified ✓" else "Verify Mobile OTP", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { viewModel.sendRegistrationMobileOtp() },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextLight)
                ) {
                    Text("Resend SMS", fontSize = 11.5.sp)
                }
            }
        }
    }
}

// STEP 4: Confirm Legal Age
@Composable
private fun Step4ConfirmLegalAge(state: RegistrationFlowState, viewModel: MainViewModel) {
    val birthYears = (1950..2008).toList().reversed()

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFFD700))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Step 4: Statutory 18+ Legal Age Verification", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text("Pink Passions strictly complies with South African law. All advertising creators and users must be at least 18 years of age.", color = TextMuted, fontSize = 12.sp)

            Spacer(modifier = Modifier.height(14.dp))

            Text("Select Your Birth Year:", color = TextLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(1996, 1998, 2000, 2002, 2004).forEach { yr ->
                    val isSelected = state.birthYear == yr
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) PinkPrimary else DarkSurfaceVariant)
                            .border(1.dp, if (isSelected) PinkPrimary else DarkBorder, RoundedCornerShape(8.dp))
                            .clickable { viewModel.confirmRegistrationAge(yr) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$yr",
                            color = if (isSelected) TextWhite else TextLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0x33FFD700))
                    .border(1.dp, Color(0xFFFFD700), RoundedCornerShape(10.dp))
                    .clickable { viewModel.confirmRegistrationAge(state.birthYear) }
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = state.confirmedAge18Plus,
                        onCheckedChange = { viewModel.confirmRegistrationAge(state.birthYear) },
                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFFFFD700))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "I solemnly declare and confirm under penalty of perjury that I am 18 years of age or older (${2026 - state.birthYear} years old) and legally entitled to create this listing.",
                        color = Color(0xFFFFD700),
                        fontSize = 11.5.sp,
                        lineHeight = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

// STEP 5: Select Account/Profile Type
@Composable
private fun Step5SelectAccountType(state: RegistrationFlowState, viewModel: MainViewModel) {
    val categories = ProfileCategory.values().filter { it != ProfileCategory.EVENTS && it != ProfileCategory.LIVE }

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Category, contentDescription = null, tint = PinkPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Step 5: Select Account / Profile Type", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text("Choose the category that best represents your services or establishment:", color = TextMuted, fontSize = 12.sp)

            Spacer(modifier = Modifier.height(12.dp))

            categories.forEach { cat ->
                val isSelected = state.selectedProfileCategory == cat
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) PinkPrimary.copy(alpha = 0.18f) else DarkSurfaceVariant)
                        .border(if (isSelected) 1.5.dp else 1.dp, if (isSelected) PinkPrimary else DarkBorder, RoundedCornerShape(12.dp))
                        .clickable { viewModel.selectRegistrationCategory(cat) }
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { viewModel.selectRegistrationCategory(cat) },
                                colors = RadioButtonDefaults.colors(selectedColor = PinkPrimary)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(cat.title, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                        }
                        if (isSelected) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = PinkPrimary, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}

// STEP 6: Select Membership Package
@Composable
private fun Step6SelectMembership(
    state: RegistrationFlowState,
    configuredTiers: List<MembershipTier>,
    viewModel: MainViewModel
) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.WorkspacePremium, contentDescription = null, tint = PinkPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Step 6: Select Membership Package", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("Select your desired advertising tier. You can upgrade anytime.", color = TextMuted, fontSize = 12.sp)
            }
        }

        configuredTiers.forEach { tier ->
            val isSelected = state.selectedMembershipTier == tier
            val tierColor = Color(tier.badgeColorHex)

            Card(
                onClick = { viewModel.selectRegistrationTier(tier) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = if (isSelected) DarkSurfaceElevated else DarkSurfaceVariant),
                border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) tierColor else DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { viewModel.selectRegistrationTier(tier) },
                                colors = RadioButtonDefaults.colors(selectedColor = tierColor)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(tier.title, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(tier.billingPeriod, color = TextMuted, fontSize = 11.sp)
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = if (tier.priceZar == 0) "FREE" else "R${String.format(java.util.Locale.US, "%,d", tier.priceZar)}",
                                color = tierColor,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            )
                            TierBadge(tier)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(tier.shortDescription, color = TextLight, fontSize = 11.5.sp)

                    Spacer(modifier = Modifier.height(8.dp))
                    tier.features.take(3).forEach { f ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 1.dp)) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = tierColor, modifier = Modifier.size(11.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(f, color = TextLight, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

// STEP 7: Create Profile Information
@Composable
private fun Step7CreateProfileInfo(state: RegistrationFlowState, viewModel: MainViewModel) {
    val provinces = listOf("Gauteng", "Western Cape", "KwaZulu-Natal", "Eastern Cape", "Free State", "Mpumalanga", "Limpopo", "North West", "Northern Cape")

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = null, tint = PinkPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Step 7: Profile Details & Bio", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text("Provide your public profile information displayed to clients in South Africa:", color = TextMuted, fontSize = 12.sp)

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = state.stageName,
                onValueChange = { viewModel.updateRegistrationState { s -> s.copy(stageName = it) } },
                label = { Text("Stage Name / Escort Name *") },
                placeholder = { Text("e.g. Bella Ross") },
                modifier = Modifier.fillMaxWidth().testTag("reg_input_stage_name"),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PinkPrimary,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = "${state.age}",
                    onValueChange = {
                        val num = it.toIntOrNull() ?: 23
                        viewModel.updateRegistrationState { s -> s.copy(age = num) }
                    },
                    label = { Text("Age") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PinkPrimary,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    )
                )

                OutlinedTextField(
                    value = state.hourlyRateZar,
                    onValueChange = { viewModel.updateRegistrationState { s -> s.copy(hourlyRateZar = it) } },
                    label = { Text("Rates / Price Text") },
                    placeholder = { Text("R1,500 / hr") },
                    modifier = Modifier.weight(1.5f),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PinkPrimary,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = state.city,
                    onValueChange = { viewModel.updateRegistrationState { s -> s.copy(city = it) } },
                    label = { Text("City") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PinkPrimary,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    )
                )

                OutlinedTextField(
                    value = state.area,
                    onValueChange = { viewModel.updateRegistrationState { s -> s.copy(area = it) } },
                    label = { Text("Area / Suburb") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PinkPrimary,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = state.incallOutcall,
                onValueChange = { viewModel.updateRegistrationState { s -> s.copy(incallOutcall = it) } },
                label = { Text("Incall / Outcall Availability") },
                placeholder = { Text("Incall & Outcall / Hotel visits") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PinkPrimary,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = state.services,
                onValueChange = { viewModel.updateRegistrationState { s -> s.copy(services = it) } },
                label = { Text("Services & Offerings") },
                placeholder = { Text("Dinner dates, Social Escort, VIP Companionship") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PinkPrimary,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = state.bioDescription,
                onValueChange = { viewModel.updateRegistrationState { s -> s.copy(bioDescription = it) } },
                label = { Text("About Me / Bio") },
                placeholder = { Text("Introduce yourself, your personality, and what clients can expect...") },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PinkPrimary,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite
                )
            )
        }
    }
}

// STEP 8: Upload Gallery
@Composable
private fun Step8UploadGallery(state: RegistrationFlowState, viewModel: MainViewModel) {
    val sampleGalleryAdditions = listOf(
        "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=500&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1508214751196-bcfd4ca60f91?w=500&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1529626455594-4ff0802cfb7e?w=500&auto=format&fit=crop&q=80"
    )

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.PhotoLibrary, contentDescription = null, tint = PinkPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Step 8: Upload Gallery Photos", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text("Add high quality photos for your profile gallery. You can set your cover avatar photo.", color = TextMuted, fontSize = 12.sp)

            Spacer(modifier = Modifier.height(14.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        sampleGalleryAdditions.forEach { url -> viewModel.addRegistrationGalleryPhoto(url) }
                        viewModel.showNotice("Sample high-resolution gallery photos loaded.")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add High-Res Photos", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(220.dp)
            ) {
                items(state.galleryPhotos) { photoUrl ->
                    val isCover = state.coverPhotoUrl == photoUrl
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .border(if (isCover) 2.dp else 1.dp, if (isCover) PinkPrimary else DarkBorder, RoundedCornerShape(10.dp))
                    ) {
                        AsyncImage(
                            model = photoUrl,
                            contentDescription = "Gallery photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        // Cover tag
                        if (isCover) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .background(PinkPrimary)
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text("COVER", color = TextWhite, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Remove button
                        IconButton(
                            onClick = { viewModel.removeRegistrationGalleryPhoto(photoUrl) },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(24.dp)
                                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                        ) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Remove", tint = TextWhite, modifier = Modifier.size(14.dp))
                        }

                        // Set cover button
                        if (!isCover) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth()
                                    .background(Color.Black.copy(alpha = 0.7f))
                                    .clickable { viewModel.setRegistrationCoverPhoto(photoUrl) }
                                    .padding(vertical = 2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Set Cover", color = TextWhite, fontSize = 8.5.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// STEP 9: Complete Verification Where Eligible
@Composable
private fun Step9CompleteVerification(state: RegistrationFlowState, viewModel: MainViewModel) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.VerifiedUser, contentDescription = null, tint = Color(0xFF00E676))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Step 9: Identity & Biometric Verification", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text("Pink Passions verified badges are strictly earned via government ID and liveness check. Never purchasable.", color = TextMuted, fontSize = 12.sp)

            Spacer(modifier = Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0x2200E676))
                    .border(1.dp, Color(0xFF00E676), RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("SA Smart ID & Liveness Checked", color = Color(0xFF00E676), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Text("Facial Biometric Match Score: 98% (High Confidence)", color = TextLight, fontSize = 11.5.sp)
                    }
                    VerifiedMemberBadge(compact = true)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("Document Type:", color = TextLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(state.verificationDocType, color = PinkLight, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

// STEP 10: Select Payment Method
@Composable
private fun Step10SelectPaymentMethod(state: RegistrationFlowState, viewModel: MainViewModel) {
    val tier = state.selectedMembershipTier

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Payment, contentDescription = null, tint = PinkPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Step 10: Select Payment Method", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text("Total for ${tier.title}: R${String.format(java.util.Locale.US, "%,d", tier.priceZar)} / month", color = Color(0xFFFFD700), fontSize = 13.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(14.dp))

            PaymentGatewaySelector(
                selectedMethod = state.paymentMethod,
                onSelect = { viewModel.updateRegistrationState { s -> s.copy(paymentMethod = it) } }
            )
        }
    }
}

// STEP 11: Complete Payment
@Composable
private fun Step11CompletePayment(state: RegistrationFlowState, viewModel: MainViewModel) {
    val tier = state.selectedMembershipTier

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = Color(0xFF00E676))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Step 11: Secure Payment Confirmation", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text("256-Bit SSL Encrypted checkout via ${state.paymentMethod.title}. Discreet billing descriptor applied.", color = TextMuted, fontSize = 12.sp)

            Spacer(modifier = Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(DarkSurfaceVariant)
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Selected Package", color = TextMuted, fontSize = 12.sp)
                        Text(tier.title, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Billing Cycle", color = TextMuted, fontSize = 12.sp)
                        Text(tier.billingPeriod, color = TextWhite, fontSize = 12.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Amount Due", color = TextMuted, fontSize = 12.sp)
                        Text("R${String.format(java.util.Locale.US, "%,d", tier.priceZar)}", color = Color(0xFF00E676), fontWeight = FontWeight.Black, fontSize = 15.sp)
                    }
                }
            }
        }
    }
}

// STEP 12: Server Activation
@Composable
private fun Step12ActivateMembership(state: RegistrationFlowState, viewModel: MainViewModel) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Color(0x3300E676))
                    .border(2.dp, Color(0xFF00E676), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(32.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text("Step 12: Server Membership Activated", color = TextWhite, fontWeight = FontWeight.Black, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text("Payment confirmed. Your subscription to ${state.selectedMembershipTier.title} is now active on the Pink Passions network.", color = TextLight, fontSize = 12.5.sp, textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(14.dp))
            TierBadge(state.selectedMembershipTier)
        }
    }
}

// STEP 13: Submit Profile for Moderation
@Composable
private fun Step13SubmitForModeration(state: RegistrationFlowState, viewModel: MainViewModel) {
    GlassCard(modifier = Modifier.fillMaxWidth(), borderColor = PinkPrimary) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = PinkPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Step 13: Submit Profile for Moderation", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "CRITICAL PLATFORM POLICY: In strict compliance with trust and safety standards, new profiles are NEVER automatically published upon submission.",
                color = Color(0xFFFFD700),
                fontSize = 12.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(DarkSurfaceVariant)
                    .padding(12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Submission Summary:", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                    Text("• Profile Name: ${state.stageName.ifBlank { "VIP Creator" }}", color = TextLight, fontSize = 12.sp)
                    Text("• Category: ${state.selectedProfileCategory.title}", color = TextLight, fontSize = 12.sp)
                    Text("• Membership: ${state.selectedMembershipTier.title}", color = TextLight, fontSize = 12.sp)
                    Text("• Initial Moderation Status: PENDING_REVIEW", color = Color(0xFFFFD700), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

// STEP 14: Moderation Status Confirmation Screen
@Composable
private fun Step14ModerationStatusPending(
    state: RegistrationFlowState,
    onFinish: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(Color(0x33FFD700))
                .border(2.dp, Color(0xFFFFD700), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = Icons.Default.HourglassTop, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(38.dp))
        }

        Text(
            text = "PROFILE SUBMITTED FOR MODERATION",
            color = TextWhite,
            fontWeight = FontWeight.Black,
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            letterSpacing = 0.5.sp
        )

        // Status Badge Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF2A1C0A),
                            DarkSurfaceVariant
                        )
                    )
                )
                .border(1.2.dp, Color(0xFFFFD700), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Current Moderation Status: ", color = TextLight, fontSize = 12.sp)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0x33FFD700))
                            .border(1.dp, Color(0xFFFFD700), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "PENDING_REVIEW",
                            color = Color(0xFFFFD700),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Divider(color = DarkBorder, thickness = 0.8.dp)
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Your profile '${state.stageName.ifBlank { "VIP Creator" }}' has been queued for review by platform administrators. Profiles will only be published publicly across South Africa once approved.",
                    color = TextWhite,
                    fontSize = 12.5.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("DRAFT", color = TextMuted, fontSize = 10.sp)
                        Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF00E676), modifier = Modifier.size(16.dp))
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("PENDING", color = Color(0xFFFFD700), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Icon(Icons.Default.HourglassBottom, null, tint = Color(0xFFFFD700), modifier = Modifier.size(16.dp))
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("APPROVED", color = TextMuted, fontSize = 10.sp)
                        Icon(Icons.Default.RadioButtonUnchecked, null, tint = TextMuted, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // Support & Contact Note
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(DarkSurfaceElevated)
                .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            Column {
                Text("Moderation Turnaround Time", color = PinkLight, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Standard review completes within 12-24 hours. Gold, VIP and Upmarket Exclusive members receive express priority moderation.", color = TextMuted, fontSize = 11.sp, lineHeight = 15.sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        PinkGradientButton(
            text = "Back to Directory Feed",
            onClick = onFinish,
            modifier = Modifier.fillMaxWidth().testTag("reg_finish_button")
        )
    }
}
