package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.data.db.BookmarkEntity
import com.example.data.db.QuizAttemptEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BiologyCompanionApp(viewModel: BiologyViewModel, modifier: Modifier = Modifier) {
    val selectedSection by viewModel.selectedSection.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            // Elegant signature header designed around the gold/navy theme of Neymar Ibn Al-Anbar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)),
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 18.dp)
                        .statusBarsPadding()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                titleDirectionArabic("الأحياء للبكالوريا"),
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.secondary,
                                    letterSpacing = 1.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.secondary)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    titleDirectionArabic("إشراف الأستاذ نيمار ابن الانبار"),
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = "قائمة الأحياء",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Embedded Search TextField if section is searchable
                    if (selectedSection != "الامتحانات" && selectedSection != "المفضلة" && selectedSection != "منوعات وفيتامينات") {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.updateSearchQuery(it) },
                            placeholder = {
                                Text(
                                    titleDirectionArabic("ابحث في المنهج... (مثلا: الكبد، البلازما)"),
                                    color = Color.White.copy(alpha = 0.6f)
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "بحث",
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "مسح البحث",
                                            tint = Color.White.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.secondary,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                                focusedContainerColor = Color.White.copy(alpha = 0.12f),
                                unfocusedContainerColor = Color.White.copy(alpha = 0.12f)
                            )
                        )
                    } else {
                        // Display beautiful gold horizontal separator for aesthetic completion
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp)
                                .background(MaterialTheme.colorScheme.secondary)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Horizontal navigation scrolling row features custom-pill buttons
            CategoryTabsRow(
                selectedSection = selectedSection,
                onSectionSelected = { section ->
                    viewModel.selectSection(section)
                    focusManager.clearFocus()
                }
            )

            // Animated Screen transition based on selected option
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                AnimatedContent(
                    targetState = selectedSection,
                    label = "AppScreenTransition"
                ) { target ->
                    when (target) {
                        "تعاريف" -> DefinitionsScreen(viewModel)
                        "تعاليل" -> ReasonExplainsScreen(viewModel)
                        "مقارنات" -> ComparisonsScreen(viewModel)
                        "موقع ووظيفة" -> OrganLocationsScreen(viewModel)
                        "أعراض وأمراض" -> DiseasesScreen(viewModel)
                        "مخططات" -> ProcessFlowsScreen(viewModel)
                        "منوعات وفيتامينات" -> MiscellaneousScreen()
                        "الامتحانات" -> QuizScreen(viewModel)
                        "المفضلة" -> BookmarksScreen(viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryTabsRow(
    selectedSection: String,
    onSectionSelected: (String) -> Unit
) {
    val sections = listOf(
        Pair("تعاريف", Icons.Default.Book),
        Pair("تعاليل", Icons.Default.HelpCenter),
        Pair("مقارنات", Icons.Default.Compare),
        Pair("موقع ووظيفة", Icons.Default.Map),
        Pair("أعراض وأمراض", Icons.Default.Healing),
        Pair("مخططات", Icons.Default.Timeline),
        Pair("منوعات وفيتامينات", Icons.Default.Fastfood),
        Pair("الامتحانات", Icons.Default.CheckCircle),
        Pair("المفضلة", Icons.Default.Favorite)
    )

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(sections) { item ->
            val isSelected = selectedSection == item.first
            val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
            val contentColor = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            val borderColor = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)

            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onSectionSelected(item.first) }
                    .border(1.dp, borderColor, RoundedCornerShape(12.dp)),
                color = backgroundColor,
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = item.second,
                        contentDescription = item.first,
                        tint = contentColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = item.first,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = contentColor
                        )
                    )
                }
            }
        }
    }
}

// 1. Definitions Screen UI
@Composable
fun DefinitionsScreen(viewModel: BiologyViewModel) {
    val list by viewModel.filteredDefinitions.collectAsStateWithLifecycle()

    if (list.isEmpty()) {
        EmptyStateView(text = "لا توجد نتائج مطابقة لبحثك في التعاريف.")
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(list, key = { it.id }) { item ->
                TermItemCard(
                    title = item.term,
                    content = item.definition,
                    category = "تعاريف",
                    itemId = item.id,
                    viewModel = viewModel
                )
            }
        }
    }
}

