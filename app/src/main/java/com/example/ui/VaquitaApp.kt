package com.example.ui

import android.os.Build
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import kotlinx.coroutines.launch
import com.example.data.*
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

// Dynamic ARS Formatter
fun formatCurrency(amount: BigDecimal): String {
    return try {
        val format = java.text.NumberFormat.getIntegerInstance(Locale.forLanguageTag("es-AR"))
        "$" + format.format(amount)
    } catch (e: Exception) {
        "$" + amount.setScale(0, java.math.RoundingMode.HALF_UP).toPlainString()
    }
}

fun formatCurrency(amount: Double): String {
    return try {
        formatCurrency(BigDecimal(amount))
    } catch (e: Exception) {
        "$" + amount.toLong().toString()
    }
}

// Sparkle/Vibrant iOS Cosmic Glass Background
val VaquitaGlassGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF0F0C1B), // Midnight Abyss top
        Color(0xFF1E1E38), // Deep Cosmic Purple
        Color(0xFF2A1C4E), // Deep Luxurious Indigo/Malbec
        Color(0xFF090D1A)  // Dark slate bottom
    )
)

// Spring scaling bounce interaction modifier for silky-smooth iOS feeling
@Composable
fun Modifier.bounceScale(interactionSource: MutableInteractionSource): Modifier {
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "bounceScale"
    )
    return this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

// High-fidelity liquid glass panel modifier
fun Modifier.glassPanel(
    cornerRadius: Float = 60f,
    borderWidth: Float = 0.5f,
    elevation: Float = 6f
): Modifier {
    // We enforce 0.5dp crisp white highlight border for ALL panels to precisely unify the Liquid Glass aesthetic.
    val actualBorderWidth = 0.5f

    val base = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
        // Hardware-accelerated background blur using Modifier.blur() for real-time frosted glass.
        // We apply a subtle blur to mimic the refractive properties of thick glass.
        // NOTE: In production compose, true backdrop blur requires RenderNode tricks, 
        // but we use Native blur as requested for the liquid aesthetics.
        this.blur(radius = 0.1.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
    } else this

    return base.drawBehind {
        // Drop shadow for floating glass depth
        drawRoundRect(
            color = Color.Black.copy(alpha = 0.15f),
            topLeft = Offset(0f, elevation),
            size = this.size,
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius, cornerRadius)
        )

        // Frost translucent glass filling base
        drawRoundRect(
            color = Color.White.copy(alpha = 0.12f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius, cornerRadius)
        )

        // GLASS DEPTH: Diagonal Gloss linear reflection overlay (simulating curved glass specular reflection)
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.25f),
                    Color.White.copy(alpha = 0.05f),
                    Color.Transparent,
                    Color.White.copy(alpha = 0.08f)
                ),
                start = Offset(0f, 0f),
                end = Offset(size.width, size.height)
            ),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius, cornerRadius)
        )

        // SPECULAR EDGES: Thin (0.5dp), crisp white highlight border to the upper and left edges
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.35f),
                    Color.White.copy(alpha = 0.10f),
                    Color.Transparent,
                    Color.Transparent
                ),
                start = Offset(0f, 0f),
                end = Offset(size.width * 0.7f, size.height * 0.7f)
            ),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius, cornerRadius),
            style = Stroke(width = actualBorderWidth)
        )
    }
}

