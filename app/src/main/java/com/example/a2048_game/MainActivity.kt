package com.example.a2048_game

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a2048_game.ui.theme._2048_gameTheme
import kotlinx.coroutines.*

// Directions for player swipes
enum class Direction {
    UP, DOWN, LEFT, RIGHT
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // SharedPreferences to save best score and moves
        val prefs = getSharedPreferences("game_prefs", Context.MODE_PRIVATE)

        setContent {
            var isDarkTheme by remember { mutableStateOf(false) } // theme toggle

            _2048_gameTheme(darkTheme = isDarkTheme) {

                // Game state
                var board by remember { mutableStateOf(createEmptyBoard()) }
                var gameState by remember { mutableStateOf("") }
                var score by remember { mutableStateOf(0) }
                var moveCount by remember { mutableStateOf(0) }
                var bestScore by remember { mutableStateOf(prefs.getInt("best_score", 0)) }
                var bestMoves by remember { mutableStateOf(prefs.getInt("best_moves", 0)) }
                var isBusy by remember { mutableStateOf(false) } // blocks input for a short time
                val scope = rememberCoroutineScope()

                // Save best score and moves into SharedPreferences
                fun saveBestResults(score: Int, moves: Int) {
                    prefs.edit()
                        .putInt("best_score", score)
                        .putInt("best_moves", moves)
                        .apply()
                }

                // Start a new game
                fun resetGame() {
                    if (score > bestScore) {
                        bestScore = score
                        bestMoves = moveCount
                        saveBestResults(bestScore, bestMoves)
                    }
                    moveCount = 0
                    score = 0
                    board = createEmptyBoard()
                    gameState = ""
                }

                // Clear saved records
                fun clearRecords() {
                    bestScore = 0
                    bestMoves = 0
                    prefs.edit().clear().apply()
                }

                // Handle swipes
                fun move(direction: Direction) {
                    if (gameState == "Game over!") return

                    val (newBoard, moved, gainedScore) = moveBoard(board, direction)
                    score += gainedScore

                    if (moved) {
                        moveCount++
                        addRandomTile(newBoard)
                        board = newBoard

                        // remove "You win!" after next move
                        if (gameState == "You win!") {
                            gameState = ""
                        }

                        // show "You win!" only once
                        if (checkWin(newBoard) && gameState.isEmpty()) {
                            gameState = "You win!"
                        } else if (isGameOver(newBoard)) {
                            gameState = "Game over!"
                        }
                    }
                }

                // background changes with theme
                val backgroundColor = if (isDarkTheme) Color(0xFF1C1C1C) else Color(0xFFFAF8EF)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(backgroundColor)
                        .padding(top = 75.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Theme toggle button
                    Button(onClick = { isDarkTheme = !isDarkTheme }) {
                        Text(if (isDarkTheme) "🌞 Light" else "🌙 Dark")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Score and move counters
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Score: $score",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkTheme) Color.White else Color.Black
                        )
                        Text(
                            text = "Moves: $moveCount",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkTheme) Color.White else Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Game grid with swipe detection
                    Game2048Screen(
                        board = board,
                        onSwipe = { dir ->
                            if (!isBusy) {
                                isBusy = true
                                scope.launch {
                                    move(dir)
                                    delay(400L) // cooldown to prevent fast input
                                    isBusy = false
                                }
                            }
                        },
                        isDarkTheme = isDarkTheme
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Game status message (win or lose)
                    Text(
                        text = gameState,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkTheme) Color.White else Color.Black
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Restart button
                    Button(onClick = { resetGame() }) {
                        Text("Restart")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Reset best record button
                    Button(onClick = { clearRecords() }) {
                        Text("Reset Records")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Best score display
                    Text(
                        text = "Best Score: $bestScore  (${bestMoves} moves)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isDarkTheme) Color.White else Color.Black
                    )
                }
            }
        }
    }
}
