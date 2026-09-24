package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.Profile
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun LiveEntertainmentScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val profiles by viewModel.allProfiles.collectAsState()
    val liveModels = profiles.take(6)
    var selectedLiveModel by remember { mutableStateOf<Profile?>(null) }
    var tokenBalance by remember { mutableStateOf(500) }
    var activeTab by remember { mutableStateOf(0) } // 0: Webcams Gallery, 1: CamSpace Widget, 2: Live Chat Room, 3: VIP Models

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
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = TextWhite)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFF0055))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("LIVE WEBCAMS SHOWS", color = TextWhite, fontWeight = FontWeight.Black, fontSize = 15.sp)
                        }
                        Text("XCams & CamSpace Live Network", color = PinkPrimary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                }

                Surface(
                    color = Color(0xFF261224),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, PinkPrimary)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Token, contentDescription = "Tokens", tint = BadgeGold, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("$tokenBalance Tokens", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        // TAB SELECTOR
        TabRow(
            selectedTabIndex = activeTab,
            containerColor = DarkSurfaceVariant,
            contentColor = PinkPrimary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = activeTab == 0,
                onClick = { activeTab = 0 },
                text = { Text("Webcam Gallery", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                icon = { Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
            Tab(
                selected = activeTab == 1,
                onClick = { activeTab = 1 },
                text = { Text("CamSpace Live", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                icon = { Icon(Icons.Default.LiveTv, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
            Tab(
                selected = activeTab == 2,
                onClick = { activeTab = 2 },
                text = { Text("Live Chat Room", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                icon = { Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
            Tab(
                selected = activeTab == 3,
                onClick = { activeTab = 3 },
                text = { Text("VIP Models", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                icon = { Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
        }

        // MAIN CONTENT ACCORDING TO ACTIVE TAB
        when (activeTab) {
            0 -> {
                // WEBCAM GALLERY IFRAME EMBED
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Surface(
                            color = PinkGlow,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, PinkPrimary.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.FiberManualRecord, contentDescription = "Live", tint = Color(0xFFFF0055), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Free Live Webcam Streaming Showroom", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Interactive chat, private shows & multi-cam preview", color = TextMuted, fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    item {
                        LiveWebcamGalleryEmbed(
                            heightDp = 480
                        )
                    }

                    item {
                        // Quick Action Tips & Cam Rooms
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    activeTab = 2
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f).height(44.dp)
                            ) {
                                Icon(Icons.Default.Forum, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Join Live Chat Room", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = {
                                    tokenBalance += 100
                                    viewModel.showNotice("Purchased 100 Tip Tokens! Total: $tokenBalance")
                                },
                                border = BorderStroke(1.dp, BadgeGold),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f).height(44.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = BadgeGold, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Buy Tip Tokens", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            1 -> {
                // CAMSPACE LIVE WIDGET EMBED
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Text(
                            text = "CamSpace Live Widget Showcase",
                            color = TextWhite,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Streaming live models, performers and VIP stage cams",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }

                    item {
                        CamSpaceLiveWidgetEmbed(
                            heightDp = 380
                        )
                    }

                    item {
                        LiveWebcamGalleryEmbed(
                            heightDp = 380
                        )
                    }
                }
            }

            2 -> {
                // LIVE WEBCAMS CHAT ROOM
                val chatMessages by viewModel.activeChatRoomMessages.collectAsState()
                var inputMessage by remember { mutableStateOf("") }

                Column(modifier = Modifier.fillMaxSize()) {
                    // Chat header info
                    Surface(
                        color = DarkSurfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Videocam, contentDescription = null, tint = PinkPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Live Webcams Chat Room", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            Text("89 Users Online", color = Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Messages List
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(chatMessages) { msg ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = if (msg.isFromMe) Arrangement.End else Arrangement.Start
                            ) {
                                Column(
                                    horizontalAlignment = if (msg.isFromMe) Alignment.End else Alignment.Start,
                                    modifier = Modifier.widthIn(max = 280.dp)
                                ) {
                                    if (!msg.isFromMe) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(msg.senderName, color = if (msg.isModel) PinkLight else TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            if (msg.isModel) {
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Surface(color = PinkPrimary, shape = RoundedCornerShape(4.dp)) {
                                                    Text("MODEL", color = TextWhite, fontSize = 8.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                                }
                                            }
                                        }
                                    }

                                    Surface(
                                        color = if (msg.isFromMe) PinkPrimary else DarkSurfaceElevated,
                                        shape = RoundedCornerShape(12.dp),
                                        border = BorderStroke(1.dp, if (msg.isFromMe) PinkPrimary else DarkBorder)
                                    ) {
                                        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                                            if (msg.tipAmountZar != null) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(Icons.Default.Token, contentDescription = null, tint = BadgeGold, modifier = Modifier.size(14.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Tipped ${msg.tipAmountZar} Tokens!", color = BadgeGold, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                                }
                                                Spacer(modifier = Modifier.height(2.dp))
                                            }
                                            Text(msg.message, color = TextWhite, fontSize = 13.sp)
                                        }
                                    }

                                    Text(msg.timestamp, color = TextDark, fontSize = 9.sp, modifier = Modifier.padding(top = 2.dp))
                                }
                            }
                        }
                    }

                    // Tip buttons bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DarkSurfaceVariant)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(20, 50, 100, 200).forEach { tip ->
                            Surface(
                                onClick = {
                                    if (tokenBalance >= tip) {
                                        tokenBalance -= tip
                                        viewModel.sendChatRoomMessage("Sent a love tip of $tip Tokens to the live models! 💖", tip)
                                    } else {
                                        viewModel.showNotice("Insufficient token balance. Please recharge.")
                                    }
                                },
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFF261224),
                                border = BorderStroke(0.8.dp, PinkPrimary)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(Icons.Default.Token, contentDescription = null, tint = BadgeGold, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("Tip $tip", color = TextWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Message input field
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
                            placeholder = { Text("Send live chat message...", color = TextDark, fontSize = 12.sp) },
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
                            modifier = Modifier.weight(1f)
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
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Send", tint = TextWhite, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            3 -> {
                // VIP PERFORMERS GRID
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Text("Featured Cam Performers", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }

                    val pairs = liveModels.chunked(2)
                    items(pairs) { rowModels ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            for (model in rowModels) {
                                Box(modifier = Modifier.weight(1f)) {
                                    ProfileCard(
                                        profile = model,
                                        onClick = { viewModel.openProfileDetail(model) },
                                        onFavouriteToggle = { viewModel.toggleFavourite(model) }
                                    )
                                }
                            }
                            if (rowModels.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}