// 2. Reason Explains Screen UI with Flip / Spring animations
@Composable
fun ReasonExplainsScreen(viewModel: BiologyViewModel) {
    val list by viewModel.filteredReasonExplains.collectAsStateWithLifecycle()

    if (list.isEmpty()) {
        EmptyStateView(text = "لا توجد نتائج مطابقة لبحثك في التعاليل.")
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(list, key = { it.id }) { item ->
                ReasonItemCard(
                    question = item.question,
                    answer = item.answer,
                    itemId = item.id,
                    viewModel = viewModel
                )
            }
        }
    }
}

// 3. Comparisons screen displaying beautiful side-by-side or clean vertical columns
@Composable
fun ComparisonsScreen(viewModel: BiologyViewModel) {
    val list by viewModel.filteredComparisons.collectAsStateWithLifecycle()

    if (list.isEmpty()) {
        EmptyStateView(text = "لا توجد نتائج مطابقة لبحثك في المقارنات.")
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(list, key = { it.id }) { item ->
                ComparisonCard(item)
            }
        }
    }
}

// 4. Organ Location and Functions
@Composable
fun OrganLocationsScreen(viewModel: BiologyViewModel) {
    val list by viewModel.filteredOrganLocations.collectAsStateWithLifecycle()

    if (list.isEmpty()) {
        EmptyStateView(text = "لا توجد نتائج مطابقة لبحثك في الأعضاء.")
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(list, key = { it.id }) { item ->
                OrganLocationCard(viewModel, item)
            }
        }
    }
}

// 5. Diseases, causes and symptoms
@Composable
fun DiseasesScreen(viewModel: BiologyViewModel) {
    val list by viewModel.filteredDiseases.collectAsStateWithLifecycle()

    if (list.isEmpty()) {
        EmptyStateView(text = "لا توجد نتائج مطابقة لبحثك في الأمراض.")
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(list, key = { it.id }) { item ->
                DiseaseCard(item)
            }
        }
    }
}

// 6. Flows Screen UI
@Composable
fun ProcessFlowsScreen(viewModel: BiologyViewModel) {
    val list by viewModel.filteredFlows.collectAsStateWithLifecycle()

    if (list.isEmpty()) {
        EmptyStateView(text = "لا توجد نتائج مطابقة لمسارات التتبع.")
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(list, key = { it.id }) { item ->
                FlowCard(item)
            }
        }
    }
}

// 7. Miscellaneous Screen containing Vitamins and Blood Matcher interactive tool
@Composable
fun MiscellaneousScreen() {
    var expandedFoodInfo by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // Dynamic Blood Transfusion interactive compatibility tool (organizing Page 42 of PDF)
        item {
            InteractiveBloodCompatibilityTool()
        }

        // Essential biology topics: Importance of Water & Vitamins (organizing Page 41-42 of PDF)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalHospital,
                                contentDescription = "فيتامينات",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = titleDirectionArabic("الفيتامينات الأساسية ودورها الحيوي"),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val vitamins = listOf(
                        Triple("فيتامين C", "الحمضيات والخضروات", "مقاومة الأمراض والنزيف والسرطان"),
                        Triple("فيتامين D", "الكبد، الحليب، وأشعة الشمس", "سلامة وتكلس العظام والأسنان"),
                        Triple("فيتامين A", "الجزر، الطماطم والبيض", "سلامة الإبصار وقوة الجلد والأغشية"),
                        Triple("فيتامين B", "الحليب والبقوليات واللحوم", "تنظيم الأعصاب وصحة خلايا الدم")
                    )

                    vitamins.forEach { vit ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .background(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.03f),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = titleDirectionArabic(vit.first),
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                )
                                Text(
                                    text = titleDirectionArabic("المصادر: ${vit.second}"),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                            Text(
                                text = titleDirectionArabic(vit.third),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.tertiary
                                ),
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.End
                            )
                        }
                    }
                }
            }
        }

        // Biological Importance of water
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandedFoodInfo = !expandedFoodInfo }
                    .border(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.WaterDrop,
                                contentDescription = "الماء",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = titleDirectionArabic("أهمية الماء لجسم الإنسان"),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                        Icon(
                            imageVector = if (expandedFoodInfo) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = "عرض التفاصيل",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    if (expandedFoodInfo) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = titleDirectionArabic(
                                "- يشكل الماء حوالي 60% من وزن الجسم وهو الوسط الحيوي لنقل الغذاء.\n" +
                                "- يساعد في إذابة وتسهيل نقل الفضلات خارج الجسم كلياً.\n" +
                                "- يؤدي نقصه الشديد إلى جفاف حاد وفشل وظائف الكلى بالكامل.\n" +
                                "- يزداد الاحتياج إليه بشكل حرج في الأيام الحارة عِند بذل الجهد."
                            ),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                lineHeight = 20.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )
                        )
                    }
                }
            }
        }
    }
}

