package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

/**
 * Dedicated Technical Support Box for Pink Passions
 * Finances & Management: Ruan Eksteen (+27 74 616 0891, ruan@pinkpassions.co.za)
 * Website & Mobile Support: Mark Van der Westhuizen (+27 62 656 6192, admin@pinkpassion.co.za)
 */
@Composable
fun TechnicalSupportBox(
    modifier: Modifier = Modifier,
    onNotice: ((String) -> Unit)? = null
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
        border = BorderStroke(1.dp, DarkBorderPink),
        modifier = modifier
            .fillMaxWidth()
            .testTag("technical_support_box")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Box Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(PinkGlow),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SupportAgent,
                        contentDescription = "Support",
                        tint = PinkPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "Technical & Management Support",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "Official South Africa Pink Passions Direct Helpdesk",
                        color = PinkLight,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 1. Finances & Management Card
            SupportContactItem(
                roleTitle = "Finances & Management",
                contactName = "Ruan Eksteen",
                whatsAppNumber = "+27 74 616 0891",
                whatsAppLink = "https://wa.me/27746160891?text=Hello%20Ruan,%20I%20am%20inquiring%20about%20Pink%20Passions%20Finances%20and%20Management.",
                emailAddress = "ruan@pinkpassions.co.za",
                roleColor = BadgeGold,
                onAction = { msg ->
                    onNotice?.invoke(msg) ?: Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 2. Website & Mobile Support Card
            SupportContactItem(
                roleTitle = "Website & Mobile Support",
                subRole = "Developer",
                contactName = "Mark Van der Westhuizen",
                whatsAppNumber = "+27 62 656 6192",
                whatsAppLink = "https://wa.me/27626566192?text=Hello%20Mark,%20I%20need%20assistance%20with%20the%20Pink%20Passions%20Mobile%20App%20or%20Website.",
                emailAddress = "admin@pinkpassion.co.za",
                roleColor = Color(0xFF00D2FF),
                onAction = { msg ->
                    onNotice?.invoke(msg) ?: Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Response Time Note
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF101018))
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Support hours: Mon – Sun 08:00 – 22:00 SAST • Fast WhatsApp responses",
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
private fun SupportContactItem(
    roleTitle: String,
    subRole: String? = null,
    contactName: String,
    whatsAppNumber: String,
    whatsAppLink: String,
    emailAddress: String,
    roleColor: Color,
    onAction: (String) -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    Surface(
        color = Color(0xFF14141F),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(0.8.dp, DarkBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header / Role badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = roleColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(0.5.dp, roleColor)
                ) {
                    Text(
                        text = if (subRole != null) "$roleTitle • $subRole" else roleTitle,
                        color = roleColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = contactName,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // WhatsApp Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0C1F15))
                    .border(0.5.dp, Color(0xFF25D366).copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .clickable {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(whatsAppLink))
                            context.startActivity(intent)
                            onAction("Opening WhatsApp to $contactName ($whatsAppNumber)")
                        } catch (e: Exception) {
                            clipboardManager.setText(AnnotatedString(whatsAppNumber))
                            onAction("Copied $contactName WhatsApp: $whatsAppNumber")
                        }
                    }
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = "WhatsApp",
                        tint = Color(0xFF25D366),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "WhatsApp $whatsAppNumber",
                        color = Color(0xFF25D366),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }
                Icon(
                    imageVector = Icons.Default.ArrowOutward,
                    contentDescription = null,
                    tint = Color(0xFF25D366),
                    modifier = Modifier.size(14.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Email Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF131726))
                    .border(0.5.dp, Color(0xFF0079C1).copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .clickable {
                        try {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:$emailAddress")
                                putExtra(Intent.EXTRA_SUBJECT, "Pink Passions Support Request - $roleTitle")
                            }
                            context.startActivity(intent)
                            onAction("Opening Email client to $emailAddress")
                        } catch (e: Exception) {
                            clipboardManager.setText(AnnotatedString(emailAddress))
                            onAction("Copied $contactName Email: $emailAddress")
                        }
                    }
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email",
                        tint = Color(0xFF00D2FF),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Email: $emailAddress",
                        color = Color(0xFF00D2FF),
                        fontSize = 12.sp
                    )
                }
                Icon(
                    imageVector = Icons.Default.ArrowOutward,
                    contentDescription = null,
                    tint = Color(0xFF00D2FF),
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
