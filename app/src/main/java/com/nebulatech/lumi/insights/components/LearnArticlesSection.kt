package com.nebulatech.lumi.insights.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nebulatech.lumi.ui.theme.LiterataFontFamily
import com.nebulatech.lumi.ui.theme.ManropeFontFamily
import com.nebulatech.lumi.ui.theme.Primary

@Composable
fun InsightsLumiBannerCard(
    text: String = "We noticed your 'High Energy' days consistently align with your Follicular phase. This is a great time for creative projects or intense workouts.",
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Primary),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF8E5572),
                            Primary,
                            Color(0xFF5B3047)
                        )
                    )
                )
                .padding(22.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.AutoAwesome,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "LUMI INSIGHT",
                        fontFamily = ManropeFontFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = Color(0xFFF3DDE7)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = text,
                    fontFamily = ManropeFontFamily,
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    color = Color.White
                )
            }
        }
    }
}

data class LearnArticleItem(
    val category: String,
    val title: String,
    val readTimeMinutes: Int,
    val gradientColors: List<Color>
)

@Composable
fun LearnArticlesSection(
    articles: List<LearnArticleItem> = listOf(
        LearnArticleItem(
            category = "Sleep & Cycle",
            title = "Why Estrogen Impacts Sleep",
            readTimeMinutes = 4,
            gradientColors = listOf(Color(0xFFF3D7DF), Color(0xFFE9C5D0))
        ),
        LearnArticleItem(
            category = "Nutrition",
            title = "The Science of Luteal Cravings",
            readTimeMinutes = 3,
            gradientColors = listOf(Color(0xFFFDE4C3), Color(0xFFF5CD8B))
        )
    ),
    onArticleClick: (LearnArticleItem) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "LEARN",
            fontFamily = ManropeFontFamily,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = Color(0xFF6E5E67),
            modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            articles.forEach { article ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(96.dp)
                        .clickable { onArticleClick(article) },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left Thumbnail Box with smooth gradient wave effect
                        Box(
                            modifier = Modifier
                                .width(96.dp)
                                .fillMaxHeight()
                                .background(
                                    brush = Brush.linearGradient(colors = article.gradientColors)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.5f))
                            )
                        }

                        // Right Title & Metadata
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = article.category,
                                fontFamily = ManropeFontFamily,
                                fontSize = 12.sp,
                                color = Color(0xFF7A6A73)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = article.title,
                                fontFamily = LiterataFontFamily,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF26181F),
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.AccessTime,
                                    contentDescription = null,
                                    tint = Color(0xFF8A7A83),
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${article.readTimeMinutes} min read",
                                    fontFamily = ManropeFontFamily,
                                    fontSize = 11.sp,
                                    color = Color(0xFF8A7A83)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
