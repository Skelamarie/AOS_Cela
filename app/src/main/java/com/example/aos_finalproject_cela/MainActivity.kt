package com.example.aos_finalproject_cela

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- PREMIUM CLEAN DARK THEME (No aggressive green background) ---
private val CleanDarkColorScheme = darkColorScheme(
    primary = Color(0xFF81C784),          // Vibrant sage accent green
    onPrimary = Color(0xFF00330C),
    primaryContainer = Color(0xFF1B2E1E), // Subtle dark green tint ONLY for the dashboard card
    onPrimaryContainer = Color(0xFFE8F5E9),
    surface = Color(0xFF1E1E1E),          // Elegant dark slate grey for input containers
    onSurface = Color(0xFFFFFFFF),        // High contrast white text
    surfaceVariant = Color(0xFF252525),   // Secondary surfaces
    onSurfaceVariant = Color(0xFFB0B0B0), // Crisp grey for hints/labels
    background = Color(0xFF121212)        // CLEAN standard Android Dark Theme background
)

@Composable
fun DarkGreenTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CleanDarkColorScheme,
        content = content
    )
}

data class Expense(
    val description: String,
    val amount: Double,
    val category: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DarkGreenTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    GamifiedSpendingTrackerScreen(innerPadding)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamifiedSpendingTrackerScreen(innerPadding: PaddingValues) {
    var descriptionInput by remember { mutableStateOf("") }
    var amountInput by remember { mutableStateOf("") }
    var incomeInput by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Food") }
    var goalInput by remember { mutableStateOf("") }
    var budgetCap by remember { mutableStateOf(0.0) }

    var monthlyIncome by remember { mutableStateOf(0.0) }
    val expenseList = remember { mutableStateListOf<Expense>() }

    val categories = listOf("Food", "Transpo", "Bills", "Entertainment", "Others")

    val totalSpend = expenseList.sumOf { it.amount }
    val moneyOnHand = monthlyIncome - totalSpend

    val progress = if (budgetCap > 0) (totalSpend / budgetCap).toFloat().coerceIn(0f, 1f) else 0f

    val (statusTitle, statusBadge, badgeColor) = when {
        monthlyIncome == 0.0 -> Triple("Set income to start your quest!", "💤 Level 0: Onlooker", Color(0xFFB0B0B0))
        totalSpend == 0.0 -> Triple("Flawless run! No spending yet.", "🛡️ Level 1: Golden Shield", Color(0xFF64B5F6))
        totalSpend > budgetCap -> Triple("Danger zone! You breached your spending cap!", "🚨 Status: Overspent", Color(0xFFE57373))
        totalSpend > budgetCap * 0.8 -> Triple("Warning! Running low on remaining shield health.", "⚠️ Status: Critical", Color(0xFFFFF176))
        else -> Triple("Great job! You are hitting your monthly target.", "🏅 Level 2: Budget Warrior", Color(0xFF81C784))
    }

    val customTextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = Color(0xFF333333),
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- HEADER & GAME RANK ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🏆 Monthly Expense Meter",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = badgeColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = statusBadge,
                        color = badgeColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        fontSize = 12.sp
                    )
                }
            }
        }

        // --- DASHBOARD CARD ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Monthly Status Dashboard",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text(text = "Total Income", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = "₱${String.format("%.2f", monthlyIncome)}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                        }
                        Column {
                            Text(text = "Total Spent", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = "₱${String.format("%.2f", totalSpend)}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFFE57373))
                        }
                        Column {
                            Text(text = "Money on Hand", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = "₱${String.format("%.2f", moneyOnHand)}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF81C784))
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Budget Shield Health (Target Limit: ₱${String.format("%.0f", budgetCap)})",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                        color = if (progress > 0.8f) Color(0xFFE57373) else MaterialTheme.colorScheme.primary,
                        trackColor = Color(0xFF333333),
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = statusTitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        // --- INCOME & GOAL SETUP ROW ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Income Card Container
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedTextField(
                            value = incomeInput,
                            onValueChange = { incomeInput = it },
                            label = { Text("Income") },
                            modifier = Modifier.weight(1.0f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            colors = customTextFieldColors
                        )
                        Button(
                            onClick = {
                                val parsedIncome = incomeInput.toDoubleOrNull() ?: 0.0
                                if (parsedIncome >= 0) { monthlyIncome = parsedIncome; incomeInput = "" }
                            },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp)
                        ) {
                            Text("Set", fontSize = 12.sp)
                        }
                    }
                }

                // Budget Limit Card Container
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedTextField(
                            value = goalInput,
                            onValueChange = { goalInput = it },
                            label = { Text("Limit") },
                            modifier = Modifier.weight(1.0f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            colors = customTextFieldColors
                        )
                        Button(
                            onClick = {
                                val parsedGoal = goalInput.toDoubleOrNull() ?: 0.0
                                if (parsedGoal >= 0) { budgetCap = parsedGoal; goalInput = "" }
                            },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp)
                        ) {
                            Text("Set", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // --- ADD TRANSACTION FORM ---
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "⚔️ Log an Activity", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
        }

        item {
            OutlinedTextField(
                value = descriptionInput,
                onValueChange = { descriptionInput = it },
                label = { Text("What did you spend on?") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = customTextFieldColors
            )
        }

        item {
            OutlinedTextField(
                value = amountInput,
                onValueChange = { amountInput = it },
                label = { Text("Amount (₱)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                colors = customTextFieldColors
            )
        }

        // FIXED CHIP SYSTEM: Uses horizontal scrolling container so text never clips or wraps awkwardly
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Select Category", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text(category, fontWeight = FontWeight.Medium) },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }
            }
        }

        item {
            Button(
                onClick = {
                    val parsedAmount = amountInput.toDoubleOrNull() ?: 0.0
                    if (descriptionInput.isNotBlank() && parsedAmount > 0) {
                        expenseList.add(Expense(descriptionInput, parsedAmount, selectedCategory))
                        descriptionInput = ""
                        amountInput = ""
                    }
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Add Expense To Quest Log", fontWeight = FontWeight.Bold)
            }
        }

        // --- TRANSACTION LOG LIST ---
        item {
            HorizontalDivider(color = Color(0xFF252525), thickness = 1.dp)
            Text(text = "📜 Quest Log (History)", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
        }

        if (expenseList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().height(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "No trackings yet. Stay thrifty!", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            items(expenseList) { expense ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = expense.description, fontWeight = FontWeight.Bold, color = Color.White)
                            Text(text = expense.category, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(
                            text = "- ₱${String.format("%.2f", expense.amount)}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFFE57373)
                        )
                    }
                }
            }
        }
    }
}