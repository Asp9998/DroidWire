package com.aryanspatel.droidwire.presentation.screens

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.aryanspatel.droidwire.domain.usecase.openUrlInBrowser
import com.aryanspatel.droidwire.domain.usecase.shareURL
import com.aryanspatel.droidwire.presentation.models.ArticleDetailUi
import com.aryanspatel.droidwire.presentation.models.DetailEvent
import com.aryanspatel.droidwire.presentation.viewmodels.DetailViewModel
import kotlinx.coroutines.delay

// ============= Main Screen Composable =============

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    id: String,
    url: String,
    onBackPressed: () -> Unit,
    viewModel: DetailViewModel = hiltViewModel()
) {
    Log.d("DetailDebug", "DetailScreen: $id, $url")
    val uiState by viewModel.uiState.collectAsState()
    val saved by viewModel.saved.collectAsState()
    val context = LocalContext.current
    var showToast by remember { mutableStateOf(false) }

    // Handle events
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is DetailEvent.ShowSavedToast -> showToast = true
                is DetailEvent.OpenShareSheet -> shareURL(context, event.url ?: "https://android-developers.googleblog.com")
                is DetailEvent.OpenExternal -> openUrlInBrowser(context, event.url ?: "https://android-developers.googleblog.com")
            }
        }
    }

    // Auto-hide toast
    LaunchedEffect(showToast) {
        if (showToast) {
            delay(2000)
            showToast = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.loading -> LoadingContent()
            uiState.error != null -> ErrorContent(
                error = uiState.error!!,
                onRetry = {  }
            )
            uiState.article != null -> {
                ArticleContent(
                    article = uiState.article!!,
                    saved = saved,
                    onBackPressed = onBackPressed,
                    onSaveToggle = {},
                    onShare = { viewModel.onShare(it)},
                    onOpenExternal = { viewModel.openExternal(it) }
                )
            }
        }

        // Toast notification
        AnimatedVisibility(
            visible = showToast,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFF1E293B),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = if (saved) "Article saved!" else "Article removed from saved",
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

// ============= Loading Skeleton =============

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadingContent() {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFF8FAFC), Color(0xFFE2E8F0))
                )
            )
    ) {
        // Top bar skeleton
        TopAppBar(
            title = {},
            navigationIcon = {
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray.copy(alpha = shimmerAlpha))
                )
            },
            actions = {
                repeat(2) {
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray.copy(alpha = shimmerAlpha))
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White.copy(alpha = 0.8f)
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title skeleton
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .height(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray.copy(alpha = shimmerAlpha))
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray.copy(alpha = shimmerAlpha))
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(24.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray.copy(alpha = shimmerAlpha))
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Image skeleton
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.LightGray.copy(alpha = shimmerAlpha))
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Description skeleton
            repeat(4) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(if (it == 3) 0.8f else 1f)
                        .height(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.LightGray.copy(alpha = shimmerAlpha))
                )
            }
        }
    }
}

// ============= Error Screen =============

@Composable
fun ErrorContent(
    error: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFF8FAFC), Color(0xFFE2E8F0))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(32.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(64.dp)
                )
                Text(
                    text = "Oops!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = error,
                    fontSize = 16.sp,
                    color = Color(0xFF64748B),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2563EB)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Try Again", fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

// ============= Article Content =============

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ArticleContent(
    article: ArticleDetailUi,
    saved: Boolean,
    onBackPressed: () -> Unit,
    onSaveToggle: () -> Unit,
    onShare: (String) -> Unit,
    onOpenExternal: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFF8FAFC), Color(0xFFE2E8F0))
                )
            )
    ) {
        // Top App Bar
        TopAppBar(
            title = {},
            navigationIcon = {
                IconButton(onClick = onBackPressed) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF334155)
                    )
                }
            },
            actions = {
                IconButton(onClick = { onShare(article.originalUrl ?: "https://android-developers.googleblog.com") }) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = Color(0xFF334155)
                    )
                }
                IconButton(onClick = { onOpenExternal(article.originalUrl ?: "https://android-developers.googleblog.com") }) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Open in browser",
                        tint = Color(0xFF334155)
                    )
                }
            },
//            colors = TopAppBarDefaults.topAppBarColors(
//                containerColor = Color.White.copy(alpha = 0.8f)
//            )
        )

        // Scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    lineHeight = 40.sp
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = article.source,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2563EB)
                    )
                    Text("•", color = Color(0xFF64748B))
                    Text(
                        text = article.timeAgo,
                        fontSize = 14.sp,
                        color = Color(0xFF64748B)
                    )
                    Text("•", color = Color(0xFF64748B))

                    // Save button
                    Surface(
                        onClick = onSaveToggle,
                        shape = RoundedCornerShape(20.dp),
                        color = if (saved) Color(0xFFFEF3C7) else Color(0xFFF1F5F9)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (saved) Icons.Filled.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (saved) Color(0xFFA16207) else Color(0xFF64748B)
                            )
                            Text(
                                text = if (saved) "Saved" else "Save",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (saved) Color(0xFFA16207) else Color(0xFF64748B)
                            )
                        }
                    }
                }
            }

            // Image Gallery
            if (article.gallery.isNotEmpty()) {
                ImageGallery(images = article.gallery)
            }

            // Body Content
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    article.description?.split("\n\n")?.forEach { paragraph ->
                        Text(
                            text = paragraph,
                            fontSize = 16.sp,
                            lineHeight = 26.sp,
                            color = Color(0xFF334155)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ============= Image Gallery Component =============

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageGallery(images: List<String>) {
    val pagerState = rememberPagerState(pageCount = { images.size })

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(16.dp))
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(images[page])
                        .crossfade(true)
                        .build(),
                    contentDescription = "Image ${page + 1}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // Dots indicator
        if (images.size > 1) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(images.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(
                                width = if (isSelected) 32.dp else 8.dp,
                                height = 8.dp
                            )
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (isSelected) Color(0xFF2563EB) else Color(0xFFCBD5E1)
                            )
                            .animateContentSize()
                    )
                }
            }
        }
    }
}
