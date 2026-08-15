package com.nebulatech.lumi.profile.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.LiveHelp
import androidx.compose.material.icons.outlined.QuestionAnswer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nebulatech.lumi.ui.theme.LiterataFontFamily
import com.nebulatech.lumi.ui.theme.ManropeFontFamily
import com.nebulatech.lumi.ui.theme.Primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpCenterBottomSheet(
    onDismissRequest: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = Color(0xFFFBF9F7),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF7E6EE)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.HelpOutline,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column {
                    Text(
                        text = "Help Center",
                        fontFamily = LiterataFontFamily,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                    Text(
                        text = "Frequently asked questions and expert cycle support",
                        fontFamily = ManropeFontFamily,
                        fontSize = 12.sp,
                        color = Color(0xFF7A6A73)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // FAQ Accordion Section
            Text(
                text = "Frequently Asked Questions",
                fontFamily = LiterataFontFamily,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FaqAccordionItem(
                        question = "How does Lumi predict my period and fertile window?",
                        answer = "Lumi uses a biological cycle model combined with your personal historical data, basal body temperature shifts, and LH test logs. As you log consecutive cycles, predictions calibrate specifically to your body."
                    )

                    HorizontalDivider(color = Color(0xFFF2ECEF), thickness = 1.dp)

                    FaqAccordionItem(
                        question = "When is the best time to log BBT (Basal Body Temperature)?",
                        answer = "Take your temperature immediately upon waking, before getting out of bed or moving around. Consistent morning timing gives the cleanest thermal shift confirmation after ovulation."
                    )

                    HorizontalDivider(color = Color(0xFFF2ECEF), thickness = 1.dp)

                    FaqAccordionItem(
                        question = "Is my reproductive data shared or sold?",
                        answer = "Never. Lumi is built on a local-first privacy model with end-to-end encryption. Your cycle, mood, and intimate health logs are stored securely on your device."
                    )

                    HorizontalDivider(color = Color(0xFFF2ECEF), thickness = 1.dp)

                    FaqAccordionItem(
                        question = "How do I share my cycle data with my OB-GYN?",
                        answer = "Go to Profile → App Settings → Data Privacy & Export and tap 'Export Health Summary (CSV)'. This generates a standardized report you can email or print for medical appointments."
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Contact Support Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9EEF2)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.QuestionAnswer,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Need personal assistance?",
                            fontFamily = LiterataFontFamily,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Our wellness and technical support team is available to answer any questions or receive your feedback.",
                        fontFamily = ManropeFontFamily,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = Color(0xFF5E4E57)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { sendSupportEmail(context) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Email,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Contact Support Team",
                                fontFamily = ManropeFontFamily,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun FaqAccordionItem(
    question: String,
    answer: String
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = question,
                fontFamily = ManropeFontFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF26181F),
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.Outlined.ExpandMore,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(20.dp)
            )
        }

        if (isExpanded) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = answer,
                fontFamily = ManropeFontFamily,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = Color(0xFF6E5E67)
            )
        }
    }
}

private fun sendSupportEmail(context: Context) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:support@lumiapp.com")
        putExtra(Intent.EXTRA_SUBJECT, "Lumi App Support & Inquiry")
        putExtra(Intent.EXTRA_TEXT, "Hello Lumi Team,\n\nI have a question regarding:")
    }
    context.startActivity(Intent.createChooser(intent, "Contact Lumi Support"))
}
