package com.example.neakta.ui.detail

import com.example.neakta.data.SessionManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.neakta.model.CommentResponse
import com.example.neakta.ui.home.PinCard
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem

private val InkText = Color(0xFFF7FAFC)
private val MutedText = Color(0xFFB8C2CC)
private val ElectricBlue = Color(0xFF5FD3A6)
private val Bubblegum = Color(0xFF5FD3A6)
private val LimePop = Color(0xFF5FD3A6)
private val DeepIndigo = Color(0xFF0D1117)
private val CardStart = Color(0xE61A202C)
private val CardEnd = Color(0xCC111827)
private val SoftOutline = Color(0x26FFFFFF)
private val GlassPanel = Color(0x991A202C)

@Composable
fun PinDetailScreen(
    pin: PinCard,
    onBack: () -> Unit,
    onRefresh: () -> Unit = {},
    voteViewModel: VoteViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = VoteViewModel.Factory(SessionManager(LocalContext.current))
    ),
    commentViewModel: CommentViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = CommentViewModel.Factory(SessionManager(LocalContext.current))
    )
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val pageScroll = rememberScrollState()
    val tagScroll = rememberScrollState()

    LaunchedEffect(pin.id) {
        voteViewModel.init(pin.votes, pin.id)
        commentViewModel.loadComments(pin.id)
    }

    val localVotes by voteViewModel.voteCount.collectAsState()
    val foundPressed by voteViewModel.hasVoted.collectAsState()
    val isSaved by voteViewModel.isSaved.collectAsState()
    val hasConfirmed by voteViewModel.hasConfirmed.collectAsState()
    val stillExistsPct by voteViewModel.stillExistsPct.collectAsState()

    val comments by commentViewModel.comments.collectAsState()
    val isLoadingComments by commentViewModel.isLoading.collectAsState()
    val isSendingComment by commentViewModel.isSending.collectAsState()
    var commentText by remember { mutableStateOf("") }
    var selectedStars by remember { mutableStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0D1117),
                        Color(0xFF111827),
                        Color(0xFF0D1117),
                        DeepIndigo
                    )
                )
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(360.dp)
                .background(
                    Brush.linearGradient(
                        listOf(
                            LimePop.copy(alpha = 0.16f),
                            LimePop.copy(alpha = 0.05f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(pageScroll)
                .padding(bottom = 118.dp)
        ) {
            HeroSection(
                pin = pin,
                onBack = {
                    onRefresh()
                    onBack()
                },
                isSaved = isSaved,
                onSaveClick = { voteViewModel.toggleSaved(pin.id) }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                PinHeader(pin = pin)

                StatsRow(
                    pin = pin,
                    localVotes = localVotes,
                    liveStillExistsPct = stillExistsPct
                )

                if (pin.tags.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(tagScroll),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        pin.tags.forEach { tag ->
                            NeonCapsule(text = tag, accent = Bubblegum)
                        }
                    }
                }

                DetailPanel(
                    eyebrow = "THE STORY",
                    title = "Why locals keep saving this",
                    accent = ElectricBlue
                ) {
                    Text(
                        text = pin.story,
                        style = TextStyle(
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 16.sp,
                            lineHeight = 24.sp,
                            fontWeight = FontWeight.Medium,
                            color = MutedText
                        )
                    )
                }

                LocationPanel(
                    pin = pin,
                    onOpenMaps = { openInMaps(context, pin.lat, pin.lng, pin.title) },
                    onCopyCoords = {
                        clipboardManager.setText(AnnotatedString("${pin.lat}, ${pin.lng}"))
                    }
                )
                val currentUserId = remember { SessionManager(context).getUserId() }

                CommentsPanel(
                    comments = comments,
                    isLoading = isLoadingComments,
                    isSending = isSendingComment,
                    commentText = commentText,
                    onCommentTextChange = { commentText = it },
                    selectedStars = selectedStars,
                    onStarsChange = { selectedStars = it },
                    currentUserId = currentUserId,
                    onSubmit = {
                        commentViewModel.postComment(pin.id, commentText, selectedStars.takeIf { it > 0 }) {
                            commentText = ""
                            selectedStars = 0
                        }
                    },                                                          // ← comma here, NOT closing paren
                    onEdit = { commentId, newContent, newStars ->
                        commentViewModel.editComment(commentId, newContent, newStars) {}
                    },
                    onDelete = { commentId ->
                        commentViewModel.deleteComment(commentId)
                    }
                )

                TextButton(
                    onClick = { voteViewModel.flagPin(pin.id) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Flag,
                        contentDescription = null,
                        tint = MutedText.copy(alpha = 0.58f),
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Flag as outdated or incorrect",
                        style = TextStyle(
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MutedText.copy(alpha = 0.58f)
                        )
                    )
                }
            }
        }

        BottomFoundBar(
            foundPressed   = foundPressed,
            hasConfirmed   = hasConfirmed,
            localVotes     = localVotes,
            onFoundClick   = { voteViewModel.toggleVote(pin.id) },
            onConfirmClick = { voteViewModel.confirmExists(pin.id) },
            modifier       = Modifier.align(Alignment.BottomCenter)
        )
    }
}

