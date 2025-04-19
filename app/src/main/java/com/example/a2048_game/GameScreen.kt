package com.example.a2048_game

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs

@Composable
fun Game2048Screen(
    board: Array<IntArray>, // the game board (4x4 grid)
    onSwipe: (Direction) -> Unit, // callback when user swipes
    isDarkTheme: Boolean // current theme mode
) {
    val gridColor = if (isDarkTheme) Color(0xFF1C1C1C) else Color(0xFFFAF8EF) // background color for grid box

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f) // square area for grid
            .padding(16.dp)
            .background(gridColor) // background of whole grid
            .pointerInput(Unit) {
                detectDragGestures { _, dragAmount ->
                    // determine swipe direction by drag vector
                    val (x, y) = dragAmount
                    if (abs(x) > abs(y)) {
                        if (x > 0) onSwipe(Direction.RIGHT) else onSwipe(Direction.LEFT)
                    } else {
                        if (y > 0) onSwipe(Direction.DOWN) else onSwipe(Direction.UP)
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for (row in board) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    for (cell in row) {
                        Tile(cell = cell, isDarkTheme = isDarkTheme) // draw each tile
                    }
                }
            }
        }
    }
}

@Composable
fun Tile(cell: Int, isDarkTheme: Boolean) {
    // choose text color based on value and theme
    val textColor = if (!isDarkTheme && cell <= 4) Color(0xFF776E65) else Color.White

    // animation scale for pop effect when tile appears
    val scale by animateFloatAsState(
        targetValue = if (cell == 0) 1f else 1.15f,
        animationSpec = tween(durationMillis = 120),
        label = "tile_scale"
    )

    // background color depending on tile value and theme
    val backgroundColor = when (cell) {
        0 -> if (isDarkTheme) Color(0xFF444444) else Color(0xFFCDC1B4)
        2 -> if (isDarkTheme) Color(0xFF665E57) else Color(0xFFEEE4DA)
        4 -> if (isDarkTheme) Color(0xFF7D7267) else Color(0xFFEDE0C8)
        8 -> if (isDarkTheme) Color(0xFFB26E4A) else Color(0xFFF2B179)
        16 -> if (isDarkTheme) Color(0xFFB55A3C) else Color(0xFFF59563)
        32 -> if (isDarkTheme) Color(0xFFB34737) else Color(0xFFF67C5F)
        64 -> if (isDarkTheme) Color(0xFFA03030) else Color(0xFFF65E3B)
        128 -> if (isDarkTheme) Color(0xFFB49C3E) else Color(0xFFEDCF72)
        256 -> if (isDarkTheme) Color(0xFFB89B2E) else Color(0xFFEDCC61)
        512 -> if (isDarkTheme) Color(0xFFB9991E) else Color(0xFFEDC850)
        1024 -> if (isDarkTheme) Color(0xFFBCA700) else Color(0xFFEDC53F)
        2048 -> if (isDarkTheme) Color(0xFFC6B800) else Color(0xFFEDC22E)
        else -> Color.Black // for tiles over 2048
    }

    Box(
        modifier = Modifier
            .size(90.dp) // tile size
            .padding(6.dp) // space between tiles
            .graphicsLayer(scaleX = scale, scaleY = scale) // apply animation
            .clip(RoundedCornerShape(12.dp)) // rounded corners
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (cell == 0) "" else cell.toString(), // don't show 0 tiles
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = textColor
        )
    }
}
