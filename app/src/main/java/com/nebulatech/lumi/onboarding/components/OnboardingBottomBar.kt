package com.nebulatech.lumi.onboarding.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nebulatech.lumi.R
import com.nebulatech.lumi.ui.theme.ManropeFontFamily
import com.nebulatech.lumi.ui.theme.Primary

@Composable
fun OnboardingBottomBar(
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit,
    isContinueEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            onClick = onBackClick,
            shape = RoundedCornerShape(100),
            border = BorderStroke(1.dp, Color(0xFFD4C2C8)),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Primary
            ),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.back_button),
                fontFamily = ManropeFontFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Button(
            onClick = onContinueClick,
            enabled = isContinueEnabled,
            shape = RoundedCornerShape(100),
            colors = ButtonDefaults.buttonColors(
                containerColor = Primary,
                contentColor = Color.White,
                disabledContainerColor = Color(0xFFE4E2E0),
                disabledContentColor = Color(0xFF827379).copy(alpha = 0.6f)
            ),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
        ) {
            Text(
                text = stringResource(R.string.continue_button),
                fontFamily = ManropeFontFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
