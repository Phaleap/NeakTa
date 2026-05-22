package com.example.neakta.ui.ranks

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.neakta.data.SessionManager
import com.example.neakta.model.ProvinceStatsResponse
import com.example.neakta.network.RetrofitClient
import com.example.neakta.ui.auth.Cinzel
import com.example.neakta.ui.core.AppLanguage
import com.example.neakta.ui.core.LocalAppLanguage
import com.example.neakta.ui.home.PinCard
import com.example.neakta.ui.home.PinViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private val InkText = Color(0xFFF7FAFC)
private val MutedText = Color(0xFFB8C2CC)
private val DeepBase = Color(0xFF0D1117)
private val CardBg = Color(0xE61A202C)
private val CardBgSoft = Color(0xCC111827)
private val Outline = Color(0x26FFFFFF)
private val Primary = Color(0xFF5FD3A6)
private val GlassPanel = Color(0x991A202C)

sealed class ProvinceRanksState {
    object Loading : ProvinceRanksState()
    data class Success(val stats: List<ProvinceStatsResponse>) : ProvinceRanksState()
    data class Error(val message: String) : ProvinceRanksState()
}

class ProvinceRanksViewModel(private val session: SessionManager) : ViewModel() {
    private val _state = MutableStateFlow<ProvinceRanksState>(ProvinceRanksState.Loading)
    val state: StateFlow<ProvinceRanksState> = _state

    init {
        fetchLeaderboard()
    }

    fun fetchLeaderboard() {
        viewModelScope.launch {
            _state.value = ProvinceRanksState.Loading
            try {
                val token = "Bearer ${session.getToken()}"
                val response = RetrofitClient.instance.getProvinceLeaderboard(token)
                if (response.isSuccessful) {
                    _state.value = ProvinceRanksState.Success(response.body().orEmpty())
                } else {
                    _state.value = ProvinceRanksState.Error("Failed to load province leaderboard")
                }
            } catch (e: Exception) {
                _state.value = ProvinceRanksState.Error(e.message ?: "Unknown error")
            }
        }
    }

    class Factory(private val session: SessionManager) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return ProvinceRanksViewModel(session) as T
        }
    }
}

@Composable
fun RanksScreen(
    onNavigateToProfile: () -> Unit = {},
    onPinClick: (PinCard) -> Unit = {},
    viewModel: PinViewModel
) {
    val context = LocalContext.current
    val ranksViewModel: ProvinceRanksViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = ProvinceRanksViewModel.Factory(SessionManager(context))
    )
    val state by ranksViewModel.state.collectAsState()
    val languageState = LocalAppLanguage.current
    val isKhmer = languageState.current == AppLanguage.KHMER

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF0D1117), Color(0xFF111827), DeepBase)))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .background(Brush.linearGradient(listOf(Primary.copy(alpha = 0.18f), Primary.copy(alpha = 0.05f), Color.Transparent)))
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { RanksHeader(onNavigateToProfile = onNavigateToProfile, isKhmer = isKhmer) }

            when (val current = state) {
                is ProvinceRanksState.Loading -> item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Primary)
                    }
                }

                is ProvinceRanksState.Error -> item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(36.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(current.message, style = TextStyle(color = MutedText, fontSize = 14.sp))
                        Surface(
                            onClick = { ranksViewModel.fetchLeaderboard() },
                            shape = RoundedCornerShape(999.dp),
                            color = GlassPanel,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Primary.copy(alpha = 0.55f))
                        ) {
                            Text(
                                text = if (isKhmer) "ព្យាយាមម្តងទៀត" else "Retry",
                                modifier = Modifier.padding(horizontal = 18.dp, vertical = 9.dp),
                                style = TextStyle(color = Primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }

                is ProvinceRanksState.Success -> {
                    val leaderboard = current.stats.sortedWith(
                        compareBy<ProvinceStatsResponse> { it.rank ?: Int.MAX_VALUE }
                            .thenByDescending { it.totalPins }
                            .thenByDescending { it.totalUpvotes }
                    )

                    item {
                        LeaderboardSummary(stats = leaderboard, isKhmer = isKhmer)
                    }

                    item {
                        Column(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = if (isKhmer) "តារាងខេត្ត" else "PROVINCE LEADERBOARD",
                                style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.2.sp, color = Primary)
                            )
                            Text(
                                text = if (isKhmer) "ខេត្តទាំងអស់\nប្រកួតដោយ gems និងការគាំទ្រ" else "All provinces,\nracing to preserve local gems",
                                style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 28.sp, lineHeight = 31.sp, fontWeight = FontWeight.Black, color = InkText)
                            )
                        }
                    }

                    if (leaderboard.isEmpty()) {
                        item { EmptyLeaderboard(isKhmer = isKhmer) }
                    } else {
                        itemsIndexed(leaderboard, key = { _, item -> item.id }) { index, item ->
                            ProvinceRankRow(
                                stat = item,
                                rank = item.rank ?: index + 1,
                                isKhmer = isKhmer
                            )
                            if (index < leaderboard.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 20.dp),
                                    thickness = 1.dp,
                                    color = Outline
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
            }
        }
    }
}

