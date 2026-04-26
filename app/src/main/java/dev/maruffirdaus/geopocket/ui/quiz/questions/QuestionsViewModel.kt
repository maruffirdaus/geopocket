package dev.maruffirdaus.geopocket.ui.quiz.questions

import androidx.lifecycle.ViewModel
import dev.maruffirdaus.geopocket.domain.topic.Subtopic
import dev.maruffirdaus.geopocket.ui.common.model.AngleResult
import dev.maruffirdaus.geopocket.ui.common.model.SegmentResult
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class QuestionsViewModel(
    @InjectedParam private val subtopic: Subtopic,
    @InjectedParam private val segments: Map<String, SegmentResult>,
    @InjectedParam private val angles: Map<String, AngleResult>
) : ViewModel()