package com.example.lab09

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage

// Colores del tema
val DarkBg = Color(0xFF1A1A2E)
val CardBg = Color(0xFF16213E)
val AccentOrange = Color(0xFFFF6B35)
val AccentAmber = Color(0xFFFFB347)
val TextPrimary = Color(0xFFF5F5F5)
val TextSecondary = Color(0xFFAAAAAA)

fun difficultyColor(difficulty: String): Color = when (difficulty.lowercase()) {
    "easy" -> Color(0xFF4CAF50)
    "medium" -> Color(0xFFFF9800)
    else -> Color(0xFFF44336)
}

@Composable
fun ScreenRecipes(navController: NavHostController, servicio: RecipeApiService) {
    val listaRecipes: SnapshotStateList<RecipeModel> = remember { mutableStateListOf() }
    LaunchedEffect(Unit) {
        val response = servicio.getRecipes()
        response.recipes.forEach { listaRecipes.add(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "🍽️ Recetas del Mundo",
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Descubre sabores increíbles",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(listaRecipes) { recipe ->
                RecipeCard(recipe = recipe, onClick = {
                    navController.navigate("recipeVer/${recipe.id}")
                })
            }
        }
    }
}

@Composable
fun RecipeCard(recipe: RecipeModel, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box {
            // Imagen
            AsyncImage(
                model = recipe.image,
                contentDescription = recipe.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            )
            // Gradiente sobre imagen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color(0xCC000000)),
                            startY = 80f
                        )
                    )
            )
            // Badge dificultad
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .background(difficultyColor(recipe.difficulty), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(recipe.difficulty, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            // Nombre sobre imagen
            Text(
                text = recipe.name,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Info debajo de imagen
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rating
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Star, contentDescription = null, tint = AccentAmber, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("${recipe.rating}", color = AccentAmber, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(" (${recipe.reviewCount})", color = TextSecondary, fontSize = 12.sp)
            }
            // Cuisine
            Box(
                modifier = Modifier
                    .background(AccentOrange.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(recipe.cuisine, color = AccentOrange, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
            // Tiempo
            Text(
                "${recipe.prepTimeMinutes + recipe.cookTimeMinutes} min",
                color = TextSecondary,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun ScreenRecipe(navController: NavHostController, servicio: RecipeApiService, id: Int) {
    var recipe by remember { mutableStateOf<RecipeModel?>(null) }
    LaunchedEffect(Unit) {
        recipe = servicio.getRecipeById(id)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        if (recipe != null) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    // Imagen hero
                    Box {
                        AsyncImage(
                            model = recipe!!.image,
                            contentDescription = recipe!!.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(280.dp)
                        )
                        // Gradiente inferior
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(280.dp)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, DarkBg),
                                        startY = 150f
                                    )
                                )
                        )
                        // Botón back
                        IconButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier
                                .padding(12.dp)
                                .background(Color(0x88000000), RoundedCornerShape(50))
                        ) {
                            Icon(Icons.Outlined.ArrowBack, contentDescription = "Volver", tint = Color.White)
                        }
                    }
                }

                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Nombre
                        Text(
                            text = recipe!!.name,
                            color = TextPrimary,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(4.dp))
                        // Cuisine + MealType
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .background(AccentOrange, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(recipe!!.cuisine, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            recipe!!.mealType.forEach { tipo ->
                                Box(
                                    modifier = Modifier
                                        .background(CardBg, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(tipo, color = TextSecondary, fontSize = 12.sp)
                                }
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        // Stats row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatChip("🔥", "${recipe!!.caloriesPerServing}", "kcal")
                            StatChip("⏱️", "${recipe!!.prepTimeMinutes + recipe!!.cookTimeMinutes}", "min")
                            StatChip("🍽️", "${recipe!!.servings}", "porciones")
                            StatChip("⭐", "${recipe!!.rating}", "(${recipe!!.reviewCount})")
                        }

                        Spacer(Modifier.height(24.dp))

                        // Ingredientes
                        Text("Ingredientes", color = AccentOrange, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        recipe!!.ingredients.forEach { ingrediente ->
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(AccentOrange, RoundedCornerShape(50))
                                )
                                Spacer(Modifier.width(10.dp))
                                Text(ingrediente, color = TextPrimary, fontSize = 14.sp)
                            }
                        }

                        Spacer(Modifier.height(24.dp))

                        // Instrucciones
                        Text("Instrucciones", color = AccentOrange, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        recipe!!.instructions.forEachIndexed { index, paso ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .background(AccentOrange, RoundedCornerShape(50)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "${index + 1}",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(Modifier.width(12.dp))
                                Text(paso, color = TextPrimary, fontSize = 14.sp, modifier = Modifier.weight(1f))
                            }
                        }
                        Spacer(Modifier.height(24.dp))
                    }
                }
            }
        } else {
            CircularProgressIndicator(
                color = AccentOrange,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
fun StatChip(emoji: String, valor: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(CardBg, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(emoji, fontSize = 20.sp)
        Text(valor, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Text(label, color = TextSecondary, fontSize = 11.sp)
    }
}