// ─── Comments ────────────────────────────────────────────────────────────────

@Composable
private fun CommentsPanel(
    comments: List<CommentResponse>,
    isLoading: Boolean,
    isSending: Boolean,
    commentText: String,
    selectedStars: Int,
    onStarsChange: (Int) -> Unit,
    onCommentTextChange: (String) -> Unit,
    currentUserId: String?,
    onSubmit: () -> Unit,
    onEdit: (commentId: String, content: String, stars: Int?) -> Unit,
    onDelete: (commentId: String) -> Unit
) {
    DetailPanel(
        eyebrow = "COMMUNITY",
        title = "What others are saying",
        accent = ElectricBlue
    ) {
        // Star rating row
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Rate:",
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MutedText
                )
            )
            (1..5).forEach { star ->
                Text(
                    text = if (star <= selectedStars) "★" else "☆",
                    fontSize = 22.sp,
                    color = if (star <= selectedStars) Color(0xFFFFD700) else MutedText.copy(alpha = 0.4f),
                    modifier = Modifier.clickable { onStarsChange(star) }
                )
            }
            // Show selected count so user knows it's registered
            if (selectedStars > 0) {
                Text(
                    text = "($selectedStars)",
                    style = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD700)
                    )
                )
            }
        }

        // Input row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = commentText,
                onValueChange = onCommentTextChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        text = "Add a comment…",
                        style = TextStyle(
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 13.sp,
                            color = MutedText.copy(alpha = 0.55f)
                        )
                    )
                },
                textStyle = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 13.sp,
                    color = InkText
                ),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ElectricBlue.copy(alpha = 0.70f),
                    unfocusedBorderColor = SoftOutline,
                    cursorColor = ElectricBlue,
                    focusedContainerColor = GlassPanel,
                    unfocusedContainerColor = GlassPanel
                ),
                maxLines = 3,
                singleLine = false
            )

            // Send button — enabled if text OR stars selected
            val canSend = commentText.isNotBlank() || selectedStars > 0
            Surface(
                onClick = onSubmit,
                shape = RoundedCornerShape(14.dp),
                color = if (canSend) ElectricBlue.copy(alpha = 0.18f) else GlassPanel,
                border = BorderStroke(
                    1.dp,
                    if (canSend) ElectricBlue.copy(alpha = 0.70f) else SoftOutline
                ),
                modifier = Modifier.size(50.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    if (isSending) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = ElectricBlue,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send comment",
                            tint = if (canSend) ElectricBlue else MutedText.copy(alpha = 0.42f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = ElectricBlue,
                        strokeWidth = 2.dp
                    )
                }
            }
            comments.isEmpty() -> {
                Text(
                    text = "No comments yet. Be the first!",
                    style = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MutedText.copy(alpha = 0.55f)
                    ),
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
            else -> {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    comments.forEach { comment ->
                        CommentItem(
                            comment = comment,
                            isOwner = currentUserId != null && comment.userId == currentUserId,
                            onEdit = onEdit,
                            onDelete = onDelete
                        )
                    }
                }
            }
        }
    }
}
@Composable
private fun CommentItem(
    comment: CommentResponse,
    isOwner: Boolean,
    onEdit: (commentId: String, content: String, stars: Int?) -> Unit,
    onDelete: (commentId: String) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var editText by remember { mutableStateOf(comment.content) }
    var editStars by remember { mutableStateOf(comment.stars ?: 0) }
    var showMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Avatar
        Surface(
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.08f),
            border = BorderStroke(1.dp, SoftOutline),
            modifier = Modifier.size(34.dp)
        ) {
            if (!comment.userAvatar.isNullOrBlank()) {
                AsyncImage(
                    model = comment.userAvatar,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().clip(CircleShape)
                )
            } else {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = LimePop,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Bubble
        Column(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(14.dp))
                .background(GlassPanel)
                .border(1.dp, SoftOutline, RoundedCornerShape(14.dp))
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Header: username + edit/delete menu for owner
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = comment.username,
                    style = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = LimePop
                    )
                )
                if (isOwner) {
                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.size(22.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Options",
                                tint = MutedText.copy(alpha = 0.6f),
                                modifier = Modifier.size(15.dp)
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(Color(0xFF1A202C))
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "Edit",
                                        style = TextStyle(
                                            fontFamily = FontFamily.SansSerif,
                                            fontSize = 13.sp,
                                            color = ElectricBlue
                                        )
                                    )
                                },
                                onClick = {
                                    editText = comment.content
                                    editStars = comment.stars ?: 0
                                    isEditing = true
                                    showMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "Delete",
                                        style = TextStyle(
                                            fontFamily = FontFamily.SansSerif,
                                            fontSize = 13.sp,
                                            color = Color(0xFFFF6B6B)
                                        )
                                    )
                                },
                                onClick = {
                                    onDelete(comment.id)
                                    showMenu = false
                                }
                            )
                        }
                    }
                }
            }

            if (isEditing) {
                // Edit mode
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    (1..5).forEach { star ->
                        Text(
                            text = if (star <= editStars) "★" else "☆",
                            fontSize = 18.sp,
                            color = if (star <= editStars) Color(0xFFFFD700) else MutedText.copy(alpha = 0.4f),
                            modifier = Modifier.clickable { editStars = star }
                        )
                    }
                }
                OutlinedTextField(
                    value = editText,
                    onValueChange = { editText = it },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 13.sp,
                        color = InkText
                    ),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBlue.copy(alpha = 0.70f),
                        unfocusedBorderColor = SoftOutline,
                        cursorColor = ElectricBlue,
                        focusedContainerColor = GlassPanel,
                        unfocusedContainerColor = GlassPanel
                    ),
                    maxLines = 3
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = { isEditing = false }) {
                        Text(
                            "Cancel",
                            style = TextStyle(
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 12.sp,
                                color = MutedText
                            )
                        )
                    }
                    TextButton(
                        onClick = {
                            onEdit(comment.id, editText, editStars.takeIf { it > 0 })
                            isEditing = false
                        }
                    ) {
                        Text(
                            "Save",
                            style = TextStyle(
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = ElectricBlue
                            )
                        )
                    }
                }
            } else {
                // View mode
                if ((comment.stars ?: 0) > 0) {
                    Row {
                        repeat(comment.stars ?: 0) {
                            Text(text = "★", color = Color(0xFFFFD700), fontSize = 14.sp)
                        }
                    }
                }
                if (comment.content.isNotBlank()) {
                    Text(
                        text = comment.content,
                        style = TextStyle(
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 13.sp,
                            lineHeight = 19.sp,
                            fontWeight = FontWeight.Medium,
                            color = InkText
                        )
                    )
                }
            }
        }
    }
}