@Composable
fun CuteVaquitaDrawing(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        // 1. Ears
        drawOval(
            color = Color(0xFFE0E0E0),
            topLeft = Offset(width * 0.15f, height * 0.25f),
            size = Size(width * 0.2f, height * 0.15f)
        )
        drawOval(
            color = Color(0xFFE0E0E0),
            topLeft = Offset(width * 0.65f, height * 0.25f),
            size = Size(width * 0.2f, height * 0.15f)
        )
        // Inner ears (pink)
        drawOval(
            color = Color(0xFFFFB7B2),
            topLeft = Offset(width * 0.19f, height * 0.28f),
            size = Size(width * 0.12f, height * 0.09f)
        )
        drawOval(
            color = Color(0xFFFFB7B2),
            topLeft = Offset(width * 0.69f, height * 0.28f),
            size = Size(width * 0.12f, height * 0.09f)
        )

        // 2. Head (Main white oval)
        drawOval(
            color = Color.White,
            topLeft = Offset(width * 0.22f, height * 0.28f),
            size = Size(width * 0.56f, height * 0.52f)
        )

        // Cow spots on the head
        drawOval(
            color = Color(0xFF333333),
            topLeft = Offset(width * 0.25f, height * 0.32f),
            size = Size(width * 0.15f, height * 0.12f)
        )
        drawOval(
            color = Color(0xFF333333),
            topLeft = Offset(width * 0.58f, height * 0.45f),
            size = Size(width * 0.18f, height * 0.15f)
        )

        // 3. Eyes (Small black circles)
        drawCircle(
            color = Color(0xFF1E1E1E),
            radius = width * 0.035f,
            center = Offset(width * 0.40f, height * 0.48f)
        )
        drawCircle(
            color = Color(0xFF1E1E1E),
            radius = width * 0.035f,
            center = Offset(width * 0.60f, height * 0.48f)
        )

        // Eye highlights (white dots)
        drawCircle(
            color = Color.White,
            radius = width * 0.01f,
            center = Offset(width * 0.39f, height * 0.47f)
        )
        drawCircle(
            color = Color.White,
            radius = width * 0.01f,
            center = Offset(width * 0.59f, height * 0.47f)
        )

        // 4. Snout (Muzzle - horizontal pink oval)
        drawOval(
            color = Color(0xFFFFCCD5),
            topLeft = Offset(width * 0.28f, height * 0.58f),
            size = Size(width * 0.44f, height * 0.2f)
        )

        // Nostrils (Two small dark pink circles)
        drawCircle(
            color = Color(0xFFFF8FA3),
            radius = width * 0.018f,
            center = Offset(width * 0.44f, height * 0.68f)
        )
        drawCircle(
            color = Color(0xFFFF8FA3),
            radius = width * 0.018f,
            center = Offset(width * 0.56f, height * 0.68f)
        )

        // Smiling cheeks (Blush)
        drawCircle(
            color = Color(0xFFFF8FA3).copy(alpha = 0.4f),
            radius = width * 0.04f,
            center = Offset(width * 0.31f, height * 0.55f)
        )
        drawCircle(
            color = Color(0xFFFF8FA3).copy(alpha = 0.4f),
            radius = width * 0.04f,
            center = Offset(width * 0.69f, height * 0.55f)
        )
    }
}

