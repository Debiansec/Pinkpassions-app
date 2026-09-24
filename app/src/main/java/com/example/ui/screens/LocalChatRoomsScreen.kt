package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.MockDataProvider
import com.example.data.PaymentMethod
import com.example.model.ChatRoom
import com.example.model.MembershipTier
import com.example.model.VerificationLevel
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun LocalChatRoomsScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val chatRooms by viewModel.chatRooms.collectAsState()
    val activeRoom = viewModel.selectedChatRoom.collectAsState().value ?: chatRooms.firstOrNull()
    val messages by viewModel.activeChatRoomMessages.collectAsState()
    val currentRoomId by viewModel.activeChatRoomId.collectAsState()
    var inputMessage by remember { mutableStateOf("") }
    var showRoomListDrawer by remember { mutableStateOf(false) }
    var selectedCategoryFilter by remember { mutableStateOf("All Rooms") }
    var showFwbSubscriptionDialog by remember { mutableStateOf(false) }
    var showFwbHeroBanner by remember { mutableStateOf(true) }

    val currentRoom = chatRooms.find { it.id == currentRoomId } ?: chatRooms.firstOrNull()
    val roomMembers = remember(currentRoomId) {
        MockDataProvider.getChatRoomUsers(currentRoomId)
    }

    val roomCategories = listOf(
        "All Rooms",
        "📍 Local Hookups",
        "🥂 Lifestyle Events",
        "💬 General Discussion"
    )

    val filteredRooms = remember(chatRooms, selectedCategoryFilter) {
        when (selectedCategoryFilter) {
            "📍 Local Hookups" -> chatRooms.filter {
                it.category.equals("Local Hookups", ignoreCase = true) ||
                it.id.contains("hookup", ignoreCase = true) ||
                it.id.contains("fwb", ignoreCase = true) ||
                it.name.contains("Hookup", ignoreCase = true) ||
                it.name.contains("Benefits", ignoreCase = true) ||
                (it.city != "South Africa" && !it.isWebcamLounge && it.id != "room_vip_events")
            }
            "🥂 Lifestyle Events" -> chatRooms.filter {
                it.category.equals("Lifestyle Events", ignoreCase = true) ||
                it.id.contains("swinger", ignoreCase = true) ||
                it.id.contains("spa", ignoreCase = true) ||
                it.id.contains("webcam", ignoreCase = true) ||
                it.id.contains("vip_events", ignoreCase = true) ||
                it.name.contains("Lifestyle", ignoreCase = true) ||
                it.name.contains("Swingers", ignoreCase = true) ||
                it.name.contains("Yacht", ignoreCase = true)
            }
            "💬 General Discussion" -> chatRooms.filter {
                it.category.equals("General Discussion", ignoreCase = true) ||
                it.id.contains("general", ignoreCase = true) ||
                it.id.contains("tips", ignoreCase = true) ||
                it.id.contains("dating_tips", ignoreCase = true) ||
                it.id.contains("relationship", ignoreCase = true) ||
                it.name.contains("General", ignoreCase = true) ||
                it.name.contains("Advice", ignoreCase = true) ||
                it.name.contains("Safe Dating", ignoreCase = true)
            }
            else -> chatRooms
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // TOP APP BAR
        Surface(
            color = DarkSurfaceElevated,
            border = BorderStroke(1.dp, DarkBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("chat_rooms_back_button")) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = TextWhite)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(currentRoom?.iconEmoji ?: "🍸", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = currentRoom?.name ?: "Local Chat Room",
                                color = TextWhite,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp
                            )
                        }
                        Text(
                            text = "${currentRoom?.city ?: "South Africa"} • ${currentRoom?.activeUsersCount ?: 35} online",
                            color = Color(0xFF00E676),
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Google Maps 50km distance viewer action button
                    IconButton(
                        onClick = { viewModel.currentTab.value = "map" },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4285F4).copy(alpha = 0.2f))
                            .border(1.dp, Color(0xFF4285F4), CircleShape)
                            .testTag("chat_open_google_maps_radar_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Radar,
                            contentDescription = "50km Radar Map",
                            tint = Color(0xFF4285F4),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Switch Room Button
                    Button(
                        onClick = { showRoomListDrawer = !showRoomListDrawer },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                        border = BorderStroke(1.dp, PinkPrimary.copy(alpha = 0.6f)),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("chat_rooms_toggle_drawer_button")
                    ) {
                        Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = PinkPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Rooms", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // FRIENDS WITH BENEFITS & LOCAL HOOKUPS INTRODUCTORY HERO BANNER
        AnimatedVisibility(visible = showFwbHeroBanner) {
            Surface(
                color = Color(0xFF1E0A1A),
                border = BorderStroke(1.dp, PinkPrimary.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth().testTag("fwb_chat_hero_banner")
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🔥", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "FRIEND'S WITH BENEFITS • ADULT DATING CHAT",
                                color = PinkLight,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.5.sp,
                                letterSpacing = 0.5.sp
                            )
                        }

                        IconButton(
                            onClick = { showFwbHeroBanner = false },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close banner", tint = TextMuted, modifier = Modifier.size(14.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Are you looking for casual sex or local hook ups in South Africa? Have you been searching local hookup dating sites but haven’t found any adult dating action near you? Friend's with Benefits is a South African adult dating chat room for those seeking friends with benefits, casual sex, or casual relationships.",
                        color = TextWhite,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 50km Radar Google Maps Button
                        Button(
                            onClick = { viewModel.currentTab.value = "map" },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4285F4)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.weight(1f).height(34.dp).testTag("fwb_open_50km_map_btn")
                        ) {
                            Icon(Icons.Default.NearMe, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("50km Maps Radar", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        // R99/m VIP Pass Subscription Button
                        Button(
                            onClick = { showFwbSubscriptionDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.weight(1f).height(34.dp).testTag("fwb_subscribe_r99_btn")
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("VIP Pass R99/mo", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // EXPANDABLE ROOM SWITCHER DRAWER WITH CATEGORY FILTERS
        AnimatedVisibility(visible = showRoomListDrawer) {
            Surface(
                color = DarkSurfaceVariant,
                border = BorderStroke(1.dp, PinkPrimary.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth().testTag("chat_rooms_drawer")
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Select Chat Room & Distance Radar:", color = PinkLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))

                    // Category filter pills
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(roomCategories) { cat ->
                            val isSelected = selectedCategoryFilter == cat
                            Surface(
                                onClick = { selectedCategoryFilter = cat },
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) PinkPrimary else DarkSurfaceElevated,
                                border = BorderStroke(0.8.dp, if (isSelected) PinkPrimary else DarkBorder)
                            ) {
                                Text(
                                    text = cat,
                                    color = if (isSelected) TextWhite else TextMuted,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        modifier = Modifier.heightIn(max = 220.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(filteredRooms) { room ->
                            val isSelected = room.id == currentRoomId
                            Surface(
                                onClick = {
                                    viewModel.selectChatRoom(room)
                                    showRoomListDrawer = false
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) PinkGlow else DarkSurfaceElevated,
                                border = BorderStroke(1.dp, if (isSelected) PinkPrimary else DarkBorder),
                                modifier = Modifier.fillMaxWidth().testTag("chat_room_item_${room.id}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(room.iconEmoji, fontSize = 18.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = room.name,
                                                    color = if (isSelected) PinkPrimary else TextWhite,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.5.sp
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Surface(
                                                    color = Color(0x33E31999),
                                                    shape = RoundedCornerShape(4.dp),
                                                    border = BorderStroke(0.5.dp, PinkPrimary.copy(alpha = 0.5f))
                                                ) {
                                                    Text(
                                                        text = room.category,
                                                        color = PinkLight,
                                                        fontSize = 8.5.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                    )
                                                }
                                            }
                                            Text(
                                                text = room.description,
                                                color = TextMuted,
                                                fontSize = 10.sp,
                                                maxLines = 1
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF00E676)))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("${room.activeUsersCount}", color = Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // ACTIVE ROOM USERS HORIZONTAL STRIP
        Surface(
            color = DarkSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Members Online:", color = TextDark, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                roomMembers.forEach { user ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkSurfaceElevated)
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        if (user.avatarUrl.isNotEmpty()) {
                            AsyncImage(
                                model = user.avatarUrl,
                                contentDescription = user.displayName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(16.dp).clip(CircleShape)
                            )
                        } else {
                            Box(
                                modifier = Modifier.size(16.dp).clip(CircleShape).background(PinkPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(user.displayName.take(1), color = TextWhite, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(user.displayName, color = TextWhite, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                        if (user.isModel) {
                            Spacer(modifier = Modifier.width(2.dp))
                            Icon(Icons.Default.Verified, contentDescription = null, tint = PinkPrimary, modifier = Modifier.size(11.dp))
                        }
                    }
                }
            }
        }

        // CHAT MESSAGES LIST
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages) { msg ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (msg.isFromMe) Arrangement.End else Arrangement.Start
                ) {
                    Column(
                        horizontalAlignment = if (msg.isFromMe) Alignment.End else Alignment.Start,
                        modifier = Modifier.widthIn(max = 290.dp)
                    ) {
                        if (!msg.isFromMe) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 2.dp)
                            ) {
                                Text(
                                    text = msg.senderName,
                                    color = if (msg.isModel) PinkLight else TextMuted,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (msg.isModel) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Surface(color = PinkPrimary, shape = RoundedCornerShape(4.dp)) {
                                        Text("VERIFIED 18+", color = TextWhite, fontSize = 8.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                    }
                                }
                            }
                        }

                        Surface(
                            color = if (msg.isFromMe) PinkPrimary else DarkSurfaceElevated,
                            shape = RoundedCornerShape(
                                topStart = 14.dp,
                                topEnd = 14.dp,
                                bottomStart = if (msg.isFromMe) 14.dp else 2.dp,
                                bottomEnd = if (msg.isFromMe) 2.dp else 14.dp
                            ),
                            border = BorderStroke(1.dp, if (msg.isFromMe) PinkPrimary else DarkBorder)
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                                if (msg.tipAmountZar != null) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Favorite, contentDescription = null, tint = BadgeGold, modifier = Modifier.size(13.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Tipped R${msg.tipAmountZar}!", color = BadgeGold, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                }
                                Text(
                                    text = msg.message,
                                    color = TextWhite,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        Text(
                            text = msg.timestamp,
                            color = TextDark,
                            fontSize = 9.sp,
                            modifier = Modifier.padding(horizontal = 2.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        // QUICK ACTION / EMOJI BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurfaceVariant)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf("🔥", "📍", "🍸", "💖", "🥂", "👋").forEach { emoji ->
                Surface(
                    onClick = {
                        viewModel.sendChatRoomMessage(emoji)
                    },
                    shape = RoundedCornerShape(12.dp),
                    color = DarkSurfaceElevated
                ) {
                    Text(emoji, fontSize = 14.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // 50km Radar Quick Button
            Surface(
                onClick = {
                    viewModel.currentTab.value = "map"
                },
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF152642),
                border = BorderStroke(0.8.dp, Color(0xFF4285F4))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF4285F4), modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("50km Map", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Tip button
            Surface(
                onClick = {
                    viewModel.sendChatRoomMessage("Sent a love tip of R50 to everyone in the room! 💖", tipAmount = 50)
                },
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF261224),
                border = BorderStroke(0.8.dp, PinkPrimary)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Favorite, contentDescription = null, tint = PinkPrimary, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("Tip R50", color = TextWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // INPUT MESSAGE BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurfaceElevated)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputMessage,
                onValueChange = { inputMessage = it },
                placeholder = { Text("Message ${currentRoom?.name ?: "room"}...", color = TextDark, fontSize = 13.sp) },
                shape = RoundedCornerShape(20.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = DarkSurfaceVariant,
                    unfocusedContainerColor = DarkSurfaceVariant,
                    focusedBorderColor = PinkPrimary,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite
                ),
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_room_input_field")
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (inputMessage.isNotBlank()) {
                        viewModel.sendChatRoomMessage(inputMessage)
                        inputMessage = ""
                    }
                },
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(PinkGradient)
                    .testTag("chat_room_send_button")
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send", tint = TextWhite, modifier = Modifier.size(18.dp))
            }
        }
    }

    // FRIENDS WITH BENEFITS VIP PASS SUBSCRIPTION DIALOG (R99 / MONTH)
    if (showFwbSubscriptionDialog) {
        var selectedPaymentMethod by remember { mutableStateOf<PaymentMethod>(PaymentMethod.PayFast) }
        var isSubscribing by remember { mutableStateOf(false) }
        var subscriptionSuccess by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showFwbSubscriptionDialog = false },
            containerColor = DarkSurfaceElevated,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PinkGlow),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = PinkPrimary, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Friend's with Benefits VIP", color = TextWhite, fontWeight = FontWeight.Black, fontSize = 16.sp)
                        Text("R99.00 / month • Cancel anytime", color = PinkLight, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            },
            text = {
                if (subscriptionSuccess) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Subscription Activated!", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "You now have full VIP access to all Friends with Benefits chat rooms, Google Maps 50km hookup radar, and unlimited private messaging.",
                            color = TextMuted,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            "Unlock full access to South Africa's most active adult dating & casual hookups network:",
                            color = TextMuted,
                            fontSize = 12.sp
                        )

                        listOf(
                            "🔥 Unlimited access to all FWB & Casual Hookup chat rooms",
                            "📍 Google Maps 50km distance viewer & local radar",
                            "💬 Send direct private messages to nearby singles & couples",
                            "🥂 Access to Swingers Club & private lifestyle parties",
                            "✨ 18+ Verified Member badge on your profile"
                        ).forEach { benefit ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = PinkPrimary, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(benefit, color = TextWhite, fontSize = 11.5.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Select South African Payment Method:", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)

                        listOf<Pair<PaymentMethod, String>>(
                            PaymentMethod.PayFast to "PayFast (Visa, Mastercard, Capitec Pay)",
                            PaymentMethod.Ozow to "Ozow Instant EFT (All SA Banks)",
                            PaymentMethod.Card to "Credit / Debit Card (3D Secure)",
                            PaymentMethod.PayPal to "PayPal / International Card",
                            PaymentMethod.ManualEFT to "Direct Bank Transfer (Manual EFT)"
                        ).forEach { (method, label) ->
                            val isSelected = selectedPaymentMethod == method
                            Surface(
                                onClick = { selectedPaymentMethod = method },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) PinkGlow else DarkSurfaceVariant,
                                border = BorderStroke(1.dp, if (isSelected) PinkPrimary else DarkBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { selectedPaymentMethod = method },
                                        colors = RadioButtonDefaults.colors(selectedColor = PinkPrimary)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(label, color = TextWhite, fontSize = 11.5.sp)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                if (subscriptionSuccess) {
                    Button(
                        onClick = { showFwbSubscriptionDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary)
                    ) {
                        Text("Start Chatting")
                    }
                } else {
                    Button(
                        onClick = {
                            isSubscribing = true
                            viewModel.selectedPaymentMethod.value = selectedPaymentMethod
                            viewModel.purchaseMembership(MembershipTier.FRIENDS_WITH_BENEFITS)
                            isSubscribing = false
                            subscriptionSuccess = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                        enabled = !isSubscribing,
                        modifier = Modifier.testTag("fwb_confirm_subscribe_r99_btn")
                    ) {
                        if (isSubscribing) {
                            CircularProgressIndicator(color = TextWhite, modifier = Modifier.size(16.dp))
                        } else {
                            Text("Subscribe for R99 / month", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            dismissButton = {
                if (!subscriptionSuccess) {
                    TextButton(onClick = { showFwbSubscriptionDialog = false }) {
                        Text("Cancel", color = TextMuted)
                    }
                }
            }
        )
    }
}

