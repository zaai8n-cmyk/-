package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.data.db.BookmarkEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface QuizState {
    object Idle : QuizState
    data class Active(
        val currentQuestionIndex: Int,
        val selectedOptionIndex: Int?, // -1 or null means not selected yet
        val answerChecked: Boolean,
        val isCorrect: Boolean?,
        val score: Int,
        val totalQuestions: Int,
        val textCorrectionAnswer: String = "" // For text corrections
    ) : QuizState
    data class Finished(
        val finalScore: Int,
        val totalQuestions: Int,
        val percentage: Float
    ) : QuizState
}

class BiologyViewModel(private val repository: BiologyLocalRepository) : ViewModel() {

    // Main App Navigation / Category selection
    private val _selectedSection = MutableStateFlow("تعاريف")
    val selectedSection = _selectedSection.asStateFlow()

    // Search Query Flow
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    // Filtered Content Flows
    val filteredDefinitions = combine(searchQuery, flowOf(BiologyRepository.definitions)) { query, list ->
        if (query.isBlank()) list
        else list.filter { it.term.contains(query, ignoreCase = true) || it.definition.contains(query, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredReasonExplains = combine(searchQuery, flowOf(BiologyRepository.reasonExplains)) { query, list ->
        if (query.isBlank()) list
        else list.filter { it.question.contains(query, ignoreCase = true) || it.answer.contains(query, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredComparisons = combine(searchQuery, flowOf(BiologyRepository.comparisons)) { query, list ->
        if (query.isBlank()) list
        else list.filter { it.title.contains(query, ignoreCase = true) || it.firstItemName.contains(query, ignoreCase = true) || it.secondItemName.contains(query, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredOrganLocations = combine(searchQuery, flowOf(BiologyRepository.organLocationFunctions)) { query, list ->
        if (query.isBlank()) list
        else list.filter { it.name.contains(query, ignoreCase = true) || it.location.contains(query, ignoreCase = true) || it.function.contains(query, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredDiseases = combine(searchQuery, flowOf(BiologyRepository.diseases)) { query, list ->
        if (query.isBlank()) list
        else list.filter { it.name.contains(query, ignoreCase = true) || it.cause.contains(query, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredFlows = combine(searchQuery, flowOf(BiologyRepository.processFlows)) { query, list ->
        if (query.isBlank()) list
        else list.filter { it.title.contains(query, ignoreCase = true) || it.steps.any { step -> step.contains(query, ignoreCase = true) } }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Bookmarks loaded from Room DB
    val bookmarks: StateFlow<List<BookmarkEntity>> = repository.allBookmarks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Quiz flow parameters
    private val _quizState = MutableStateFlow<QuizState>(QuizState.Idle)
    val quizState = _quizState.asStateFlow()

    // Questions chosen for the current session
    private var activeQuestions: List<QuizQuestion> = emptyList()

    // Historical quiz scores
    val quizAttempts = repository.quizAttempts.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectSection(section: String) {
        _selectedSection.value = section
        // Clear search query upon switching sections for a cleaner UX
        _searchQuery.value = ""
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Toggle Favorite Action
    fun toggleBookmark(category: String, itemId: String, title: String, subtitle: String) {
        viewModelScope.launch {
            repository.toggleBookmark(category, itemId, title, subtitle)
        }
    }

    // Is current item bookmarked?
    fun isBookmarkedFlow(category: String, itemId: String): Flow<Boolean> {
        return repository.isBookmarkedFlow(category, itemId)
    }

    // Start Interactive Exam
    fun startQuiz() {
        // Randomize questions for a fresh exam every time!
        activeQuestions = BiologyRepository.quizQuestions.shuffled()
        _quizState.value = QuizState.Active(
            currentQuestionIndex = 0,
            selectedOptionIndex = null,
            answerChecked = false,
            isCorrect = null,
            score = 0,
            totalQuestions = activeQuestions.size
        )
    }

    // User select option
    fun selectOption(optionIndex: Int) {
        val current = _quizState.value
        if (current is QuizState.Active && !current.answerChecked) {
            _quizState.value = current.copy(selectedOptionIndex = optionIndex)
        }
    }

    // Checked selected option with correct answer
    fun checkQuizAnswer() {
        val current = _quizState.value
        if (current is QuizState.Active && !current.answerChecked) {
            val question = activeQuestions[current.currentQuestionIndex]
            val isCorrect = if (question.type == "تصحيح العبارة") {
                // If correction statement, user clicks to acknowledge spelling or verifies statement with text correction
                true
            } else {
                current.selectedOptionIndex == question.correctAnswerIndex
            }

            val newScore = if (isCorrect == true) current.score + 1 else current.score
            _quizState.value = current.copy(
                answerChecked = true,
                isCorrect = isCorrect,
                score = newScore,
                textCorrectionAnswer = if (question.type == "تصحيح العبارة") question.correctAnswerText else ""
            )
        }
    }

    // Advance to next question or complete exam
    fun nextQuizQuestion() {
        val current = _quizState.value
        if (current is QuizState.Active && current.answerChecked) {
            val nextIndex = current.currentQuestionIndex + 1
            if (nextIndex < current.totalQuestions) {
                _quizState.value = QuizState.Active(
                    currentQuestionIndex = nextIndex,
                    selectedOptionIndex = null,
                    answerChecked = false,
                    isCorrect = null,
                    score = current.score,
                    totalQuestions = current.totalQuestions
                )
            } else {
                // Done! Save results
                val finalPct = (current.score.toFloat() / current.totalQuestions.toFloat()) * 100f
                _quizState.value = QuizState.Finished(
                    finalScore = current.score,
                    totalQuestions = current.totalQuestions,
                    percentage = finalPct
                )
                viewModelScope.launch {
                    repository.saveQuizAttempt(current.score, current.totalQuestions)
                }
            }
        }
    }

    // Reset Quiz to Idle
    fun resetQuiz() {
        _quizState.value = QuizState.Idle
    }

    fun getActiveQuestion(): QuizQuestion? {
        val current = _quizState.value
        return if (current is QuizState.Active) {
            activeQuestions.getOrNull(current.currentQuestionIndex)
        } else {
            null
        }
    }
}

class BiologyViewModelFactory(private val repository: BiologyLocalRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BiologyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BiologyViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
