package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*

/**
 * On-brand skeleton shimmer brush matching the dark luxury hot-pink aesthetic.
 */
@Composable
fun rememberPinkShimmerBrush(): Brush {
    val shimmerColors = listOf(
        Color(0xFF161622),
        Color(0xFF281424),
        Color(0xFF38142E),
        Color(0xFF281424),
        Color(0xFF161622)
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    return Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim, y = translateAnim)
    )
}

@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(8.dp)
) {
    val brush = rememberPinkShimmerBrush()
    Box(
        modifier = modifier
            .clip(shape)
            .background(brush)
    )
}

@Composable
fun SkeletonProfileCard(modifier: Modifier = Modifier) {
    val brush = rememberPinkShimmerBrush()
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(DarkSurfaceVariant)
            .border(1.dp, DarkBorder, RoundedCornerShape(18.dp))
    ) {
        Column {
            // Image Placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                    .background(brush)
            )

            Column(modifier = Modifier.padding(12.dp)) {
                // Name & rating row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(modifier = Modifier.width(100.dp).height(16.dp).clip(RoundedCornerShape(4.dp)).background(brush))
                    Box(modifier = Modifier.width(40.dp).height(16.dp).clip(RoundedCornerShape(4.dp)).background(brush))
                }

                Spacer(modifier = Modifier.height(8.dp))
                // Subtitle
                Box(modifier = Modifier.width(140.dp).height(12.dp).clip(RoundedCornerShape(4.dp)).background(brush))

                Spacer(modifier = Modifier.height(10.dp))
                // Price & badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(modifier = Modifier.width(70.dp).height(16.dp).clip(RoundedCornerShape(4.dp)).background(brush))
                    Box(modifier = Modifier.width(50.dp).height(14.dp).clip(RoundedCornerShape(4.dp)).background(brush))
                }
            }
        }
    }
}

@Composable
fun SkeletonFeaturedCarousel(modifier: Modifier = Modifier) {
    val brush = rememberPinkShimmerBrush()
    Column(modifier = modifier.padding(vertical = 12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(modifier = Modifier.width(160.dp).height(18.dp).clip(RoundedCornerShape(4.dp)).background(brush))
            Box(modifier = Modifier.width(60.dp).height(14.dp).clip(RoundedCornerShape(4.dp)).background(brush))
        }
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(3) {
                Box(
                    modifier = Modifier
                        .width(190.dp)
                        .height(240.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(DarkSurfaceVariant)
                        .border(1.dp, DarkBorder, RoundedCornerShape(18.dp))
                ) {
                    Column {
                        Box(modifier = Modifier.fillMaxWidth().height(160.dp).background(brush))
                        Column(modifier = Modifier.padding(10.dp)) {
                            Box(modifier = Modifier.width(110.dp).height(14.dp).clip(RoundedCornerShape(4.dp)).background(brush))
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(modifier = Modifier.width(80.dp).height(12.dp).clip(RoundedCornerShape(4.dp)).background(brush))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SkeletonFeedList(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        repeat(3) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SkeletonProfileCard(modifier = Modifier.weight(1f))
                SkeletonProfileCard(modifier = Modifier.weight(1f))
            }
        }
    }
}
