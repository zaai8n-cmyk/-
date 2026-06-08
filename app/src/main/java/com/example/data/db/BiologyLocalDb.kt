package com.example.data.db

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "bookmarks", primaryKeys = ["category", "itemId"])
data class BookmarkEntity(
    val category: String, // "تعاريف", "تعاليل", "أعضاء"
    val itemId: String,
    val title: String,
    val subtitle: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "quiz_attempts")
data class QuizAttemptEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val percentage: Float,
    val score: Int,
    val total: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface BiologyDao {
    @Query("SELECT * FROM bookmarks ORDER BY timestamp DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE category = :category AND itemId = :itemId)")
    suspend fun isBookmarked(category: String, itemId: String): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE category = :category AND itemId = :itemId)")
    fun isBookmarkedFlow(category: String, itemId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity)

    @Delete
    suspend fun deleteBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE category = :category AND itemId = :itemId")
    suspend fun deleteBookmarkById(category: String, itemId: String)

    @Query("SELECT * FROM quiz_attempts ORDER BY timestamp DESC")
    fun getAllQuizAttempts(): Flow<List<QuizAttemptEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizAttempt(attempt: QuizAttemptEntity)
}

@Database(entities = [BookmarkEntity::class, QuizAttemptEntity::class], version = 1, exportSchema = false)
abstract class BiologyDatabase : RoomDatabase() {
    abstract fun dao(): BiologyDao

    companion object {
        @Volatile
        private var INSTANCE: BiologyDatabase? = null

        fun getDatabase(context: Context): BiologyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BiologyDatabase::class.java,
                    "biology_ney_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