@Composable
fun VaquitaApp(viewModel: JuntadaViewModel) {
    val selectedId by viewModel.selectedJuntadaId.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VaquitaGlassGradient)
    ) {
        AnimatedContent(
            targetState = selectedId,
            transitionSpec = {
                slideInHorizontally(
                    initialOffsetX = { width -> if (targetState != null) width else -width },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) + fadeIn(
                    animationSpec = spring(stiffness = Spring.StiffnessLow)
                ) togetherWith
                slideOutHorizontally(
                    targetOffsetX = { width -> if (targetState != null) -width else width },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) + fadeOut(
                    animationSpec = spring(stiffness = Spring.StiffnessLow)
                )
            },
            label = "screen_transition",
            modifier = Modifier.fillMaxSize()
        ) { id ->
            if (id == null) {
                HomeScreen(viewModel = viewModel)
            } else {
                DetailScreen(
                    viewModel = viewModel,
                    onBack = { viewModel.selectJuntada(null) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: JuntadaViewModel) {
    val jundadas by viewModel.allJuntadas.collectAsStateWithLifecycle()
    val friendGroups by viewModel.allFriendGroups.collectAsStateWithLifecycle()
    var showCreateDialog by remember { mutableStateOf(false) }
    var showGroupManager by remember { mutableStateOf(false) }
    val fabInteraction = remember { MutableInteractionSource() }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Vaquita 🐮",
                            fontWeight = FontWeight.Black,
                            fontSize = 22.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Box(
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Split de Gastos",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showGroupManager = true },
                        modifier = Modifier.testTag("group_manager_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Groups,
                            contentDescription = "Gestionar grupos",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            // iOS-like Bottom Action Panel
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .bounceScale(fabInteraction)
                        .glassPanel(cornerRadius = 100f, borderWidth = 0.5f, elevation = 20f)
                        .background(Color(0xFF0F172A).copy(alpha = 0.55f), RoundedCornerShape(100.dp))
                        .clickable(
                            interactionSource = fabInteraction,
                            indication = null,
                            onClick = { showCreateDialog = true }
                        )
                        .padding(4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(100.dp))
                            .padding(horizontal = 24.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Nueva Juntada",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Nueva Juntada", 
                            color = Color.White,
                            fontWeight = FontWeight.Black, 
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (jundadas.isEmpty()) {
                // Empty state with high visual depth contrast
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CuteVaquitaDrawing(
                        modifier = Modifier
                            .size(170.dp)
                            .background(Color.Transparent)
                    )
                    Spacer(modifier = Modifier.height(28.dp))
                    Text(
                        text = "¡Hacé una vaquita! 🐮",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Dividir los gastos del asado o la juntada con amigos ya no es un quilombo. Creá una juntada, agregá los participantes, poné cuánto gastó cada uno y listo.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.75f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Text(
                            text = "Tus Juntadas Recientes",
                            fontWeight = FontWeight.ExtraBold,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
                        )
                    }
                    items(jundadas, key = { it.id }) { juntada ->
                        JuntadaCard(
                            juntada = juntada,
                            onClick = { viewModel.selectJuntada(juntada.id) },
                            onDelete = { viewModel.deleteJuntada(juntada) }
                        )
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        JuntadaCreateDialog(
            friendGroups = friendGroups,
            onConfirm = { name, friends ->
                viewModel.createJuntada(name, friends)
                showCreateDialog = false
            },
            onDismiss = { showCreateDialog = false }
        )
    }

    if (showGroupManager) {
        GroupManagerDialog(
            viewModel = viewModel,
            onDismiss = { showGroupManager = false }
        )
    }
}

@Composable
fun JuntadaCard(
    juntada: Juntada,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .bounceScale(interactionSource)
            .glassPanel(cornerRadius = 60f, borderWidth = 2.5f, elevation = 6f)
            .clip(RoundedCornerShape(24.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = onClick
            )
            .testTag("juntada_item_${juntada.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = juntada.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(6.dp))
                val dateStr = remember(juntada.dateCreated) {
                    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    sdf.format(Date(juntada.dateCreated))
                }
                Text(
                    text = "Creado: $dateStr",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(10.dp))
                // Row of participants initials
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFFA5F3FC)
                    )
                    Text(
                        text = if (juntada.participants.isEmpty()) "Sin amigos agregados"
                               else if (juntada.participants.size == 1) "1 amigo integrado"
                               else "${juntada.participants.size} amigos integrados",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.75f)
                    )
                }
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.testTag("delete_juntada_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Borrar juntada",
                    tint = Color(0xFFFDA4AF).copy(alpha = 0.9f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    viewModel: JuntadaViewModel,
    onBack: () -> Unit
) {
    val juntada by viewModel.selectedJuntada.collectAsStateWithLifecycle()
    val gastos by viewModel.activeGastos.collectAsStateWithLifecycle()
    val splitResult by viewModel.splitResult.collectAsStateWithLifecycle()

    val pagerState = rememberPagerState(pageCount = { 2 })
    val activeTab = pagerState.currentPage
    val coroutineScope = rememberCoroutineScope()
    var showAddGastoDialog by remember { mutableStateOf(false) }
    var showEditFriendsDialog by remember { mutableStateOf(false) }

    val safeJuntada = juntada
    if (safeJuntada == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color.White)
        }
        return
    }

    val addGastoInteraction = remember { MutableInteractionSource() }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                title = {
                    Column {
                        Text(
                            text = safeJuntada.name,
                            fontWeight = FontWeight.Black,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${safeJuntada.participants.size} amigos integrados",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.65f)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showEditFriendsDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Group,
                            contentDescription = "Editar amigos",
                            tint = Color(0xFFA5F3FC)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Glass Pill for Navigation Tabs
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .glassPanel(cornerRadius = 100f, borderWidth = 0.5f, elevation = 10f)
                            .background(Color(0xFF0F172A).copy(alpha = 0.55f), RoundedCornerShape(100.dp))
                            .padding(4.dp)
                    ) {
                        val currentOffset = pagerState.currentPage + pagerState.currentPageOffsetFraction

                        // Sliding Glass Pill Indicator
                        Box(modifier = Modifier.matchParentSize()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.5f)
                                    .fillMaxHeight()
                                    .graphicsLayer {
                                        translationX = size.width * currentOffset
                                    }
                                    .glassPanel(cornerRadius = 100f, borderWidth = 0.5f, elevation = 2f)
                                    .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(100.dp))
                            )
                        }

                        Row(modifier = Modifier.fillMaxWidth()) {
                            listOf("Gastos 💸", "Saldos 📊").forEachIndexed { index, label ->
                                val isSelected = activeTab == index
                                val tabInteraction = remember { MutableInteractionSource() }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(100.dp))
                                        .bounceScale(tabInteraction)
                                        .clickable(
                                            interactionSource = tabInteraction,
                                            indication = null
                                        ) {
                                            coroutineScope.launch {
                                                pagerState.animateScrollToPage(
                                                    page = index,
                                                    animationSpec = spring(
                                                        dampingRatio = Spring.DampingRatioLowBouncy,
                                                        stiffness = Spring.StiffnessLow
                                                    )
                                                )
                                            }
                                        }
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.65f),
                                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }

                    // Glass Pill for Action Button (only if activeTab == 0 && participants.isNotEmpty())
                    AnimatedVisibility(
                        visible = activeTab == 0 && safeJuntada.participants.isNotEmpty(),
                        enter = scaleIn(spring(stiffness = Spring.StiffnessMediumLow)),
                        exit = scaleOut(spring(stiffness = Spring.StiffnessMediumLow))
                    ) {
                        Box(
                            modifier = Modifier
                                .bounceScale(addGastoInteraction)
                                .glassPanel(cornerRadius = 100f, borderWidth = 0.5f, elevation = 10f)
                                .background(Color(0xFF0F172A).copy(alpha = 0.55f), RoundedCornerShape(100.dp))
                                .clickable(
                                    interactionSource = addGastoInteraction,
                                    indication = null,
                                    onClick = { showAddGastoDialog = true }
                                )
                                .padding(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(100.dp))
                                    .padding(horizontal = 20.dp, vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Agregar Gasto",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "Nuevo", 
                                        color = Color.White,
                                        fontWeight = FontWeight.Black, 
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
                beyondViewportPageCount = 1
            ) { page ->
                if (page == 0) {
                    GastosTab(
                        gastos = gastos,
                        onDeleteGasto = { viewModel.deleteGasto(it) },
                        onAddPrompt = { showAddGastoDialog = true },
                        onEditFriendsPrompt = { showEditFriendsDialog = true },
                        participantsCount = safeJuntada.participants.size
                    )
                } else {
                    SaldosTab(
                        juntada = safeJuntada,
                        splitResult = splitResult ?: SplitResult(BigDecimal.ZERO, BigDecimal.ZERO, emptyList(), emptyList())
                    )
                }
            }
        }
    }

    if (showAddGastoDialog) {
        GastoAddDialog(
            participants = safeJuntada.participants,
            onAdd = { payerName, desc, amount ->
                viewModel.addGasto(payerName, desc, amount)
                showAddGastoDialog = false
            },
            onDismiss = { showAddGastoDialog = false }
        )
    }

    if (showEditFriendsDialog) {
        val friendGroups by viewModel.allFriendGroups.collectAsStateWithLifecycle()
        FriendsEditDialog(
            juntada = safeJuntada,
            friendGroups = friendGroups,
            onAdd = { viewModel.addParticipant(it) },
            onRemove = { viewModel.removeParticipant(it) },
            onDismiss = { showEditFriendsDialog = false }
        )
    }
}

@Composable
fun GastosTab(
    gastos: List<Gasto>,
    onDeleteGasto: (Gasto) -> Unit,
    onAddPrompt: () -> Unit,
    onEditFriendsPrompt: () -> Unit,
    participantsCount: Int
) {
    val emptyBtn1Interaction = remember { MutableInteractionSource() }
    val emptyBtn2Interaction = remember { MutableInteractionSource() }

    Box(modifier = Modifier.fillMaxSize()) {
        if (participantsCount == 0) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Group,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = Color.White.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "¡Sin amigos integrados!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Para poder añadir gastos, primero tenés que agregar a las personas que participan de esta juntada.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onEditFriendsPrompt,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFA5F3FC),
                        contentColor = Color(0xFF0F172A)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.bounceScale(emptyBtn1Interaction)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Integrar Amigos", fontWeight = FontWeight.ExtraBold)
                }
            }
        } else if (gastos.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Receipt,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = Color.White.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "No hay gastos anotados",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Comenzá agregando qué compró y cuánto gastó cada persona en esta juntada para resolver la división de cuentas.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onAddPrompt,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFA5F3FC),
                        contentColor = Color(0xFF0F172A)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.bounceScale(emptyBtn2Interaction)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Agregar Primer Gasto", fontWeight = FontWeight.ExtraBold)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(gastos, key = { it.id }) { gasto ->
                    GastoCard(gasto = gasto, onDelete = { onDeleteGasto(gasto) })
                }
            }
        }
    }
}

