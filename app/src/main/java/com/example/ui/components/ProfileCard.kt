package com.example.ui.components

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.MembershipTier
import com.example.model.Profile
import com.example.model.VerificationLevel
import com.example.ui.theme.*

/**
 * Reusable, premium dark-themed ProfileCard component with Coil image loading,
 * verification badges, rating bar, online pulsing indicator, and quick actions.
 */
@Composable
fun ProfileCard(
    profile: Profile,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onFavouriteToggle: () -> Unit,
    showQuickActions: Boolean = true
) {
    val context = LocalContext.current

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
        border = BorderStroke(
            1.dp,
            when {
                profile.isGold -> BadgeGold.copy(alpha = 0.7f)
                profile.isFeatured -> PinkPrimary.copy(alpha = 0.7f)
                else -> DarkBorder
            }
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("profile_card_${profile.id}")
    ) {
        Column {
            // Image Container with Badges & Overlays
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(185.dp)
                    .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                    .background(Color(0xFF161622))
            ) {
                AsyncImage(
                    model = profile.avatarUrl,
                    contentDescription = "Profile photo of ${profile.displayName}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Dark atmospheric gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0x330A0A0F),
                                    Color(0xEE0A0A0F)
                                )
                            )
                        )
                )

                // Top Badge Bar: Online Status, Dynamic Verified/Premium Badge, and Favourite Heart Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        OnlineIndicator(isOnline = profile.isOnline)
                        VerifiedPremiumBadge(
                            membershipTier = profile.membershipTier,
                            verificationLevel = profile.verifiedLevel,
                            isGold = profile.isGold,
                            isFeatured = profile.isFeatured,
                            compact = true
                        )
                    }

                    IconButton(
                        onClick = onFavouriteToggle,
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color(0xAA0A0A0F))
                            .testTag("favourite_button_${profile.id}")
                    ) {
                        Icon(
                            imageVector = if (profile.isFavourite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (profile.isFavourite) "Remove from favourites" else "Add to favourites",
                            tint = if (profile.isFavourite) PinkPrimary else TextWhite,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }

                // Bottom Overlay: Secondary Verification/Featured Status & City Location
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (profile.isFeatured) {
                        FeaturedBadge()
                    } else if (profile.isSponsored) {
                        SponsoredBadge()
                    } else if (profile.verifiedLevel != VerificationLevel.NONE) {
                        VerifiedMemberBadge(compact = true)
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    Surface(
                        color = Color(0xCC13131D),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(0.5.dp, DarkBorderPink)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = null,
                                tint = PinkPrimary,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = profile.city,
                                color = TextWhite,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Profile Information Body
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${profile.displayName}, ${profile.age}",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.5.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    FiveStarRatingBadge(rating = profile.rating, reviewCount = profile.reviewCount)
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Dynamic Verified / Membership Tier Label Strip
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    VerifiedPremiumBadge(
                        membershipTier = profile.membershipTier,
                        verificationLevel = profile.verifiedLevel,
                        isGold = profile.isGold,
                        isFeatured = profile.isFeatured,
                        compact = true
                    )
                    Text(
                        text = "• ${profile.category.title}",
                        color = TextMuted,
                        fontSize = 10.5.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = "${profile.area}, ${profile.city}",
                    color = TextMuted,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = profile.priceText,
                        color = PinkPrimary,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp
                    )

                    Surface(
                        color = PinkGlow,
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(0.5.dp, PinkPrimary.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = "South Africa",
                            color = PinkPrimary,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                if (showQuickActions) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(color = DarkBorder, thickness = 0.8.dp)
                    Spacer(modifier = Modifier.height(8.dp))

                    // Quick Action Buttons (WhatsApp, Dial, Chat)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // WhatsApp Direct Button
                        Button(
                            onClick = {
                                try {
                                    val uri = Uri.parse("https://wa.me/27821234567?text=Hi%20${profile.displayName},%20I%20found%20your%20profile%20on%20Pink%20Passions%20SA.")
                                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                                } catch (e: Exception) {
                                    // Fallback
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF25D366).copy(alpha = 0.2f),
                                contentColor = Color(0xFF25D366)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(32.dp)
                                .border(0.8.dp, Color(0xFF25D366).copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        ) {
                            Icon(imageVector = Icons.Default.Chat, contentDescription = "WhatsApp", modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("WhatsApp", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        // View Full Profile Action
                        Button(
                            onClick = onClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PinkPrimary,
                                contentColor = TextWhite
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(32.dp)
                        ) {
                            Text("View Rates", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