// Interactive blood donors matcher organizing the biology chart of page 42
@Composable
fun InteractiveBloodCompatibilityTool() {
    val bloodTypes = listOf("A", "B", "AB", "O")
    var selectedDonor by remember { mutableStateOf("O") }
    var selectedRecipient by remember { mutableStateOf("A") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = titleDirectionArabic("مُنظم ومُطابق فصائل الدم"),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Text(
                text = titleDirectionArabic("تحقق فورياً من توافق المتبرع والمستلم طبياً"),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Donor selection row
            Text(
                text = titleDirectionArabic("فصيلة دم المتبرع (الواهب):"),
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                bloodTypes.forEach { type ->
                    val isSelected = selectedDonor == type
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.2f))
                            .clickable { selectedDonor = type },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = type,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Recipient selection row
            Text(
                text = titleDirectionArabic("فصيلة دم المستلم (المريض):"),
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                bloodTypes.forEach { type ->
                    val isSelected = selectedRecipient == type
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.3f))
                            .clickable { selectedRecipient = type },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = type,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Check logic
            val isCompatible = checkBloodCompatibility(selectedDonor, selectedRecipient)
            val comparisonText = if (isCompatible) "عملية نقل الدم آمنة ومتوافقة وممكنة ✔️" else "عملية نقل الدم غير آمنة وقد تؤدي للموت ❌"
            val barColor = if (isCompatible) Color(0xFF2E7D32) else MaterialTheme.colorScheme.tertiary

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = barColor.copy(alpha = 0.12f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, barColor.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isCompatible) Icons.Default.Favorite else Icons.Default.Warning,
                        contentDescription = "نتيجة الفحص",
                        tint = barColor,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = titleDirectionArabic(comparisonText),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = barColor
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

fun checkBloodCompatibility(donor: String, recipient: String): Boolean {
    if (donor == "O") return true // O universal donor
    if (recipient == "AB") return true // AB universal recipient
    if (donor == recipient) return true
    if (donor == "A" && recipient == "AB") return true
    if (donor == "B" && recipient == "AB") return true
    return false
}


// 8. Quiz Practice UI - Complete with historic scores and questions
@Composable
fun QuizScreen(viewModel: BiologyViewModel) {
    val quizState by viewModel.quizState.collectAsStateWithLifecycle()
    val attempts by viewModel.quizAttempts.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        when (val state = quizState) {
            is QuizState.Idle -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Quiz,
                                    contentDescription = "اختبار الأحياء",
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(56.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = titleDirectionArabic("الاختبار الذكي الشامل"),
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = titleDirectionArabic("اختبار عشوائي منظم بالكامل من الملخص والتعاريف والتعاليل والأسئلة لتثبيت المعلومات قبل الامتحان."),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.8f),
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                Button(
                                    onClick = { viewModel.startQuiz() },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                ) {
                                    Text(
                                        text = titleDirectionArabic("ابدأ الاختبار الآن"),
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.onSecondary
                                        )
                                    )
                                }
                            }
                        }
                    }

                    if (attempts.isNotEmpty()) {
                        item {
                            Text(
                                text = titleDirectionArabic("سجل العلامات والامتحانات السابقة:"),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }

                        items(attempts) { item ->
                            QuizAttemptRow(item)
                        }
                    }
                }
            }

            is QuizState.Active -> {
                val question = viewModel.getActiveQuestion()
                if (question != null) {
                    ActiveQuizView(viewModel, state, question)
                }
            }

            is QuizState.Finished -> {
                FinishedQuizView(viewModel, state)
            }
        }
    }
}