@Composable
fun GastoCard(
    gasto: Gasto,
    onDelete: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .bounceScale(interactionSource)
            .glassPanel(cornerRadius = 48f, borderWidth = 2.0f, elevation = 4f)
            .clip(RoundedCornerShape(20.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Circle with initial of payer
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFA5F3FC),
                                    Color(0xFF38BDF8)
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = gasto.payerName.take(1).uppercase(),
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF0F172A),
                        fontSize = 18.sp
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = gasto.description,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )
                    Text(
                        text = "Pagado por ${gasto.payerName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.65f)
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = formatCurrency(gasto.amount),
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = Color(0xFFA5F3FC)
                )
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Borrar gasto",
                        modifier = Modifier.size(18.dp),
                        tint = Color(0xFFFDA4AF).copy(alpha = 0.9f)
                    )
                }
            }
        }
    }
}

@Composable
fun SaldosTab(
    juntada: Juntada,
    splitResult: SplitResult
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val copyBtnInteraction = remember { MutableInteractionSource() }

    // Generate Shareable summary text
    val shareableTextByVaquita = remember(juntada, splitResult) {
        val sb = StringBuilder()
        sb.append("📊 Vaquita: Split de Gastos 🐮\n")
        sb.append("-----------------------------\n")
        sb.append("Juntada: ${juntada.name}\n")
        sb.append("Total Gastado: ${formatCurrency(splitResult.totalSpent)}\n")
        sb.append("Por persona (${juntada.participants.size} amigos): ${formatCurrency(splitResult.quota)}\n\n")

        sb.append("💵 Saldos de cada uno:\n")
        splitResult.balances.forEach { balance ->
            val sign = if (balance.balance >= java.math.BigDecimal.ZERO) "+" else "-"
            val absBalance = balance.balance.abs()
            sb.append("- ${balance.name}: Gastó ${formatCurrency(balance.spent)} (Saldo: $sign${formatCurrency(absBalance)})\n")
        }

        sb.append("\n💸 ¿Cómo arreglamos las cuentas? (Quilombo resuelto):\n")
        if (splitResult.transfers.isEmpty()) {
            sb.append("✅ ¡Están todos a mano! No hay deudas pendientes.\n")
        } else {
            splitResult.transfers.forEach { transfer ->
                sb.append("👉 ${transfer.from} le tiene que pagar ${formatCurrency(transfer.amount)} a ${transfer.to}\n")
            }
        }
        sb.append("\n¡Hecho con Vaquita! 🐮✨")
        sb.toString()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Core Statistics Card (Asymmetric & Depth visual design)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassPanel(cornerRadius = 60f, borderWidth = 2.5f, elevation = 6f)
                    .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(24.dp))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "RESUMEN DE LA JUNTADA 📊",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFA5F3FC),
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Total Gastado",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = formatCurrency(splitResult.totalSpent),
                                fontWeight = FontWeight.Black,
                                fontSize = 24.sp,
                                color = Color.White
                            )
                        }
                        Box(
                            modifier = Modifier
                                .height(48.dp)
                                .width(1.5.dp)
                                .background(Color.White.copy(alpha = 0.15f))
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Cuota por Persona",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = formatCurrency(splitResult.quota),
                                fontWeight = FontWeight.Black,
                                fontSize = 24.sp,
                                color = Color(0xFFA5F3FC)
                            )
                        }
                    }
                }
            }
        }

        // Settlements / Transfers recommendations (The core solution)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassPanel(cornerRadius = 60f, borderWidth = 2.5f, elevation = 6f)
                    .background(Color.White.copy(alpha = 0.03f), RoundedCornerShape(24.dp))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = null,
                                tint = Color(0xFFA5F3FC),
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "¿Cómo arreglar las cuentas?",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                        }

                        // Share Action Button with WhatsApp support
                        IconButton(
                            onClick = {
                                try {
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, shareableTextByVaquita)
                                        type = "text/plain"
                                    }
                                    val shareIntent = Intent.createChooser(sendIntent, "Enviar a los pibes por WhatsApp").apply {
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    }
                                    context.startActivity(shareIntent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "No hay aplicaciones disponibles para compartir", Toast.LENGTH_LONG).show()
                                    e.printStackTrace()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Compartir",
                                tint = Color(0xFFA5F3FC)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    if (splitResult.transfers.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Color(0xFF065F46).copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .border(1.dp, Color(0xFF34D399).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "✨ ¡Perfecto! Nadie le debe nada a nadie.",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF34D399),
                                fontSize = 14.sp
                            )
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            splitResult.transfers.forEach { transfer ->
                                TransferRow(transfer = transfer)
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(shareableTextByVaquita))
                                Toast.makeText(context, "¡Cuentas copiadas! Pegalas en el WhatsApp", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFA5F3FC),
                                contentColor = Color(0xFF0F172A)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .bounceScale(copyBtnInteraction)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Copiar Cuentas para WhatsApp", fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }
        }

        // Balances breakdown label
        item {
            Text(
                text = "Detalle por Participante 🐮",
                fontWeight = FontWeight.Black,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
            )
        }

        items(splitResult.balances) { balance ->
            BalanceRow(balance = balance)
        }
    }
}

