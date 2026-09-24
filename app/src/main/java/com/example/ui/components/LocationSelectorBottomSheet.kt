package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MockDataProvider
import com.example.ui.theme.*

/**
 * Bottom Sheet for selecting South African cities and provinces with search.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSelectorBottomSheet(
    selectedCity: String?,
    selectedProvince: String?,
    onLocationSelected: (province: String?, city: String?) -> Unit,
    onDismissRequest: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedProvinceTab by remember { mutableStateOf<String?>(null) }

    val popularCities = listOf(
        Pair("Gauteng", "Sandton"),
        Pair("Western Cape", "Cape Town"),
        Pair("Western Cape", "Camps Bay"),
        Pair("Gauteng", "Johannesburg"),
        Pair("KwaZulu-Natal", "Durban"),
        Pair("KwaZulu-Natal", "Umhlanga"),
        Pair("Gauteng", "Pretoria"),
        Pair("Gauteng", "Rosebank")
    )

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = DarkSurfaceElevated,
        scrimColor = Color.Black.copy(alpha = 0.75f),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = {
            BottomSheetDefaults.DragHandle(color = DarkBorderPink)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Select Location",
                        color = TextWhite,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Filter verified providers across South Africa",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }

                // Reset / All SA Button
                TextButton(
                    onClick = {
                        onLocationSelected(null, null)
                        onDismissRequest()
                    }
                ) {
                    Text("All South Africa", color = PinkPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Search Box for Cities
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search city or area (e.g. Sandton, Camps Bay)...", color = TextDark, fontSize = 13.sp) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = PinkPrimary)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = DarkSurfaceVariant,
                    unfocusedContainerColor = DarkSurfaceVariant,
                    focusedBorderColor = PinkPrimary,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("location_search_input")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Popular Hotspots Quick Pills
            Text(
                text = "POPULAR HOTSPOTS",
                color = PinkLight,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(popularCities) { (prov, city) ->
                    val isSelected = selectedCity.equals(city, ignoreCase = true)
                    Surface(
                        onClick = {
                            onLocationSelected(prov, city)
                            onDismissRequest()
                        },
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) PinkPrimary else DarkSurfaceVariant,
                        border = BorderStroke(1.dp, if (isSelected) PinkPrimary else DarkBorder)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = null,
                                tint = if (isSelected) TextWhite else PinkPrimary,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = city,
                                color = if (isSelected) TextWhite else TextWhite,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Province selector horizontal chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedProvinceTab == null,
                        onClick = { selectedProvinceTab = null },
                        label = { Text("All Provinces", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PinkPrimary,
                            selectedLabelColor = TextWhite,
                            containerColor = DarkSurfaceVariant,
                            labelColor = TextMuted
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = DarkBorder,
                            selectedBorderColor = PinkPrimary,
                            enabled = true,
                            selected = selectedProvinceTab == null
                        )
                    )
                }
                items(MockDataProvider.PROVINCES) { province ->
                    val isSelected = selectedProvinceTab == province
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedProvinceTab = if (isSelected) null else province
                        },
                        label = { Text(province, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PinkPrimary,
                            selectedLabelColor = TextWhite,
                            containerColor = DarkSurfaceVariant,
                            labelColor = TextMuted
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = DarkBorder,
                            selectedBorderColor = PinkPrimary,
                            enabled = true,
                            selected = isSelected
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Filtered City List
            val provincesToShow = if (selectedProvinceTab != null) {
                listOf(selectedProvinceTab!!)
            } else {
                MockDataProvider.PROVINCES
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 280.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                provincesToShow.forEach { province ->
                    val allCitiesInProvince = MockDataProvider.CITIES_BY_PROVINCE[province] ?: emptyList()
                    val filteredCities = if (searchQuery.isNotBlank()) {
                        allCitiesInProvince.filter { it.lowercase().contains(searchQuery.lowercase().trim()) }
                    } else {
                        allCitiesInProvince
                    }

                    if (filteredCities.isNotEmpty()) {
                        item {
                            Surface(
                                color = DarkSurfaceVariant.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = province.uppercase(),
                                        color = PinkLight,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.sp,
                                        letterSpacing = 0.5.sp
                                    )
                                    Text(
                                        text = "Select Province",
                                        color = PinkPrimary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.clickable {
                                            onLocationSelected(province, null)
                                            onDismissRequest()
                                        }
                                    )
                                }
                            }
                        }

                        items(filteredCities) { city ->
                            val isSelected = selectedCity.equals(city, ignoreCase = true)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) PinkGlow else Color.Transparent)
                                    .clickable {
                                        onLocationSelected(province, city)
                                        onDismissRequest()
                                    }
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.LocationCity,
                                        contentDescription = null,
                                        tint = if (isSelected) PinkPrimary else TextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = city,
                                        color = if (isSelected) PinkPrimary else TextWhite,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 14.sp
                                    )
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = PinkPrimary,
                                        modifier = Modifier.size(18.dp)
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