// ─── Hero ─────────────────────────────────────────────────────────────────────

@Composable
private fun HeroSection(
    pin: PinCard,
    onBack: () -> Unit,
    isSaved: Boolean,
    onSaveClick: () -> Unit
) {
    val mediaUrls = remember(pin.mediaUrls, pin.imageUrl) {
        (pin.mediaUrls + pin.imageUrl)
            .filter { it.isNotBlank() }
            .distinct()
    }
    val pagerState = rememberPagerState(pageCount = { mediaUrls.size })
    val coroutineScope = rememberCoroutineScope()
    var fullScreenPhotoIndex by remember(pin.id) { mutableStateOf<Int?>(null) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(390.dp)
    ) {
        if (mediaUrls.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(CardStart),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No photo",
                    style = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MutedText
                    )
                )
            }
        } else {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                AsyncImage(
                    model = mediaUrls[page],
                    contentDescription = "${pin.title} photo ${page + 1}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { fullScreenPhotoIndex = page }
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0x77111827),
                            Color(0x22111827),
                            Color(0xEE0D1117)
                        )
                    )
                )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            GlassIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                onClick = onBack
            )
            GlassIconButton(
                icon = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                contentDescription = "Save",
                onClick = onSaveClick
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 20.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NeonCapsule(text = pin.category.uppercase(), accent = ElectricBlue)
                NeonCapsule(text = "#${pin.votes} rising", accent = LimePop)
            }
            Text(
                text = pin.title,
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 38.sp,
                    lineHeight = 40.sp,
                    fontWeight = FontWeight.Black,
                    color = InkText
                ),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (mediaUrls.size > 1) {
            GalleryNavigation(
                currentPage = pagerState.currentPage,
                pageCount = mediaUrls.size,
                onPrevious = {
                    val previousPage = (pagerState.currentPage - 1).coerceAtLeast(0)
                    coroutineScope.launch { pagerState.animateScrollToPage(previousPage) }
                },
                onNext = {
                    val nextPage = (pagerState.currentPage + 1).coerceAtMost(mediaUrls.lastIndex)
                    coroutineScope.launch { pagerState.animateScrollToPage(nextPage) }
                },
                modifier = Modifier.align(Alignment.Center)
            )
            MediaCounter(
                currentPage = pagerState.currentPage,
                pageCount = mediaUrls.size,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp, bottom = 30.dp)
            )
        }
    }

    fullScreenPhotoIndex?.let { startPage ->
        FullScreenPhotoGallery(
            title = pin.title,
            mediaUrls = mediaUrls,
            startPage = startPage,
            onDismiss = { fullScreenPhotoIndex = null }
        )
    }
}