@Composable
fun QuizAttemptRow(attempt: QuizAttemptEntity) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (attempt.percentage >= 60f) Color(0xFF2E7D32).copy(alpha = 0.15f) else MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (attempt.percentage >= 60f) Icons.Default.Check else Icons.Default.Close,
                        contentDescription = "حالة الامتحان",
                        tint = if (attempt.percentage >= 60f) Color(0xFF2E7D32) else MaterialTheme.colorScheme.tertiary
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = titleDirectionArabic("الدرجة: ${attempt.score} من ${attempt.total}"),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = titleDirectionArabic("التاريخ: ${formatTimestampDate(attempt.timestamp)}"),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
            Text(
                text = "${attempt.percentage.toInt()}%",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = if (attempt.percentage >= 60f) Color(0xFF2E7D32) else MaterialTheme.colorScheme.tertiary
                )
            )
        }
    }
}

@Composable
fun ActiveQuizView(
    viewModel: BiologyViewModel,
    state: QuizState.Active,
    question: QuizQuestion
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        // Progress bar indicators
        LinearProgressIndicator(
            progress = { (state.currentQuestionIndex + 1).toFloat() / state.totalQuestions.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = titleDirectionArabic("السؤال ${state.currentQuestionIndex + 1} من ${state.totalQuestions}"),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = titleDirectionArabic("الدرجة الحالية: ${state.score}"),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                // Display Question Category Type badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = titleDirectionArabic(question.type),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = titleDirectionArabic(question.questionText),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        lineHeight = 24.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Answer Area depends on question options/type
                if (question.type == "تصحيح العبارة") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = titleDirectionArabic("تفكر في العبارة واكتشف الخطأ ثم انقر للتحقق من الحل."),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.tertiary
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    val choices = if (question.isTrueFalse) listOf("صح", "خطأ") else question.options

                    choices.forEachIndexed { index, option ->
                        val isSelected = state.selectedOptionIndex == index
                        val optionBorderColor = when {
                            state.answerChecked && index == question.correctAnswerIndex -> Color(0xFF2E7D32)
                            state.answerChecked && isSelected -> MaterialTheme.colorScheme.tertiary
                            isSelected -> MaterialTheme.colorScheme.primary
                            else -> Color.LightGray.copy(alpha = 0.5f)
                        }

                        val containerColor = when {
                            state.answerChecked && index == question.correctAnswerIndex -> Color(0xFF2E7D32).copy(alpha = 0.08f)
                            state.answerChecked && isSelected -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.08f)
                            isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                            else -> MaterialTheme.colorScheme.surface
                        }

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.5.dp, optionBorderColor, RoundedCornerShape(12.dp))
                                .clickable(enabled = !state.answerChecked) { viewModel.selectOption(index) },
                            color = containerColor,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { viewModel.selectOption(index) },
                                    enabled = !state.answerChecked,
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = MaterialTheme.colorScheme.primary
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = titleDirectionArabic(option),
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.weight(1f)
                                )
                                if (state.answerChecked && index == question.correctAnswerIndex) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "صحيحة",
                                        tint = Color(0xFF2E7D32)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Checked feedback alert
        if (state.answerChecked) {
            val isAnswerCoreCorrect = state.isCorrect ?: false
            val checkedColor = if (isAnswerCoreCorrect) Color(0xFF2E7D32) else MaterialTheme.colorScheme.tertiary
            val checkedText = if (isAnswerCoreCorrect) "إجابة صحيحة وممتازة! أحسنت." else "إجابة خاطئة. انتبه للتصحيح أدناه."

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .border(1.dp, checkedColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                color = checkedColor.copy(alpha = 0.08f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isAnswerCoreCorrect) Icons.Default.Info else Icons.Default.Cancel,
                            contentDescription = "إفادة",
                            tint = checkedColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = titleDirectionArabic(checkedText),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = checkedColor
                            )
                        )
                    }

                    if (question.type == "تصحيح العبارة") {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = titleDirectionArabic("التصحيح العلمي: ${state.textCorrectionAnswer}"),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }
        }

        // Action control button
        val enableAction = state.selectedOptionIndex != null || question.type == "تصحيح العبارة" || state.answerChecked
        Button(
            onClick = {
                if (state.answerChecked) viewModel.nextQuizQuestion()
                else viewModel.checkQuizAnswer()
            },
            enabled = enableAction,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (state.answerChecked) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = titleDirectionArabic(if (state.answerChecked) "السؤال التالي" else "التحقق من الإجابة"),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (state.answerChecked) MaterialTheme.colorScheme.onSecondary else Color.White
                )
            )
        }
    }
}

