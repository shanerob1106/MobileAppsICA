package com.example.sendit.helpers

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

// ExpandableText composable
@Composable
fun ExpandableText(text: String, modifier: Modifier = Modifier, fontSize: TextUnit = 14.sp) {
    var showMore by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .animateContentSize(animationSpec = tween(100))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { showMore = !showMore }
    ) {
        if (showMore) {
            Text(text = text, fontSize = fontSize)
        } else {
            Text(
                text = text,
                fontSize = fontSize,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}