@Composable
private fun FullScreenPhotoGallery(
    title: String,
    mediaUrls: List<String>,
    startPage: Int,
    onDismiss: () -> Unit
) {
    val pagerState = rememberPagerState(
        initialPage = startPage.coerceIn(0, mediaUrls.lastIndex),
        pageCount = { mediaUrls.size }
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                AsyncImage(
                    model = mediaUrls[page],
                    contentDescription = "$title full photo ${page + 1}",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlassIconButton(
                    icon = Icons.Default.Close,
                    contentDescription = "Close photo",
                    onClick = onDismiss
                )
                if (mediaUrls.size > 1) {
                    MediaCounter(
                        currentPage = pagerState.currentPage,
                        pageCount = mediaUrls.size
                    )
                }
            }
        }
    }
}

// ─── Shared UI ────────────────────────────────────────────────────────────────

@Composable
private fun GalleryNavigation(
    currentPage: Int,
    pageCount: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        GlassIconButton(
            icon = Icons.Default.ChevronLeft,
            contentDescription = "Previous photo",
            onClick = onPrevious,
            enabled = currentPage > 0
        )
        GlassIconButton(
            icon = Icons.Default.ChevronRight,
            contentDescription = "Next photo",
            onClick = onNext,
            enabled = currentPage < pageCount - 1
        )
    }
}

@Composable
private fun MediaCounter(
    currentPage: Int,
    pageCount: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(999.dp),
        color = GlassPanel,
        border = BorderStroke(1.dp, LimePop.copy(alpha = 0.55f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${currentPage + 1} / $pageCount",
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = LimePop
                )
            )
        }
    }
}

@Composable
private fun GlassIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Surface(
        shape = CircleShape,
        color = if (enabled) GlassPanel else GlassPanel.copy(alpha = 0.45f),
        border = BorderStroke(1.dp, if (enabled) SoftOutline else SoftOutline.copy(alpha = 0.45f))
    ) {
        IconButton(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier.size(44.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = if (enabled) InkText else MutedText.copy(alpha = 0.42f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun PinHeader(pin: PinCard) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Bubblegum,
                    modifier = Modifier.size(15.dp)
                )
                Text(
                    text = pin.province,
                    style = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.4.sp,
                        color = Bubblegum
                    )
                )
            }
            Text(
                text = pin.timeAgo,
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MutedText
                )
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.08f),
                border = BorderStroke(1.dp, SoftOutline)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = LimePop,
                    modifier = Modifier
                        .padding(7.dp)
                        .size(15.dp)
                )
            }
            Text(
                text = "Pinned by ${pin.author}",
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MutedText
                )
            )
        }
    }
}

@Composable
private fun StatsRow(pin: PinCard, localVotes: Int, liveStillExistsPct: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatCard(
            value = "$localVotes",
            label = "saves",
            accent = LimePop,
            icon = Icons.Default.KeyboardArrowUp,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            value = "${pin.stillExistsPct}%",
            label = "still there",
            accent = if (pin.stillExistsPct >= 80) ElectricBlue else Bubblegum,
            icon = Icons.Default.TravelExplore,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            value = pin.yearDiscovered,
            label = "found",
            accent = Bubblegum,
            icon = Icons.Default.LocationOn,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatCard(
    value: String,
    label: String,
    accent: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .background(Brush.verticalGradient(listOf(CardStart, CardEnd)))
            .border(1.dp, SoftOutline, RoundedCornerShape(22.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = accent, modifier = Modifier.size(17.dp))
        Text(
            text = value,
            style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 22.sp, fontWeight = FontWeight.Black, color = InkText),
            maxLines = 1
        )
        Text(
            text = label,
            style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MutedText),
            maxLines = 1
        )
    }
}

