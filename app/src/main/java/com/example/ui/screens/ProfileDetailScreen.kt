package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileDetailScreen(
    profile: Profile,
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var showReportDialog by remember { mutableStateOf(false) }
    var showAddReviewDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 90.dp)
        ) {
            // TOP HERO IMAGE WITH BACK & FAVOURITE BUTTONS
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
                    .background(Color(0xFF161622))
            ) {
                AsyncImage(
                    model = profile.avatarUrl,
                    contentDescription = profile.displayName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Gradient Vignette
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0x99000000), Color.Transparent, Color(0xFF0A0A0F))
                            )
                        )
                )

                // Top navigation bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0x99000000))
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = TextWhite)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = { viewModel.toggleFavourite(profile) },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color(0x99000000))
                                .testTag("profile_fav_button")
                        ) {
                            Icon(
                                imageVector = if (profile.isFavourite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favourite",
                                tint = if (profile.isFavourite) PinkPrimary else TextWhite
                            )
                        }

                        IconButton(
                            onClick = { showReportDialog = true },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color(0x99000000))
                        ) {
                            Icon(imageVector = Icons.Default.Flag, contentDescription = "Report", tint = TextMuted)
                        }
                    }
                }

                // Badges row at image bottom
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OnlineIndicator(isOnline = profile.isOnline)
                    VerificationBadge(profile.verifiedLevel)
                    if (profile.membershipTier != MembershipTier.BASIC_FREE || profile.isGold) {
                        VerifiedPremiumBadge(
                            membershipTier = profile.membershipTier,
                            isGold = profile.isGold,
                            isSubscriptionActive = true,
                            compact = true
                        )
                    }
                    if (profile.isFeatured) FeaturedBadge()
                }
            }

            // PROFILE CONTENT BODY
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                // Name, Age & Price
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${profile.displayName}, ${profile.age}",
                            color = TextWhite,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = null,
                                tint = PinkPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${profile.area}, ${profile.city} (${profile.province})",
                                color = TextMuted,
                                fontSize = 13.sp
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = profile.priceText,
                            color = PinkPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        RatingBar(rating = profile.rating, reviewCount = profile.reviewCount)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // QUICK DISCREET ACTIONS ROW
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Message
                    DarkOutlineButton(
                        text = "Message",
                        icon = Icons.Default.ChatBubble,
                        onClick = {
                            viewModel.openConversation("c1")
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("profile_action_message")
                    )

                    // WhatsApp
                    PinkGradientButton(
                        text = "WhatsApp",
                        icon = Icons.Default.Phone,
                        onClick = {
                            viewModel.showNotice("Opening WhatsApp with ${profile.displayName} (${profile.whatsapp})")
                        },
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("profile_action_whatsapp")
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // ABOUT ME CARD
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = DarkBorder
                ) {
                    Column {
                        Text(
                            text = "About Me",
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = profile.description,
                            color = TextWhite.copy(alpha = 0.85f),
                            fontSize = 13.sp,
                            lineHeight = 19.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // SERVICES & EXPERIENCES
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = DarkBorder
                ) {
                    Column {
                        Text(
                            text = "Offered Services & Companionship",
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val serviceList = profile.services.split(", ")
                            serviceList.forEach { srv ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = DarkSurfaceVariant,
                                    border = BorderStroke(1.dp, DarkBorderPink.copy(alpha = 0.5f))
                                ) {
                                    Text(
                                        text = srv,
                                        color = PinkLight,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // VERIFICATION & SAFETY AUDIT DETAILS
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                    border = BorderStroke(1.dp, BadgeVerifiedGreen.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = null,
                                tint = BadgeVerifiedGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Verification & Identity Assurance",
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "This advertiser has passed 100% genuine photo authenticity and age verification audits in accordance with South African advertising safety regulations.",
                            color = TextMuted,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // STATS OVERVIEW
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = DarkSurfaceVariant,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("${profile.viewsCount}", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("Views", color = TextMuted, fontSize = 11.sp)
                        }
                    }
                    Surface(
                        color = DarkSurfaceVariant,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("${profile.contactClicks}", color = PinkPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("Contacted", color = TextMuted, fontSize = 11.sp)
                        }
                    }
                    Surface(
                        color = DarkSurfaceVariant,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("${profile.reviewCount}", color = Color(0xFFFFD700), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("Reviews", color = TextMuted, fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // REVIEWS SECTION
                val profileReviews by viewModel.getReviewsForProfile(profile.id).collectAsState(initial = emptyList())

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Client Reviews & Ratings",
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        FiveStarRatingBadge(rating = profile.rating, reviewCount = profileReviews.size)
                    }

                    TextButton(onClick = { showAddReviewDialog = true }) {
                        Text("+ Write Review", color = PinkPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (profileReviews.isEmpty()) {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "No reviews yet. Be the first verified client to leave a 5-star rating for ${profile.displayName}!",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        profileReviews.forEach { rev ->
                            ReviewCardItem(review = rev)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // TECHNICAL SUPPORT BOX
                TechnicalSupportBox(onNotice = { viewModel.showNotice(it) })
            }
        }
    }

    if (showAddReviewDialog) {
        var newRating by remember { mutableStateOf(5) }
        var reviewText by remember { mutableStateOf("") }
        var reviewerName by remember { mutableStateOf("Verified Client") }

        AlertDialog(
            onDismissRequest = { showAddReviewDialog = false },
            containerColor = DarkSurfaceElevated,
            title = {
                Text("Review for ${profile.displayName}", color = TextWhite, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Select Rating:", color = TextMuted, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        (1..5).forEach { star ->
                            IconButton(onClick = { newRating = star }) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "$star stars",
                                    tint = if (star <= newRating) Color(0xFFFFD700) else Color(0xFF444455),
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = reviewerName,
                        onValueChange = { reviewerName = it },
                        label = { Text("Your Display Name") },
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
                        value = reviewText,
                        onValueChange = { reviewText = it },
                        label = { Text("Your Experience & Feedback") },
                        minLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PinkPrimary,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (reviewText.isNotBlank()) {
                            viewModel.submitReview(profile.id, newRating, reviewText, reviewerName)
                            showAddReviewDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary)
                ) {
                    Text("Submit Review", color = TextWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddReviewDialog = false }) {
                    Text("Cancel", color = TextMuted)
                }
            }
        )
    }

    if (showReportDialog) {
        var reportReason by remember { mutableStateOf("Inaccurate details / photos") }
        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            containerColor = DarkSurfaceElevated,
            title = {
                Text("Report Listing", color = TextWhite, fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(
                        text = "Pink Passions takes platform integrity seriously. Please select reason for moderation review:",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    listOf(
                        "Inaccurate details / photos",
                        "Suspected fake profile",
                        "Unresponsive contact details",
                        "Violation of 18+ platform policy"
                    ).forEach { r ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { reportReason = r }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = reportReason == r,
                                onClick = { reportReason = r },
                                colors = RadioButtonDefaults.colors(selectedColor = PinkPrimary)
                            )
                            Text(r, color = TextWhite, fontSize = 13.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.showNotice("Report submitted. Our moderation team will investigate.")
                        showReportDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary)
                ) {
                    Text("Submit Report", color = TextWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { showReportDialog = false }) {
                    Text("Cancel", color = TextMuted)
                }
            }
        )
    }
}