@Composable
fun FinishedQuizView(viewModel: BiologyViewModel, state: QuizState.Finished) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
            .border(1.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = "كأس الانجاز",
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(72.dp)
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = titleDirectionArabic("تهانينا! لقد أنهيت الاختبار"),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black)
            )
            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "${state.percentage.toInt()}%",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Black,
                    color = if (state.percentage >= 60f) Color(0xFF2E7D32) else MaterialTheme.colorScheme.tertiary
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = titleDirectionArabic("لقد أجبت بـ ${state.finalScore} إجابات صحيحة من إجمالي ${state.totalQuestions} سؤال."),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.resetQuiz() },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text(titleDirectionArabic("الرئيسية"))
                }

                Button(
                    onClick = { viewModel.startQuiz() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1.2f)
                        .height(48.dp)
                ) {
                    Text(titleDirectionArabic("إعادة الاختبار"))
                }
            }
        }
    }
}


// 9. Bookmarks Screen displaying saved items
@Composable
fun BookmarksScreen(viewModel: BiologyViewModel) {
    val list by viewModel.bookmarks.collectAsStateWithLifecycle()

    if (list.isEmpty()) {
        EmptyStateView(text = "قائمة المفضلة فارغة حالياً. اضغط على رمز القلب لتثبيت أي تعاريف أو تعاليل لحفظها هنا لسرعة المراجعة.")
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(list) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = titleDirectionArabic(item.category),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                )
                            }

                            IconButton(
                                onClick = { viewModel.toggleBookmark(item.category, item.itemId, item.title, item.subtitle) }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "حذف من المفضلة",
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = titleDirectionArabic(item.title),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = titleDirectionArabic(item.subtitle),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                lineHeight = 20.sp
                            )
                        )
                    }
                }
            }
        }
    }
}


// Reusable Cards components and Empty views
@Composable
fun EmptyStateView(text: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.LibraryBooks,
            contentDescription = "مخطوطة فارغة",
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
            modifier = Modifier.size(72.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = titleDirectionArabic(text),
            style = MaterialTheme.typography.bodyMedium.copy(
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            ),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun TermItemCard(
    title: String,
    content: String,
    category: String,
    itemId: String,
    viewModel: BiologyViewModel
) {
    val isFav by viewModel.isBookmarkedFlow(category, itemId).collectAsStateWithLifecycle(initialValue = false)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = titleDirectionArabic(title),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 0.5.sp
                    )
                )

                IconButton(
                    onClick = { viewModel.toggleBookmark(category, itemId, title, content) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "حفظ لللأهمية",
                        tint = if (isFav) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = titleDirectionArabic(content),
                style = MaterialTheme.typography.bodyMedium.copy(
                    lineHeight = 22.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                )
            )
        }
    }
}

