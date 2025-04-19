package com.example.a2048_game

// Create an empty 4x4 board and place two random tiles on it
fun createEmptyBoard(): Array<IntArray> {
    val board = Array(4) { IntArray(4) } // 4 rows, each with 4 zeroes
    addRandomTile(board)
    addRandomTile(board)
    return board
}

// Add a random tile (2 or 4) to an empty spot on the board
fun addRandomTile(board: Array<IntArray>) {
    val empty = mutableListOf<Pair<Int, Int>>() // list of empty cells

    for (i in 0 until 4) {
        for (j in 0 until 4) {
            if (board[i][j] == 0) {
                empty.add(i to j)
            }
        }
    }

    if (empty.isNotEmpty()) {
        val (x, y) = empty.random()
        board[x][y] = if ((0..9).random() < 9) 2 else 4 // 90% chance 2, 10% chance 4
    }
}

// Move the whole board in a given direction
// Returns: new board, was it moved, and how many points were gained
fun moveBoard(
    board: Array<IntArray>,
    direction: Direction
): Triple<Array<IntArray>, Boolean, Int> {
    val newBoard = Array(4) { board[it].clone() } // Copy the board
    var moved = false
    var gained = 0

    // Rotate the board to reuse move-left logic
    repeat(
        when (direction) {
            Direction.LEFT -> 0
            Direction.UP -> 1
            Direction.RIGHT -> 2
            Direction.DOWN -> 3
        }
    ) {
        newBoard.rotateLeft()
    }

    // Move all rows to the left
    for (i in 0 until 4) {
        val (newRow, rowMoved, points) = moveRowLeftWithScore(newBoard[i])
        newBoard[i] = newRow
        if (rowMoved) moved = true
        gained += points
    }

    // Rotate the board back to original direction
    repeat(
        when (direction) {
            Direction.LEFT -> 0
            Direction.UP -> 3
            Direction.RIGHT -> 2
            Direction.DOWN -> 1
        }
    ) {
        newBoard.rotateLeft()
    }

    return Triple(newBoard, moved, gained)
}

// Move one row to the left and calculate points
fun moveRowLeftWithScore(row: IntArray): Triple<IntArray, Boolean, Int> {
    val newRow = IntArray(4)
    var last = 0
    var index = 0
    var moved = false
    var score = 0

    for (i in 0..3) {
        val value = row[i]
        if (value == 0) continue

        if (value == last) {
            newRow[index - 1] *= 2       // Merge tiles
            score += newRow[index - 1]   // Add score
            last = 0
            moved = true
        } else {
            newRow[index] = value
            if (i != index) moved = true // Means the tile was moved
            last = value
            index++
        }
    }

    return Triple(newRow, moved, score)
}

// Rotate the board counterclockwise
fun Array<IntArray>.rotateLeft() {
    val temp = Array(4) { IntArray(4) }
    for (i in 0..3) {
        for (j in 0..3) {
            temp[3 - j][i] = this[i][j]
        }
    }
    for (i in 0..3) this[i] = temp[i]
}

// Check if player has won (reached 2048)
fun checkWin(board: Array<IntArray>): Boolean {
    for (row in board) {
        if (row.contains(2048)) return true
    }
    return false
}

// Check if no moves are left → game over
fun isGameOver(board: Array<IntArray>): Boolean {
    for (i in 0 until 4) {
        for (j in 0 until 4) {
            if (board[i][j] == 0) return false // still empty cells
            if (i < 3 && board[i][j] == board[i + 1][j]) return false // vertical merge possible
            if (j < 3 && board[i][j] == board[i][j + 1]) return false // horizontal merge possible
        }
    }
    return true
}
