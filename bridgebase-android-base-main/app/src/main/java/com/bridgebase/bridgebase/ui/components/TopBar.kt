package com.bridgebase.bridgebase.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bridgebase.bridgebase.R
import com.bridgebase.bridgebase.utils.AppBrand

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    height: Dp = 76.dp   // reduced for premium feel
) {
    val statusInsets = WindowInsets.statusBars.asPaddingValues()

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .background(AppBrand.primary) // new premium blue
            .padding(top = statusInsets.calculateTopPadding())
            .padding(bottom = 6.dp)
            .height(height)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start // centered looked off — start is cleaner
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Company Logo",
                modifier = Modifier.size(34.dp) // more refined
            )

            Spacer(Modifier.width(8.dp))

            AutoFitTextOneLine(
                text = AppBrand.APPNAME,
                modifier = Modifier.weight(1f),
                maxFontSize = 30.sp,
                minFontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppBrand.onPrimaryLight
            )
        }
    }
}