@Composable
fun TransferRow(transfer: Transfer) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .glassPanel(cornerRadius = 32f, borderWidth = 1.5f, elevation = 2f)
            .height(IntrinsicSize.Min)
            .background(Color.White.copy(alpha = 0.02f), RoundedCornerShape(14.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = transfer.from,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowRight,
                    contentDescription = "paga a",
                    tint = Color(0xFFFDA4AF),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Text(
                    text = transfer.to,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF34D399),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = formatCurrency(transfer.amount),
                fontWeight = FontWeight.Black,
                fontSize = 15.sp,
                color = Color(0xFFA5F3FC)
            )
        }
    }
}

@Composable
fun BalanceRow(balance: ParticipantBalance) {
    val isCreditor = balance.balance >= java.math.BigDecimal.ZERO
    val formattedDiff = formatCurrency(balance.balance.abs())

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .glassPanel(cornerRadius = 36f, borderWidth = 1.5f, elevation = 3f)
            .background(Color.White.copy(alpha = 0.04f), RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = balance.name,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Gastó ${formatCurrency(balance.spent)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }

            Box(
                modifier = Modifier
                    .background(
                        color = if (isCreditor) Color(0xFF065F46).copy(alpha = 0.4f) else Color(0xFF991B1B).copy(alpha = 0.4f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = if (isCreditor) Color(0xFF34D399).copy(alpha = 0.6f) else Color(0xFFFCA5A5).copy(alpha = 0.6f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (isCreditor) "+$formattedDiff" else "-$formattedDiff",
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    color = if (isCreditor) Color(0xFF34D399) else Color(0xFFFCA5A5)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JuntadaCreateDialog(
    friendGroups: List<FriendGroup>,
    onConfirm: (String, List<String>) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var currentFriend by remember { mutableStateOf("") }
    val friendsList = remember { mutableStateListOf<String>() }
    val addFriendBtnInteraction = remember { MutableInteractionSource() }
    val cancelBtnInteraction = remember { MutableInteractionSource() }
    val confirmBtnInteraction = remember { MutableInteractionSource() }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .glassPanel(cornerRadius = 70f, borderWidth = 3.0f, elevation = 16f)
                .background(Color(0xFF0F172A).copy(alpha = 0.88f), RoundedCornerShape(28.dp))
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Nueva Juntada 🐮",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre de la Juntada") },
                    placeholder = { Text("Asado del finde, Escapada, etc.") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFA5F3FC),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedLabelColor = Color(0xFFA5F3FC),
                        unfocusedLabelColor = Color.White.copy(alpha = 0.5f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedPlaceholderColor = Color.White.copy(alpha = 0.3f),
                        unfocusedPlaceholderColor = Color.White.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("juntada_name_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Amigos participantes:",
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp
                )

                if (friendGroups.isNotEmpty()) {
                    Text(
                        text = "Cargar integrantes de Grupo:",
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 12.sp
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(friendGroups) { group ->
                            val groupInteraction = remember { MutableInteractionSource() }
                            Box(
                                modifier = Modifier
                                    .bounceScale(groupInteraction)
                                    .background(Color(0xFFA5F3FC).copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                                    .border(1.dp, Color(0xFFA5F3FC).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                    .clickable(
                                        interactionSource = groupInteraction,
                                        indication = LocalIndication.current
                                    ) {
                                        group.participants.forEach { member ->
                                            if (!friendsList.contains(member)) {
                                                friendsList.add(member)
                                            }
                                        }
                                    }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Groups,
                                        contentDescription = null,
                                        tint = Color(0xFFA5F3FC),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = group.name,
                                        color = Color(0xFFA5F3FC),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = currentFriend,
                        onValueChange = { currentFriend = it },
                        label = { Text("Nombre del Amigo") },
                        placeholder = { Text("Pepito") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFA5F3FC),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                            focusedLabelColor = Color(0xFFA5F3FC),
                            unfocusedLabelColor = Color.White.copy(alpha = 0.5f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedPlaceholderColor = Color.White.copy(alpha = 0.3f),
                            unfocusedPlaceholderColor = Color.White.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("friend_name_input"),
                        singleLine = true
                    )
                    IconButton(
                        onClick = {
                            if (currentFriend.trim().isNotEmpty() && !friendsList.contains(currentFriend.trim())) {
                                friendsList.add(currentFriend.trim())
                                currentFriend = ""
                            }
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color(0xFFA5F3FC)
                        ),
                        modifier = Modifier
                            .bounceScale(addFriendBtnInteraction)
                            .size(52.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Agregar amigo",
                            tint = Color(0xFF0F172A),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Friends chip list
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 120.dp)
                ) {
                    if (friendsList.isEmpty()) {
                        Text(
                            text = "Agregá al menos a un par de amigos para dividir las cuentas.",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            items(friendsList) { friend ->
                                Box(
                                    modifier = Modifier
                                        .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                                        .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                        .clickable { friendsList.remove(friend) }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(text = friend, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Remover",
                                            tint = Color(0xFFFDA4AF),
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.bounceScale(cancelBtnInteraction)
                    ) {
                        Text("Cancelar", color = Color.White.copy(alpha = 0.6f), fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = {
                            if (name.trim().isNotEmpty()) {
                                onConfirm(name, friendsList.toList())
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFA5F3FC),
                            contentColor = Color(0xFF0F172A),
                            disabledContainerColor = Color.White.copy(alpha = 0.1f),
                            disabledContentColor = Color.White.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(14.dp),
                        enabled = name.trim().isNotEmpty(),
                        modifier = Modifier.bounceScale(confirmBtnInteraction)
                    ) {
                        Text("Comenzar", fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GastoAddDialog(
    participants: List<String>,
    onAdd: (String, String, BigDecimal) -> Unit,
    onDismiss: () -> Unit
) {
    var payerName by remember { mutableStateOf(participants.firstOrNull() ?: "") }
    var description by remember { mutableStateOf("") }
    var amountStr by remember { mutableStateOf("") }
    val cancelBtnInteraction = remember { MutableInteractionSource() }
    val saveBtnInteraction = remember { MutableInteractionSource() }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .glassPanel(cornerRadius = 70f, borderWidth = 3.0f, elevation = 16f)
                .background(Color(0xFF0F172A).copy(alpha = 0.88f), RoundedCornerShape(28.dp))
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Anotar Gasto 💸",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("¿Qué se compró?") },
                    placeholder = { Text("Ej. Bondiola, Bebidas, Carbón") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFA5F3FC),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedLabelColor = Color(0xFFA5F3FC),
                        unfocusedLabelColor = Color.White.copy(alpha = 0.5f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedPlaceholderColor = Color.White.copy(alpha = 0.3f),
                        unfocusedPlaceholderColor = Color.White.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("gasto_name_input"),
                    singleLine = true
                )

                // Amount
                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("¿Cuánto salió? (ARS)") },
                    placeholder = { Text("Ej. 15000") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFA5F3FC),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedLabelColor = Color(0xFFA5F3FC),
                        unfocusedLabelColor = Color.White.copy(alpha = 0.5f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedPlaceholderColor = Color.White.copy(alpha = 0.3f),
                        unfocusedPlaceholderColor = Color.White.copy(alpha = 0.3f)
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("gasto_amount_input"),
                    singleLine = true
                )

                // Selector: ¿Quién pagó?
                Text(
                    text = "¿Quién lo pagó?",
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(participants) { name ->
                        val isSelected = payerName.equals(name, ignoreCase = true)
                        val interactionSource = remember { MutableInteractionSource() }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .bounceScale(interactionSource)
                                .background(
                                    color = if (isSelected) Color(0xFFA5F3FC)
                                    else Color.White.copy(alpha = 0.08f)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = LocalIndication.current
                                ) { payerName = name }
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = name,
                                color = if (isSelected) Color(0xFF0F172A)
                                else Color.White.copy(alpha = 0.75f),
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.bounceScale(cancelBtnInteraction)
                    ) {
                        Text("Cancelar", color = Color.White.copy(alpha = 0.6f), fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = {
                            val amount = amountStr.replace(',', '.').toBigDecimalOrNull() ?: java.math.BigDecimal.ZERO
                            if (payerName.isNotEmpty() && amount > java.math.BigDecimal.ZERO) {
                                onAdd(payerName, description, amount)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFA5F3FC),
                            contentColor = Color(0xFF0F172A),
                            disabledContainerColor = Color.White.copy(alpha = 0.1f),
                            disabledContentColor = Color.White.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(14.dp),
                        enabled = payerName.isNotEmpty() && (amountStr.replace(',', '.').toBigDecimalOrNull() ?: java.math.BigDecimal.ZERO) > java.math.BigDecimal.ZERO,
                        modifier = Modifier.bounceScale(saveBtnInteraction)
                    ) {
                        Text("Guardar", fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsEditDialog(
    juntada: Juntada,
    friendGroups: List<FriendGroup>,
    onAdd: (String) -> Unit,
    onRemove: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    val addFriendBtnInteraction = remember { MutableInteractionSource() }
    val okBtnInteraction = remember { MutableInteractionSource() }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .glassPanel(cornerRadius = 70f, borderWidth = 3.0f, elevation = 16f)
                .background(Color(0xFF0F172A).copy(alpha = 0.88f), RoundedCornerShape(28.dp))
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Añadir Amigos 👥",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre del Amigo") },
                        placeholder = { Text("Ej. Juan, Lucas") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFA5F3FC),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                            focusedLabelColor = Color(0xFFA5F3FC),
                            unfocusedLabelColor = Color.White.copy(alpha = 0.5f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedPlaceholderColor = Color.White.copy(alpha = 0.3f),
                            unfocusedPlaceholderColor = Color.White.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("add_friend_on_edit_input"),
                        singleLine = true
                    )
                    IconButton(
                        onClick = {
                            if (name.trim().isNotEmpty()) {
                                onAdd(name.trim())
                                name = ""
                            }
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color(0xFFA5F3FC)
                        ),
                        modifier = Modifier
                            .bounceScale(addFriendBtnInteraction)
                            .size(52.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Agregar amigo",
                            tint = Color(0xFF0F172A),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Amigos en esta juntada:",
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.titleSmall
                )

                if (friendGroups.isNotEmpty()) {
                    Text(
                        text = "Agregar integrantes de Grupo:",
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 12.sp
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(friendGroups) { group ->
                            val groupInteraction = remember { MutableInteractionSource() }
                            Box(
                                modifier = Modifier
                                    .bounceScale(groupInteraction)
                                    .background(Color(0xFFA5F3FC).copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                                    .border(1.dp, Color(0xFFA5F3FC).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                    .clickable(
                                        interactionSource = groupInteraction,
                                        indication = LocalIndication.current
                                    ) {
                                        group.participants.forEach { member ->
                                            if (!juntada.participants.contains(member)) {
                                                onAdd(member)
                                            }
                                        }
                                    }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Groups,
                                        contentDescription = null,
                                        tint = Color(0xFFA5F3FC),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = group.name,
                                        color = Color(0xFFA5F3FC),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                ) {
                    if (juntada.participants.isEmpty()) {
                        Text(
                            text = "No agregaste ningún amigo todavía.",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(juntada.participants) { friend ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                                        .padding(horizontal = 14.dp, vertical = 10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = friend,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = 14.sp
                                        )
                                        IconButton(
                                            onClick = { onRemove(friend) },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Remover",
                                                tint = Color(0xFFFDA4AF),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFA5F3FC),
                            contentColor = Color(0xFF0F172A)
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.bounceScale(okBtnInteraction)
                    ) {
                        Text("Aceptar", fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupManagerDialog(
    viewModel: JuntadaViewModel,
    onDismiss: () -> Unit
) {
    val groups by viewModel.allFriendGroups.collectAsStateWithLifecycle()
    
    var newGroupName by remember { mutableStateOf("") }
    var currentMemberName by remember { mutableStateOf("") }
    val newGroupMembers = remember { mutableStateListOf<String>() }
    
    val addMemberInteraction = remember { MutableInteractionSource() }
    val saveGroupInteraction = remember { MutableInteractionSource() }
    val closeInteraction = remember { MutableInteractionSource() }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .glassPanel(cornerRadius = 70f, borderWidth = 3.0f, elevation = 16f)
                .background(Color(0xFF0F172A).copy(alpha = 0.88f), RoundedCornerShape(28.dp))
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Mis Grupos de Amigos 👥",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.bounceScale(closeInteraction)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }

                // List of existing groups
                Text(
                    text = "Grupos Guardados:",
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                ) {
                    if (groups.isEmpty()) {
                        Text(
                            text = "No tenés grupos creados todavía. ¡Rellená el formulario de abajo para armar uno!",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(groups) { group ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(14.dp))
                                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(14.dp))
                                        .padding(14.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = group.name,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                fontSize = 15.sp
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = group.participants.joinToString(", "),
                                                fontSize = 12.sp,
                                                color = Color.White.copy(alpha = 0.6f)
                                            )
                                        }
                                        IconButton(
                                            onClick = { viewModel.deleteFriendGroup(group) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Borrar grupo",
                                                tint = Color(0xFFFDA4AF),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Divider(color = Color.White.copy(alpha = 0.1f))

                // Creation parameters
                Text(
                    text = "Crear Nuevo Grupo:",
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp
                )

                OutlinedTextField(
                    value = newGroupName,
                    onValueChange = { newGroupName = it },
                    label = { Text("Nombre del Grupo") },
                    placeholder = { Text("Ej. Familia, Fútbol, Facu") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFA5F3FC),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedLabelColor = Color(0xFFA5F3FC),
                        unfocusedLabelColor = Color.White.copy(alpha = 0.5f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedPlaceholderColor = Color.White.copy(alpha = 0.3f),
                        unfocusedPlaceholderColor = Color.White.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("group_name_input"),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = currentMemberName,
                        onValueChange = { currentMemberName = it },
                        label = { Text("Miembro del Grupo") },
                        placeholder = { Text("Ej. Lucas") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFA5F3FC),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                            focusedLabelColor = Color(0xFFA5F3FC),
                            unfocusedLabelColor = Color.White.copy(alpha = 0.5f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedPlaceholderColor = Color.White.copy(alpha = 0.3f),
                            unfocusedPlaceholderColor = Color.White.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("group_member_name_input"),
                        singleLine = true
                    )
                    IconButton(
                        onClick = {
                            if (currentMemberName.trim().isNotEmpty() && !newGroupMembers.contains(currentMemberName.trim())) {
                                newGroupMembers.add(currentMemberName.trim())
                                currentMemberName = ""
                            }
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color(0xFFA5F3FC)
                        ),
                        modifier = Modifier
                            .bounceScale(addMemberInteraction)
                            .size(52.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Agregar miembro",
                            tint = Color(0xFF0F172A),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Added group members flow
                if (newGroupMembers.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        items(newGroupMembers) { member ->
                            Box(
                                modifier = Modifier
                                    .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                    .clickable { newGroupMembers.remove(member) }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(text = member, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remover",
                                        tint = Color(0xFFFDA4AF),
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            if (newGroupName.trim().isNotEmpty() && newGroupMembers.isNotEmpty()) {
                                viewModel.createFriendGroup(newGroupName, newGroupMembers.toList())
                                newGroupName = ""
                                newGroupMembers.clear()
                            }
                        },
                        enabled = newGroupName.trim().isNotEmpty() && newGroupMembers.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFA5F3FC),
                            contentColor = Color(0xFF0F172A),
                            disabledContainerColor = Color.White.copy(alpha = 0.1f),
                            disabledContentColor = Color.White.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.bounceScale(saveGroupInteraction)
                    ) {
                        Text("Crear Grupo", fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}

