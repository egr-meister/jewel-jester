package com.jeweljester.game.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jeweljester.game.audio.SoundManager
import com.jeweljester.game.data.Assets
import com.jeweljester.game.ui.theme.Gold
import com.jeweljester.game.ui.theme.JewelDeep
import com.jeweljester.game.ui.theme.JewelLight
import com.jeweljester.game.ui.theme.White

/** Полноэкранный фон + контент. */
@Composable
fun JewelBackground(content: @Composable BoxScope.() -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = Assets.bgMain),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        content()
    }
}

/** Кнопка: фон — плашка btn_plate, текст поверх. */
@Composable
fun JewelButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(enabled = enabled) { SoundManager.click(); onClick() },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = Assets.btnPlate),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
        Text(
            text = text.uppercase(),
            color = White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
    }
}

/** Кнопка главного меню: ornate-рамка btn_menu + белый текст по центру. */
@Composable
fun MenuButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .aspectRatio(1.55f)
            .clickable { SoundManager.click(); onClick() },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = Assets.btnMenu),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
        Text(
            text = text.uppercase(),
            color = White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 44.dp)
        )
    }
}

/**
 * Панель (пауза/результаты/правила). Рисуется в Compose (скруглённый
 * прямоугольник с золотой рамкой), поэтому корректно масштабируется под любой
 * контент и экран — без растяжения декоративной рамки-картинки.
 */
@Composable
fun JewelPanel(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.verticalGradient(listOf(JewelLight, JewelDeep)))
            .border(3.dp, Gold, RoundedCornerShape(24.dp))
            .padding(horizontal = 22.dp, vertical = 26.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp),
        content = content
    )
}

/** Верхняя панель: назад + заголовок + опциональная пауза (иконки-ассеты). */
@Composable
fun JewelTopBar(
    title: String,
    onBack: () -> Unit,
    onPause: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButtonImage(res = Assets.iconBack, description = "Back", onClick = onBack)
        Text(
            text = title.uppercase(),
            color = White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 22.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
        )
        if (onPause != null) {
            IconButtonImage(res = Assets.iconPause, description = "Pause", onClick = onPause)
        } else {
            Box(modifier = Modifier.size(48.dp))
        }
    }
}

@Composable
fun IconButtonImage(res: Int, description: String, onClick: () -> Unit) {
    Image(
        painter = painterResource(id = res),
        contentDescription = description,
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .clickable { SoundManager.click(); onClick() }
            .semantics { contentDescription = description }
    )
}

/** Полупрозрачная подложка для модальных панелей. */
@Composable
fun ModalScrim(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xCC160730))
            .padding(horizontal = 36.dp),
        contentAlignment = Alignment.Center
    ) { content() }
}
