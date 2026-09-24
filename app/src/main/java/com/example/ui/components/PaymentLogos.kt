package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.PaymentMethod
import com.example.ui.theme.*

/**
 * Authentic PayPal Logo Badge
 */
@Composable
fun PayPalLogo(
    modifier: Modifier = Modifier,
    isDark: Boolean = true
) {
    Surface(
        color = if (isDark) Color(0xFF001C38) else Color(0xFFF2F7FD),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color(0xFF0079C1).copy(alpha = 0.6f)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Stylized double-P PayPal Monogram
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF003087)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "P",
                    color = Color(0xFF0079C1),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Row {
                Text(
                    text = "Pay",
                    color = Color(0xFF0079C1),
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    fontStyle = FontStyle.Italic
                )
                Text(
                    text = "Pal",
                    color = Color(0xFF00457C),
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    fontStyle = FontStyle.Italic
                )
            }
        }
    }
}

/**
 * Authentic PayFast South Africa Logo Badge
 */
@Composable
fun PayFastLogo(
    modifier: Modifier = Modifier,
    isDark: Boolean = true
) {
    Surface(
        color = if (isDark) Color(0xFF22080A) else Color(0xFFFFF0F0),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color(0xFFE31B23).copy(alpha = 0.6f)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // PayFast Red Square Motif
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFFE31B23)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Row {
                Text(
                    text = "Pay",
                    color = Color(0xFFE31B23),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp
                )
                Text(
                    text = "Fast",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                )
            }
        }
    }
}

/**
 * Trust & Security Badge: 256-Bit SSL Encryption & 3D Secure Protection
 */
@Composable
fun SecurePaymentsBadge(
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xFF0E1A14),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Color(0xFF00E676).copy(alpha = 0.4f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF00E676).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "Secure Payments",
                    tint = Color(0xFF00E676),
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "SECURE PAYMENTS",
                        color = Color(0xFF00E676),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• 256-Bit SSL Encrypted",
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }
                Text(
                    text = "Guaranteed fraud prevention with 3D-Secure & PCI-DSS compliance.",
                    color = TextWhite.copy(alpha = 0.8f),
                    fontSize = 10.sp
                )
            }
        }
    }
}

/**
 * Payment Gateway Selection Strip with PayPal & PayFast Logos
 */
@Composable
fun PaymentGatewaySelector(
    selectedMethod: PaymentMethod,
    onSelect: (PaymentMethod) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Payment Gateways & Methods",
            color = PinkLight,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )

        // PayPal Card
        Card(
            onClick = { onSelect(PaymentMethod.PayPal) },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (selectedMethod == PaymentMethod.PayPal) Color(0xFF0A1B2E) else DarkSurfaceVariant
            ),
            border = BorderStroke(
                1.5.dp,
                if (selectedMethod == PaymentMethod.PayPal) Color(0xFF0079C1) else DarkBorder
            ),
            modifier = Modifier.fillMaxWidth().testTag("payment_option_paypal")
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedMethod == PaymentMethod.PayPal,
                    onClick = { onSelect(PaymentMethod.PayPal) },
                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF0079C1))
                )
                Spacer(modifier = Modifier.width(6.dp))
                PayPalLogo()
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "PayPal Secure Checkout",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Global cards, balances, or international bank payments",
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }
            }
        }

        // PayFast Card
        Card(
            onClick = { onSelect(PaymentMethod.PayFast) },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (selectedMethod == PaymentMethod.PayFast) Color(0xFF260C10) else DarkSurfaceVariant
            ),
            border = BorderStroke(
                1.5.dp,
                if (selectedMethod == PaymentMethod.PayFast) Color(0xFFE31B23) else DarkBorder
            ),
            modifier = Modifier.fillMaxWidth().testTag("payment_option_payfast")
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedMethod == PaymentMethod.PayFast,
                    onClick = { onSelect(PaymentMethod.PayFast) },
                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFE31B23))
                )
                Spacer(modifier = Modifier.width(6.dp))
                PayFastLogo()
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "PayFast (South Africa)",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Visa, Mastercard, Capitec Pay, Instant EFT & Debit Card",
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }
            }
        }

        // Ozow Instant EFT Card
        Card(
            onClick = { onSelect(PaymentMethod.Ozow) },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (selectedMethod == PaymentMethod.Ozow) DarkSurfaceElevated else DarkSurfaceVariant
            ),
            border = BorderStroke(
                1.dp,
                if (selectedMethod == PaymentMethod.Ozow) PinkPrimary else DarkBorder
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedMethod == PaymentMethod.Ozow,
                    onClick = { onSelect(PaymentMethod.Ozow) },
                    colors = RadioButtonDefaults.colors(selectedColor = PinkPrimary)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Ozow Instant Bank EFT",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Capitec, FNB, ABSA, Nedbank, Standard Bank, Investec",
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }
            }
        }

        // Trust badge
        SecurePaymentsBadge()
    }
}

/**
 * Interactive Payment Processing Dialog with PayPal/PayFast Handshake
 */
@Composable
fun PaymentGatewayDialog(
    amountZar: Int,
    itemTitle: String,
    selectedMethod: PaymentMethod,
    isProcessing: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = { if (!isProcessing) onDismiss() }) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
            border = BorderStroke(1.dp, if (selectedMethod == PaymentMethod.PayPal) Color(0xFF0079C1) else DarkBorderPink),
            modifier = Modifier.fillMaxWidth().testTag("payment_gateway_dialog")
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Gateway Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (selectedMethod == PaymentMethod.PayPal) {
                        PayPalLogo()
                    } else if (selectedMethod == PaymentMethod.PayFast) {
                        PayFastLogo()
                    } else {
                        Text("Secure Payment", color = TextWhite, fontWeight = FontWeight.Bold)
                    }

                    Surface(
                        color = Color(0xFF0E1A14),
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(0.5.dp, Color(0xFF00E676))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(10.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("SSL 256-BIT", color = Color(0xFF00E676), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = itemTitle,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Text(
                    text = "R$amountZar ZAR",
                    color = PinkPrimary,
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp
                )

                if (selectedMethod == PaymentMethod.PayPal) {
                    val usdEst = (amountZar / 18.5).toInt()
                    Text(
                        text = "≈ $$usdEst USD at standard PayPal exchange rate",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                SecurePaymentsBadge()

                Spacer(modifier = Modifier.height(20.dp))

                if (isProcessing) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 12.dp)
                    ) {
                        CircularProgressIndicator(
                            color = if (selectedMethod == PaymentMethod.PayPal) Color(0xFF0079C1) else PinkPrimary,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (selectedMethod == PaymentMethod.PayPal) "Connecting to PayPal Gateway..." else "Connecting to PayFast Gateway...",
                            color = TextWhite,
                            fontSize = 12.sp
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, DarkBorder)
                        ) {
                            Text("Cancel", color = TextMuted)
                        }

                        Button(
                            onClick = onConfirm,
                            modifier = Modifier.weight(1f).testTag("confirm_gateway_payment_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedMethod == PaymentMethod.PayPal) Color(0xFF0079C1) else PinkPrimary
                            )
                        ) {
                            Text(
                                text = if (selectedMethod == PaymentMethod.PayPal) "Pay with PayPal" else "Pay with PayFast",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