@Composable
fun ReasonItemCard(
    question: String,
    answer: String,
    itemId: String,
    viewModel: BiologyViewModel
) {
    var expanded by remember { mutableStateOf(false) }
    val isFav by viewModel.isBookmarkedFlow("تعاليل", itemId).collectAsStateWithLifecycle(initialValue = false)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (expanded) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .clickable { expanded = !expanded }
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "علل",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondary,
                                fontSize = 9.sp
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = titleDirectionArabic(question),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.toggleBookmark("تعاليل", itemId, question, answer) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "حفظ للتعليل",
                            tint = if (isFav) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "عرض الإجابة النموذجية",
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    )
                }
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = titleDirectionArabic("الأجابة النموذجية:"),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = titleDirectionArabic(answer),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                lineHeight = 21.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ComparisonCard(item: Comparison) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CompareArrows,
                    contentDescription = "أيقونة مقارنة",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = titleDirectionArabic("مقارنة: " + item.title),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Comparison Matrix rendering (Dynamic column layouts)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = titleDirectionArabic(item.firstItemName),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(24.dp)
                        .background(Color.Gray.copy(alpha = 0.3f))
                )
                Text(
                    text = titleDirectionArabic(item.secondItemName),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.tertiary
                    ),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Compare criteria rows
            item.criteria.forEachIndexed { idx, criteriaName ->
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                    Text(
                        text = titleDirectionArabic("وجه المقارنة: $criteriaName"),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.02f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = titleDirectionArabic(item.firstItemDetails.getOrElse(idx) { "" }),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(8.dp),
                                lineHeight = 16.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                            color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.02f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = titleDirectionArabic(item.secondItemDetails.getOrElse(idx) { "" }),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(8.dp),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrganLocationCard(viewModel: BiologyViewModel, item: OrganLocationFunction) {
    val isFav by viewModel.isBookmarkedFlow("أعضاء", item.id).collectAsStateWithLifecycle(initialValue = false)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = titleDirectionArabic(item.name),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )

                IconButton(
                    onClick = { viewModel.toggleBookmark("أعضاء", item.id, item.name, "الموقع: ${item.location}\nالوظيفة: ${item.function}") },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "حفظ العضو مكرراً للسرعة",
                        tint = if (isFav) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.03f),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = titleDirectionArabic("الموقع في الجسم:"),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = titleDirectionArabic(item.location),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = titleDirectionArabic("الأهمية والوظيفة الطبية:"),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = titleDirectionArabic(item.function),
                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp)
                    )
                }
            }
        }
    }
}

@Composable
fun DiseaseCard(item: Disease) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (expanded) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.3f),
                RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .clickable { expanded = !expanded }
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "مرض ناقل",
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = titleDirectionArabic(item.name),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Text(
                            text = titleDirectionArabic("المسبب: " + item.cause),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "شاهد الأعراض والوقاية",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(16.dp))

                // Symptoms
                Text(
                    text = titleDirectionArabic("الأعراض المرضية:"),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                item.symptoms.forEach { symptom ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "• ",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = titleDirectionArabic(symptom),
                            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 18.sp),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Prevention & treatment
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color(0xFFE8F5E9), RoundedCornerShape(12.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = titleDirectionArabic("الوقاية السليمة:"),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32)
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        item.prevention.forEach { prev ->
                            Text(
                                text = titleDirectionArabic("- $prev"),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    lineHeight = 15.sp,
                                    color = Color(0xFF1B5E20)
                                ),
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = titleDirectionArabic("العلاج الموصى به:"),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        item.treatment.forEach { treat ->
                            Text(
                                text = titleDirectionArabic("- $treat"),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    lineHeight = 15.sp,
                                    color = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FlowCard(item: BiologyProcessFlow) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.PlaylistPlay,
                    contentDescription = "مخططات التتبع",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = titleDirectionArabic(item.title),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            item.steps.forEachIndexed { index, step ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (index + 1).toString(),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = titleDirectionArabic(step),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            lineHeight = 18.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                if (index < item.steps.lastIndex) {
                    Box(
                        modifier = Modifier
                            .padding(start = 13.dp)
                            .width(2.dp)
                            .height(14.dp)
                            .background(MaterialTheme.colorScheme.secondary)
                    )
                }
            }
        }
    }
}


// Layout directional helpers for pristine Arabic layout format aligning RTL safely
fun titleDirectionArabic(text: String): String {
    return text
}

fun formatTimestampDate(timestamp: Long): String {
    val date = java.util.Date(timestamp)
    val format = java.text.SimpleDateFormat("yyyy/MM/dd HH:mm", java.util.Locale.getDefault())
    return format.format(date)
}
