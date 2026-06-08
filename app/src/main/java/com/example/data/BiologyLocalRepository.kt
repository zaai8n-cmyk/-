package com.example.data

import com.example.data.db.BiologyDao
import com.example.data.db.BookmarkEntity
import com.example.data.db.QuizAttemptEntity
import kotlinx.coroutines.flow.Flow

class BiologyLocalRepository(private val dao: BiologyDao) {

    val allBookmarks: Flow<List<BookmarkEntity>> = dao.getAllBookmarks()
    val quizAttempts: Flow<List<QuizAttemptEntity>> = dao.getAllQuizAttempts()

    fun isBookmarkedFlow(category: String, itemId: String): Flow<Boolean> {
        return dao.isBookmarkedFlow(category, itemId)
    }

    suspend fun toggleBookmark(category: String, itemId: String, title: String, subtitle: String) {
        val exists = dao.isBookmarked(category, itemId)
        if (exists) {
            dao.deleteBookmarkById(category, itemId)
        } else {
            dao.insertBookmark(
                BookmarkEntity(
                    category = category,
                    itemId = itemId,
                    title = title,
                    subtitle = subtitle
                )
            )
        }
    }

    suspend fun saveQuizAttempt(score: Int, total: Int) {
        val pct = (score.toFloat() / total.toFloat()) * 100f
        dao.insertQuizAttempt(
            QuizAttemptEntity(
                score = score,
                total = total,
                percentage = pct
            )
        )
    }
}
