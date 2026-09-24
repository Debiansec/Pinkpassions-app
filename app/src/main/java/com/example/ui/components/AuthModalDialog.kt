package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun AuthModalDialog(
    isOpen: Boolean,
    initialMode: String = "REGISTER",
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    var mode by remember { mutableStateOf(initialMode) } // "REGISTER" or "SIGN_IN"
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
            border = BorderStroke(1.2.dp, PinkPrimary.copy(alpha = 0.7f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("auth_modal_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (mode == "REGISTER") "CREATE CREATOR ACCOUNT" else "SIGN IN TO PINK PASSIONS",
                        color = TextWhite,
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        letterSpacing = 0.5.sp
                    )

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Mode Tabs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { mode = "REGISTER" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (mode == "REGISTER") PinkPrimary else DarkSurfaceVariant
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            "REGISTER",
                            color = if (mode == "REGISTER") TextWhite else TextLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Button(
                        onClick = { mode = "SIGN_IN" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (mode == "SIGN_IN") PinkPrimary else DarkSurfaceVariant
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            "SIGN IN",
                            color = if (mode == "SIGN_IN") TextWhite else TextLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (mode == "REGISTER") {
                    Text(
                        text = "Register to create your escort profile, masseuse listing, or venue showcase. Receive inquiries directly via WhatsApp & Telegram.",
                        color = TextMuted,
                        fontSize = 11.5.sp,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Display Name / Stage Name") },
                        placeholder = { Text("e.g. Bella Ross") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PinkPrimary,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    placeholder = { Text("you@example.com") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PinkPrimary,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PinkPrimary,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                PinkGradientButton(
                    text = if (mode == "REGISTER") "Start Full 14-Step Registration" else "Sign In",
                    onClick = {
                        if (mode == "REGISTER") {
                            viewModel.updateRegistrationState {
                                it.copy(
                                    email = email.ifBlank { "member@pinkpassions.co.za" },
                                    accountDisplayName = name.ifBlank { "VIP Creator" },
                                    stageName = name.ifBlank { "VIP Creator" }
                                )
                            }
                            viewModel.startRegistrationFlow(initialStep = 1)
                        } else {
                            viewModel.showNotice("Welcome back! Signed in to Pink Passions.")
                            onDismiss()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                if (mode == "REGISTER") {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Strict Trust & Safety: Profiles undergo administrator moderation review before publication across South Africa.",
                        color = Color(0xFFFFD700),
                        fontSize = 10.5.sp,
                        lineHeight = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
