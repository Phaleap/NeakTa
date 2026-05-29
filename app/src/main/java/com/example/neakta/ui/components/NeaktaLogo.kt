package com.example.neakta.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.neakta.R
import com.example.neakta.ui.auth.Cinzel

@Composable
fun NeaktaLogo(
    modifier: Modifier = Modifier,
    size: Dp = 44.dp
) {
    Image(
        painter = painterResource(id = R.drawable.neakta_logo),
        contentDescription = "NeakTa logo",
        modifier = modifier.size(size)
    )
}

@Composable
fun NeaktaBrandMark(
    modifier: Modifier = Modifier,
    logoSize: Dp = 44.dp,
    textColor: Color,
    fontSize: TextUnit = 24.sp,
    letterSpacing: TextUnit = 5.sp
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        NeaktaLogo(size = logoSize)
        Text(
            text = "NEAKTA",
            style = TextStyle(
                fontFamily = Cinzel,
                fontSize = fontSize,
                letterSpacing = letterSpacing,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        )
    }
}

@Composable
fun NeaktaVerticalBrandMark(
    modifier: Modifier = Modifier,
    logoSize: Dp = 96.dp,
    textColor: Color,
    fontSize: TextUnit = 42.sp,
    letterSpacing: TextUnit = 10.sp
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        NeaktaLogo(size = logoSize)
        Text(
            text = "NEAKTA",
            style = TextStyle(
                fontFamily = Cinzel,
                fontSize = fontSize,
                letterSpacing = letterSpacing,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        )
    }
}
