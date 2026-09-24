package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.draw.scale
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
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class LiveCamChannel(
    val id: String,
    val name: String,
    val age: Int,
    val city: String,
    val title: String,
    val viewers: Int,
    val imageUrl: String,
    val tags: List<String>,
    val isVip: Boolean = false
)

val defaultCamChannels = listOf(
    LiveCamChannel(
        id = "cam_1",
        name = "Bella V.",
        age = 22,
        city = "Sandton, JHB",
        title = "Sensual Champagne & Velvet Chill VIP",
        viewers = 1482,
        imageUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=800&auto=format&fit=crop&q=80",
        tags = listOf("Sensual", "Private Cam", "1080p"),
        isVip = true
    ),
    LiveCamChannel(
        id = "cam_2",
        name = "Natasha K.",
        age = 24,
        city = "Sea Point, CPT",
        title = "Luxury Penthouse Suite & Lingerie Show",
        viewers = 2140,
        imageUrl = "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=800&auto=format&fit=crop&q=80",
        tags = listOf("Blonde", "4K Ultra", "Chat Now"),
        isVip = true
    ),
    LiveCamChannel(
        id = "cam_3",
        name = "Chloe M.",
        age = 21,
        city = "Umhlanga, DBN",
        title = "Late Night Beachside Lounge & Tease",
        viewers = 964,
        imageUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=800&auto=format&fit=crop&q=80",
        tags = listOf("Brunette", "Interactive", "Toys"),
        isVip = false
    ),
    LiveCamChannel(
        id = "cam_4",
        name = "Elena S.",
        age = 25,
        city = "Menlyn, PTA",
        title = "Exotic Glamour & Sensual Massage Stage",
        viewers = 1750,
        imageUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=800&auto=format&fit=crop&q=80",
        tags = listOf("Curvy", "VIP Lounge", "HD"),
        isVip = true
    )
)

/**
 * 100% Native Jetpack Compose Live Webcam Gallery Component.
 * Completely eliminates Chromium WebView instantiation and Mesa GPU renderer crashes
 * in containerized Android emulator environments while providing an ultra-responsive,
 * interactive live studio player with direct external browser launch support.
 */
