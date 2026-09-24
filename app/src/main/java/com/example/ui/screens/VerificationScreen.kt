package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VerificationLevel
import com.example.model.VerificationStatus
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun VerificationScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val status by viewModel.userVerificationStatus.collectAsState()
    val level by viewModel.userVerificationLevel.collectAsState()
    
    var selectedMethodTab by remember { mutableStateOf(0) } // 0: Biometrics & ID, 1: Mobile OTP, 2: Email
    var mobileNumber by remember { mutableStateOf("+27 82 ") }
    var otpCode by remember { mutableStateOf("") }
    var emailAddress by remember { mutableStateOf("creator@pinkpassions.co.za") }
    var selectedDocType by remember { mutableStateOf("RSA Smart ID Card") }
    var isSimulatingScan by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // App Bar
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
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = TextWhite)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "Trust & Verification Centre",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Status: ", color = TextMuted, fontSize = 11.sp)
                        VerifiedMemberBadge(compact = true)
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Hero Status Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                    border = BorderStroke(1.2.dp, Color(0xFF00E676)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0x3300E676))
                                        .border(1.dp, Color(0xFF00E676), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VerifiedUser,
                                        contentDescription = null,
                                        tint = Color(0xFF00E676),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Official Badge Program",
                                        color = TextWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "NEON GREEN SHIELD BADGE",
                                        color = Color(0xFF00E676),
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }
                            VerifiedMemberBadge()
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Verified profiles earn 300% more client inquiries and rank higher in search results. The Neon Green Shield cannot be purchased with money — it is only awarded after strict identity authentication.",
                            color = TextLight,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF14141E))
                                .border(0.8.dp, DarkBorder, RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = PinkPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Privacy Guarantee: Verification ID documents & facial scans are encrypted and NEVER shown on client-facing profiles.",
                                    color = TextMuted,
                                    fontSize = 11.sp,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                }
            }

            // Method Selector Tabs
            item {
                TabRow(
                    selectedTabIndex = selectedMethodTab,
                    containerColor = DarkSurfaceElevated,
                    contentColor = PinkPrimary,
                    divider = { Divider(color = DarkBorder) }
                ) {
                    Tab(
                        selected = selectedMethodTab == 0,
                        onClick = { selectedMethodTab = 0 },
                        text = { Text("Biometrics & ID", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedMethodTab == 1,
                        onClick = { selectedMethodTab = 1 },
                        text = { Text("Mobile OTP", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedMethodTab == 2,
                        onClick = { selectedMethodTab = 2 },
                        text = { Text("Email", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            // Tab 0: Biometrics & ID
            if (selectedMethodTab == 0) {
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "1. Select Government Document",
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            listOf("South African Smart ID Card", "RSA Green Barcode ID", "Valid International Passport").forEach { doc ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { selectedDocType = doc }
                                        .padding(vertical = 4.dp)
                                ) {
                                    RadioButton(
                                        selected = selectedDocType == doc,
                                        onClick = { selectedDocType = doc },
                                        colors = RadioButtonDefaults.colors(selectedColor = PinkPrimary)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(doc, color = TextLight, fontSize = 13.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Divider(color = DarkBorder)
                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "2. AI Facial Liveness & Similarity Check",
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Compare live face geometry against your ID photograph and listing gallery pictures.",
                                color = TextMuted,
                                fontSize = 11.5.sp
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            if (isSimulatingScan) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    CircularProgressIndicator(color = Color(0xFF00E676), modifier = Modifier.size(36.dp))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Analyzing facial landmarks & EXIF metadata...", color = Color(0xFF00E676), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                            } else {
                                PinkGradientButton(
                                    text = "Start Biometric ID Verification",
                                    onClick = {
                                        isSimulatingScan = true
                                        viewModel.submitFacialAndIdVerification(selectedDocType)
                                        isSimulatingScan = false
                                    },
                                    modifier = Modifier.fillMaxWidth().testTag("start_biometric_verification_button"),
                                    icon = Icons.Default.CameraAlt
                                )
                            }
                        }
                    }
                }
            }

            // Tab 1: Mobile OTP
            if (selectedMethodTab == 1) {
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "South African Mobile Verification",
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Receive a 4-digit SMS OTP to verify your active WhatsApp / call line.",
                                color = TextMuted,
                                fontSize = 11.5.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = mobileNumber,
                                onValueChange = { mobileNumber = it },
                                label = { Text("Mobile Number (+27)") },
                                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = PinkPrimary) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PinkPrimary,
                                    unfocusedBorderColor = DarkBorder,
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = otpCode,
                                onValueChange = { otpCode = it },
                                label = { Text("4-Digit OTP Code (Enter 1234 for demo)") },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF00E676)) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF00E676),
                                    unfocusedBorderColor = DarkBorder,
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            PinkGradientButton(
                                text = "Verify Mobile OTP",
                                onClick = {
                                    viewModel.submitMobileVerification(mobileNumber, otpCode)
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            // Tab 2: Email
            if (selectedMethodTab == 2) {
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Email Address Verification",
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = emailAddress,
                                onValueChange = { emailAddress = it },
                                label = { Text("Email Address") },
                                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = PinkPrimary) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PinkPrimary,
                                    unfocusedBorderColor = DarkBorder,
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            PinkGradientButton(
                                text = "Send Email Confirmation Link",
                                onClick = {
                                    viewModel.submitEmailVerification(emailAddress)
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            // Technical Support Box
            item {
                Spacer(modifier = Modifier.height(6.dp))
                TechnicalSupportBox(onNotice = { viewModel.showNotice(it) })
            }
        }
    }
}