@Composable
private fun DetailPanel(
    eyebrow: String,
    title: String,
    accent: Color,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .background(Brush.linearGradient(listOf(CardStart, CardEnd)))
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(listOf(accent.copy(alpha = 0.48f), SoftOutline, Color.Transparent)),
                shape = RoundedCornerShape(26.dp)
            )
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = eyebrow,
            style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.2.sp, color = accent)
        )
        Text(
            text = title,
            style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 24.sp, lineHeight = 27.sp, fontWeight = FontWeight.Black, color = InkText)
        )
        content()
    }
}

@Composable
private fun LocationPanel(
    pin: PinCard,
    onOpenMaps: () -> Unit,
    onCopyCoords: () -> Unit
) {
    DetailPanel(eyebrow = "FIND IT", title = "Coordinates plus the local hint", accent = LimePop) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(132.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Brush.linearGradient(listOf(Color(0xFF111827), Color(0xFF1A202C), Color(0xFF0D1117))))
                .border(1.dp, SoftOutline, RoundedCornerShape(20.dp))
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(74.dp)
                    .clip(CircleShape)
                    .background(LimePop.copy(alpha = 0.12f))
                    .border(1.dp, LimePop.copy(alpha = 0.55f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = LimePop, modifier = Modifier.size(34.dp))
            }
            Text(
                text = "${pin.lat}, ${pin.lng}",
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 14.dp),
                style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = InkText)
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            DetailActionButton(text = "Open maps", icon = Icons.Default.Directions, accent = LimePop, filled = true, onClick = onOpenMaps, modifier = Modifier.weight(1f))
            DetailActionButton(text = "Copy coords", icon = Icons.Default.ContentCopy, accent = ElectricBlue, filled = false, onClick = onCopyCoords, modifier = Modifier.weight(1f))
        }

        if (pin.localDirections.isNotBlank()) {
            Text(
                text = pin.localDirections,
                style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 14.sp, lineHeight = 21.sp, fontWeight = FontWeight.Medium, color = MutedText)
            )
        }
    }
}

@Composable
private fun DetailActionButton(
    text: String,
    icon: ImageVector,
    accent: Color,
    filled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(46.dp),
        shape = RoundedCornerShape(16.dp),
        color = GlassPanel,
        border = BorderStroke(1.dp, accent.copy(alpha = 0.55f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = accent, modifier = Modifier.size(17.dp))
            Spacer(modifier = Modifier.width(7.dp))
            Text(text = text, style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = accent))
        }
    }
}

@Composable
private fun NeonCapsule(text: String, accent: Color) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = Color.White.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.58f))
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 0.7.sp, color = InkText)
        )
    }
}

@Composable
private fun BottomFoundBar(
    foundPressed: Boolean,
    hasConfirmed: Boolean,
    localVotes: Int,
    onFoundClick: () -> Unit,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier.fillMaxWidth(), color = Color.Transparent) {
        Column(
            modifier = Modifier
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color(0xEE0D1117), DeepIndigo)))
                .padding(horizontal = 20.dp, vertical = 14.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onFoundClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GlassPanel, contentColor = LimePop),
                border = BorderStroke(1.dp, LimePop.copy(alpha = 0.70f)),
                elevation = null
            ) {
                Icon(Icons.Default.KeyboardArrowUp, null, tint = LimePop, modifier = Modifier.size(19.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (foundPressed) "Saved to the hype list" else "I found this spot",
                    style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = LimePop)
                )
            }

            Button(
                onClick = onConfirmClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GlassPanel,
                    contentColor = if (hasConfirmed) ElectricBlue else MutedText
                ),
                border = BorderStroke(1.dp, if (hasConfirmed) ElectricBlue.copy(alpha = 0.70f) else MutedText.copy(alpha = 0.30f)),
                elevation = null
            ) {
                Icon(Icons.Default.TravelExplore, null, tint = if (hasConfirmed) ElectricBlue else MutedText, modifier = Modifier.size(17.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (hasConfirmed) "✓ Still there — confirmed!" else "This place still exists",
                    style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = if (hasConfirmed) ElectricBlue else MutedText)
                )
            }

            Text(
                text = "$localVotes people saved this find",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MutedText)
            )
        }
    }
}

fun openInMaps(context: Context, lat: Double, lng: Double, label: String) {
    val uri = Uri.parse("geo:$lat,$lng?q=$lat,$lng($label)")
    val intent = Intent(Intent.ACTION_VIEW, uri)
    intent.setPackage("com.google.android.apps.maps")
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    } else {
        val browserUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=$lat,$lng")
        context.startActivity(Intent(Intent.ACTION_VIEW, browserUri))
    }
}