@Composable
fun LiveWebcamGalleryEmbed(
    modifier: Modifier = Modifier,
    heightDp: Int = 460
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var selectedChannelIndex by remember { mutableIntStateOf(0) }
    var isPlaying by remember { mutableStateOf(true) }
    var isMuted by remember { mutableStateOf(true) }
    var heartReactions by remember { mutableIntStateOf(142) }
    var tipMessage by remember { mutableStateOf<String?>(null) }

    val currentChannel = defaultCamChannels[selectedChannelIndex.coerceIn(0, defaultCamChannels.lastIndex)]

    // Pulsing animation for the LIVE badge
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_transition")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(heightDp.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurfaceElevated)
            .border(1.dp, DarkBorderPink, RoundedCornerShape(16.dp))
            .testTag("live_webcam_gallery_embed_box")
    ) {
        // 1. LIVE VIDEO STREAM SURFACE
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { isPlaying = !isPlaying }
        ) {
            AsyncImage(
                model = currentChannel.imageUrl,
                contentDescription = "${currentChannel.name} Live Stream",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Cinematic Dark Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Black.copy(alpha = 0.55f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.85f)
                            )
                        )
                    )
            )

            // 2. TOP STREAM INFO BAR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .align(Alignment.TopStart),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // LIVE Pulsing Badge & Viewer Count
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Color(0xFFFF0055),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.testTag("badge_live_cam")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .scale(pulseScale)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "LIVE HD",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        color = Color.Black.copy(alpha = 0.65f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Icon(
                                Icons.Default.Visibility,
                                contentDescription = null,
                                tint = Color(0xFF00E676),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${currentChannel.viewers + (heartReactions % 5)}",
                                color = TextWhite,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Audio & External HD Stream Controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    IconButton(
                        onClick = { isMuted = !isMuted },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.65f))
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                            contentDescription = if (isMuted) "Unmute Audio" else "Mute Audio",
                            tint = if (isMuted) TextMuted else PinkPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Surface(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.xcams.com"))
                            try { context.startActivity(intent) } catch (_: Exception) {}
                        },
                        shape = RoundedCornerShape(8.dp),
                        color = PinkPrimary,
                        modifier = Modifier.testTag("button_open_browser_hd")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.OpenInBrowser,
                                contentDescription = null,
                                tint = TextWhite,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Full HD Web",
                                color = TextWhite,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // 3. CENTER PAUSE / PLAY OVERLAY (IF PAUSED)
            if (!isPlaying) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.45f)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(PinkPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = "Play Stream",
                            tint = TextWhite,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }

            // 4. TIP TOKENS NOTIFICATION POPUP
            AnimatedVisibility(
                visible = tipMessage != null,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically(),
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 24.dp)
            ) {
                Surface(
                    color = Color(0xFF261224),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, BadgeGold)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Stars, contentDescription = null, tint = BadgeGold, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = tipMessage ?: "",
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // 5. BOTTOM STREAM DETAILS & INTERACTIVE CONTROLS
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            ) {
                // Model Name & Tags
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${currentChannel.name}, ${currentChannel.age}",
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            if (currentChannel.isVip) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = BadgeGold,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "VIP",
                                        color = Color.Black,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = "${currentChannel.city} • ${currentChannel.title}",
                            color = PinkLight,
                            fontSize = 11.5.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Interactive Reaction & Tip Bar
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Heart Reaction Button
                        Surface(
                            onClick = { heartReactions++ },
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.65f),
                            border = BorderStroke(1.dp, PinkPrimary.copy(alpha = 0.5f))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    Icons.Default.Favorite,
                                    contentDescription = "Send Love",
                                    tint = Color(0xFFFF0055),
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "$heartReactions",
                                    color = TextWhite,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Quick Tip Button
                        Surface(
                            onClick = {
                                coroutineScope.launch {
                                    tipMessage = "Tipped 25 Tokens to ${currentChannel.name}! ❤️"
                                    delay(2200)
                                    tipMessage = null
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF2E1A33),
                            border = BorderStroke(1.dp, BadgeGold)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.Token, contentDescription = null, tint = BadgeGold, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("Tip 25", color = BadgeGold, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // MULTI-CHANNEL SWITCHER THUMBNAIL ROW
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(defaultCamChannels) { index, channel ->
                        val isSelected = index == selectedChannelIndex
                        Surface(
                            onClick = { selectedChannelIndex = index },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) PinkSubtle else DarkSurfaceVariant,
                            border = BorderStroke(1.5.dp, if (isSelected) PinkPrimary else Color.Transparent),
                            modifier = Modifier
                                .width(78.dp)
                                .testTag("cam_channel_switch_$index")
                        ) {
                            Row(
                                modifier = Modifier.padding(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = channel.imageUrl,
                                    contentDescription = channel.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Column {
                                    Text(
                                        text = channel.name,
                                        color = TextWhite,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "LIVE",
                                        color = if (isSelected) Color(0xFFFF0055) else TextMuted,
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * 100% Native Jetpack Compose CamSpace Live Widget Showcase.
 * Built entirely natively in Compose to provide an interactive, lightweight live broadcast
 * card without relying on Chromium or Mesa GPU rendering processes.
 */
@Composable
fun CamSpaceLiveWidgetEmbed(
    modifier: Modifier = Modifier,
    heightDp: Int = 320
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isPlaying by remember { mutableStateOf(true) }
    var currentModelIndex by remember { mutableIntStateOf(1) }
    var reactionCount by remember { mutableIntStateOf(218) }
    var noticeMessage by remember { mutableStateOf<String?>(null) }

    val model = defaultCamChannels[currentModelIndex.coerceIn(0, defaultCamChannels.lastIndex)]

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(heightDp.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurfaceElevated)
            .border(1.dp, Color(0xFF00A7E1).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .testTag("camspace_live_widget_embed_box")
    ) {
        AsyncImage(
            model = model.imageUrl,
            contentDescription = "CamSpace Live Stream",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Black.copy(alpha = 0.6f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.85f)
                        )
                    )
                )
        )

        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .align(Alignment.TopStart),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = Color(0xFF00A7E1),
                shape = RoundedCornerShape(6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Icon(Icons.Default.LiveTv, contentDescription = null, tint = TextWhite, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "CAMSPACE LIVE",
                        color = TextWhite,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Surface(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://camspacelive.com"))
                    try { context.startActivity(intent) } catch (_: Exception) {}
                },
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF0A2239),
                border = BorderStroke(1.dp, Color(0xFF00A7E1))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("Open CamSpace", color = Color(0xFF80D8FF), fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(3.dp))
                    Icon(Icons.Default.OpenInBrowser, contentDescription = null, tint = Color(0xFF80D8FF), modifier = Modifier.size(12.dp))
                }
            }
        }

        // Notification Banner
        AnimatedVisibility(
            visible = noticeMessage != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Surface(
                color = Color(0xFF071C2E),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, Color(0xFF00A7E1))
            ) {
                Text(
                    text = noticeMessage ?: "",
                    color = Color(0xFF80D8FF),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }
        }

        // Bottom Controls & Model Info
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${model.name} (${model.age})",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "${model.city} • 1080p Stage Feed",
                        color = Color(0xFF80D8FF),
                        fontSize = 11.sp
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        onClick = {
                            currentModelIndex = (currentModelIndex + 1) % defaultCamChannels.size
                        },
                        shape = RoundedCornerShape(8.dp),
                        color = DarkSurfaceVariant,
                        border = BorderStroke(1.dp, DarkBorder)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Shuffle, contentDescription = null, tint = TextWhite, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Next Cam", color = TextWhite, fontSize = 10.5.sp)
                        }
                    }

                    Surface(
                        onClick = {
                            reactionCount++
                            coroutineScope.launch {
                                noticeMessage = "Sent Rose to ${model.name}! 🌹"
                                delay(1800)
                                noticeMessage = null
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF00A7E1)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Favorite, contentDescription = null, tint = TextWhite, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("$reactionCount", color = TextWhite, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