@Composable
private fun RanksHeader(onNavigateToProfile: () -> Unit, isKhmer: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = if (isKhmer) "ចំណាត់ថ្នាក់" else "RANKS",
                style = TextStyle(fontFamily = Cinzel, fontSize = 24.sp, letterSpacing = 5.sp, color = InkText)
            )
            Text(
                text = if (isKhmer) "មោទនភាពខេត្តទាំង ២៥" else "Friendly pride across all 25 provinces",
                style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = MutedText)
            )
        }
        Surface(shape = CircleShape, color = GlassPanel, border = androidx.compose.foundation.BorderStroke(1.dp, Outline)) {
            IconButton(onClick = onNavigateToProfile, modifier = Modifier.size(44.dp)) {
                Icon(Icons.Default.Person, contentDescription = "Profile", tint = InkText, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun LeaderboardSummary(stats: List<ProvinceStatsResponse>, isKhmer: Boolean) {
    val totalPins = stats.sumOf { it.totalPins }
    val totalUpvotes = stats.sumOf { it.totalUpvotes }
    val totalContributors = stats.sumOf { it.totalContributors }
    val leader = stats.firstOrNull()?.province?.let { if (isKhmer) it.nameKm else it.nameEn } ?: "-"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.horizontalGradient(listOf(CardBg, CardBgSoft)))
            .border(1.dp, Primary.copy(alpha = 0.32f), RoundedCornerShape(24.dp))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Default.Star, null, tint = Primary, modifier = Modifier.size(19.dp))
            Text(
                text = if (isKhmer) "ខេត្តនាំមុខ: $leader" else "Leading province: $leader",
                style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 16.sp, fontWeight = FontWeight.Black, color = InkText),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            SummaryCell(value = totalPins.toString(), label = if (isKhmer) "Gems" else "Gems", modifier = Modifier.weight(1f))
            SummaryCell(value = totalUpvotes.toString(), label = if (isKhmer) "ការគាំទ្រ" else "Upvotes", modifier = Modifier.weight(1f))
            SummaryCell(value = totalContributors.toString(), label = if (isKhmer) "អ្នកចូលរួម" else "Contributors", modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun SummaryCell(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(GlassPanel)
            .border(1.dp, Outline, RoundedCornerShape(18.dp))
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text(value, style = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Black, color = Primary))
        Text(label, style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MutedText), maxLines = 1)
    }
}

@Composable
private fun ProvinceRankRow(stat: ProvinceStatsResponse, rank: Int, isKhmer: Boolean) {
    val provinceName = if (isKhmer) stat.province.nameKm else stat.province.nameEn
    val rankColor = when (rank) {
        1 -> Color(0xFFFFD700)
        2 -> Color(0xFFC0C0C0)
        3 -> Color(0xFFCD7F32)
        else -> MutedText
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(CardBgSoft)
            .border(1.dp, Outline, RoundedCornerShape(22.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(rankColor.copy(alpha = if (rank <= 3) 0.18f else 0.08f))
                .border(1.dp, rankColor.copy(alpha = 0.55f), RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("#$rank", style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Black, color = rankColor))
        }

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                Icon(Icons.Default.LocationOn, null, tint = Primary, modifier = Modifier.size(14.dp))
                Text(
                    text = provinceName,
                    style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 18.sp, lineHeight = 21.sp, fontWeight = FontWeight.Black, color = InkText),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                MiniMetric(icon = Icons.Default.LocationOn, value = stat.totalPins.toString(), label = if (isKhmer) "gems" else "gems")
                MiniMetric(icon = Icons.Default.KeyboardArrowUp, value = stat.totalUpvotes.toString(), label = if (isKhmer) "upvotes" else "upvotes")
                MiniMetric(icon = Icons.Default.Groups, value = stat.totalContributors.toString(), label = if (isKhmer) "people" else "people")
            }
        }
    }
}

@Composable
private fun MiniMetric(icon: androidx.compose.ui.graphics.vector.ImageVector, value: String, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
        Icon(icon, null, tint = Primary, modifier = Modifier.size(13.dp))
        Text(value, style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Black, color = Primary))
        Text(label, style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MutedText))
    }
}

@Composable
private fun EmptyLeaderboard(isKhmer: Boolean) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(Icons.Default.LocationOn, null, tint = Primary, modifier = Modifier.size(34.dp))
        Text(
            text = if (isKhmer) "មិនទាន់មានចំណាត់ថ្នាក់" else "No province stats yet",
            style = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.Black, color = InkText)
        )
        Text(
            text = if (isKhmer) "ចុះ gem ដំបូងដើម្បីចាប់ផ្តើម" else "Drop the first gem to start the race.",
            style = TextStyle(fontSize = 13.sp, color = MutedText)
        )
    }
}
