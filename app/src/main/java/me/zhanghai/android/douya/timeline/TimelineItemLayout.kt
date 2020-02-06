/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.timeline

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.zhanghai.android.douya.api.info.SizedImage
import me.zhanghai.android.douya.api.info.TimelineItem
import me.zhanghai.android.douya.api.info.VideoInfo
import me.zhanghai.android.douya.api.util.activityCompat
import me.zhanghai.android.douya.api.util.normalOrClosest
import me.zhanghai.android.douya.api.util.subtitleWithEntities
import me.zhanghai.android.douya.api.util.textWithEntities
import me.zhanghai.android.douya.api.util.textWithEntitiesAndParent
import me.zhanghai.android.douya.api.util.uriOrUrl
import me.zhanghai.android.douya.arch.EventLiveData
import me.zhanghai.android.douya.arch.ResumedLifecycleOwner
import me.zhanghai.android.douya.arch.mapDistinct
import me.zhanghai.android.douya.arch.valueCompat
import me.zhanghai.android.douya.databinding.TimelineItemLayoutBinding
import me.zhanghai.android.douya.link.UriHandler
import me.zhanghai.android.douya.ui.HorizontalImageAdapter
import me.zhanghai.android.douya.util.GutterItemDecoration
import me.zhanghai.android.douya.util.OnHorizontalScrollListener
import me.zhanghai.android.douya.util.dpToDimensionPixelSize
import me.zhanghai.android.douya.util.fadeInUnsafe
import me.zhanghai.android.douya.util.fadeOutUnsafe
import me.zhanghai.android.douya.util.layoutInflater
import me.zhanghai.android.douya.util.takeIfNotEmpty
import org.threeten.bp.ZonedDateTime

class TimelineItemLayout : ConstraintLayout {
    companion object {
        private const val IMAGE_RECYCLER_GUTTER_SIZE_DP = 2
    }

    private val lifecycleOwner = ResumedLifecycleOwner()

    private val binding = TimelineItemLayoutBinding.inflate(context.layoutInflater, this, true)

    private val imageAdapter = HorizontalImageAdapter()

    private val viewModel = ViewModel()

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr)

    init {
        binding.imageRecycler.apply {
            layoutManager = LinearLayoutManager(null, RecyclerView.HORIZONTAL, false)
            val gutterSize = context.dpToDimensionPixelSize(IMAGE_RECYCLER_GUTTER_SIZE_DP)
            addItemDecoration(GutterItemDecoration(gutterSize))
            itemAnimator = null
            adapter = imageAdapter
            addOnScrollListener(object : OnHorizontalScrollListener() {
                private var scrollingLeft = true

                override fun onScrolledLeft() {
                    if (imageAdapter.itemCount == 0 || scrollingLeft) {
                        return
                    }
                    scrollingLeft = true
                    binding.imageRecyclerDescriptionScrim.fadeInUnsafe()
                    binding.imageRecyclerDescriptionText.fadeInUnsafe()
                }

                override fun onScrolledRight() {
                    if (imageAdapter.itemCount == 0 || !scrollingLeft) {
                        return
                    }
                    scrollingLeft = false
                    binding.imageRecyclerDescriptionScrim.fadeOutUnsafe()
                    binding.imageRecyclerDescriptionText.fadeOutUnsafe()
                }
            })
        }

        binding.lifecycleOwner = lifecycleOwner
        binding.viewModel = viewModel
        viewModel.imageList.observe(lifecycleOwner) { imageAdapter.submitList(it) }
        viewModel.openUriEvent.observe(lifecycleOwner) { UriHandler.open(it, context) }
    }

    fun setImageRecyclerViewPool(imageRecyclerViewPool: RecyclerView.RecycledViewPool) {
        binding.imageRecycler.setRecycledViewPool(imageRecyclerViewPool)
    }

    fun setTimelineItem(timelineItem: TimelineItem?) {
        viewModel.setTimelineItem(timelineItem)
        binding.executePendingBindings()
    }

    class ViewModel {
        data class State(
            val avatarUrl: String,
            val author: String,
            val authorUri: String,
            val time: ZonedDateTime?,
            val activity: String,
            val hasText: Boolean,
            val text: (Context) -> CharSequence,
            val topic: String,
            val topicUri: String,
            val hasReshared: Boolean,
            val resharedDeleted: Boolean,
            val resharedAuthor: String,
            val resharedActivity: String,
            val resharedText: CharSequence,
            val resharedTopic: String,
            val resharedTopicUri: String,
            val resharedUri: String,
            val hasCard: Boolean,
            val cardOwner: String,
            val cardActivity: String,
            val cardImageUrl: String,
            val cardTitle: String,
            val cardText: CharSequence,
            val cardTopic: String,
            val cardTopicUri: String,
            val cardUri: String,
            val image: SizedImage?,
            val imageList: List<SizedImage>,
            val video: VideoInfo?,
            val likeCount: Int,
            val commentCount: Int,
            val reshareCount: Int
        )

        private val state = MutableLiveData(
            State(
                avatarUrl = "",
                author = "",
                authorUri = "",
                time = null,
                activity = "",
                hasText = false,
                text = { "" },
                topic = "",
                topicUri = "",
                hasReshared = false,
                resharedDeleted = false,
                resharedAuthor = "",
                resharedActivity = "",
                resharedText = "",
                resharedTopic = "",
                resharedTopicUri = "",
                resharedUri = "",
                hasCard = false,
                cardOwner = "",
                cardActivity = "",
                cardImageUrl = "",
                cardTitle = "",
                cardText = "",
                cardTopic = "",
                cardTopicUri = "",
                cardUri = "",
                image = null,
                imageList = emptyList(),
                video = null,
                likeCount = 0,
                commentCount = 0,
                reshareCount = 0
            )
        )
        val avatarUrl = state.mapDistinct { it.avatarUrl }
        val author = state.mapDistinct { it.author }
        val time = state.mapDistinct { it.time }
        val activity = state.mapDistinct { it.activity }
        val hasText = state.mapDistinct { it.hasText }
        val text = state.mapDistinct { it.text }
        val topic = state.mapDistinct { it.topic }
        val hasReshared = state.mapDistinct { it.hasReshared }
        val resharedDeleted = state.mapDistinct { it.resharedDeleted }
        val resharedAuthor = state.mapDistinct { it.resharedAuthor }
        val resharedActivity = state.mapDistinct { it.resharedActivity }
        val resharedText = state.mapDistinct { it.resharedText }
        val resharedTopic = state.mapDistinct { it.resharedTopic }
        val hasCard = state.mapDistinct { it.hasCard }
        val cardOwner = state.mapDistinct { it.cardOwner }
        val cardActivity = state.mapDistinct { it.cardActivity }
        val cardImageUrl = state.mapDistinct { it.cardImageUrl }
        val cardTitle = state.mapDistinct { it.cardTitle }
        val cardText = state.mapDistinct { it.cardText }
        val cardTopic = state.mapDistinct { it.cardTopic }
        val image = state.mapDistinct { it.image }
        val imageList = state.mapDistinct { it.imageList }
        val video = state.mapDistinct { it.video }
        val likeCount = state.mapDistinct { it.likeCount }
        val commentCount = state.mapDistinct { it.commentCount }
        val reshareCount = state.mapDistinct { it.reshareCount }

        private val _openUriEvent = EventLiveData<String>()
        val openUriEvent: LiveData<String> = _openUriEvent

        fun setTimelineItem(timelineItem: TimelineItem?) {
            val status = timelineItem?.content?.status
            state.value = if (status != null) {
                val contentStatus = status.resharedStatus ?: status
                val card = contentStatus.card
                val images = card?.imageBlock?.images?.takeIfNotEmpty()?.map { it.image!! }
                    ?: contentStatus.images
                val video = contentStatus.videoInfo
                State(
                    avatarUrl = status.author?.avatar ?: "",
                    author = status.author?.name ?: "",
                    authorUri = status.author?.uriOrUrl ?: "",
                    activity = status.activityCompat,
                    time = status.createTime,
                    hasText = status.text.isNotEmpty(),
                    text = status::textWithEntitiesAndParent,
                    topic = status.topic?.name ?: "",
                    topicUri = status.topic?.uriOrUrl ?: "",
                    hasReshared = status.resharedStatus != null,
                    resharedDeleted = status.resharedStatus?.deleted ?: false,
                    resharedAuthor = status.resharedStatus?.author?.name ?: "",
                    resharedActivity = status.resharedStatus?.activityCompat ?: "",
                    resharedText = status.resharedStatus?.textWithEntities ?: "",
                    resharedTopic = status.resharedStatus?.topic?.name ?: "",
                    resharedTopicUri = status.resharedStatus?.topic?.uriOrUrl ?: "",
                    resharedUri = status.resharedStatus?.uri ?: "",
                    hasCard = card != null,
                    cardOwner = card?.ownerName ?: "",
                    cardActivity = card?.activity ?: "",
                    cardImageUrl = card?.image?.normalOrClosest?.url?.takeIf { images.isEmpty() }
                        ?: "",
                    cardTitle = card?.title ?: "",
                    cardText = card?.subtitleWithEntities?.takeIfNotEmpty() ?: card?.url ?: "",
                    cardTopic = card?.topic?.name ?: "",
                    cardTopicUri = card?.topic?.uriOrUrl ?: "",
                    cardUri = card?.uriOrUrl ?: "",
                    image = images.singleOrNull()?.takeIf { video == null },
                    imageList = images.takeIf { video == null && it.size > 1 } ?: emptyList(),
                    video = video,
                    likeCount = status.likeCount,
                    commentCount = status.commentsCount,
                    reshareCount = status.resharesCount
                )
            } else {
                val images = timelineItem?.content?.photos?.ifEmpty {
                    timelineItem.content.photo?.let { listOf(it) }
                }?.map { it.image!! } ?: emptyList()
                val video = timelineItem?.content?.videoInfo
                State(
                    avatarUrl = timelineItem?.owner?.avatar ?: "",
                    author = timelineItem?.owner?.name ?: "",
                    authorUri = timelineItem?.owner?.uriOrUrl ?: "",
                    activity = timelineItem?.action ?: "",
                    time = null,
                    hasText = false,
                    text = { "" },
                    topic = "",
                    topicUri = "",
                    hasReshared = false,
                    resharedDeleted = false,
                    resharedAuthor = "",
                    resharedActivity = "",
                    resharedText = "",
                    resharedTopic = "",
                    resharedTopicUri = "",
                    resharedUri = "",
                    hasCard = true,
                    cardOwner = "",
                    cardActivity = "",
                    cardImageUrl = images.singleOrNull()?.normalOrClosest?.url ?: "",
                    cardTitle = timelineItem?.content?.title ?: "",
                    cardText = timelineItem?.content?.abstractString ?: "",
                    cardTopic = timelineItem?.topic?.name ?: "",
                    cardTopicUri = timelineItem?.topic?.uriOrUrl ?: "",
                    cardUri = timelineItem?.content?.uriOrUrl ?: "",
                    image = null,
                    imageList = images.takeIf { video == null && it.size > 1 } ?: emptyList(),
                    video = video,
                    likeCount = timelineItem?.reactionsCount ?: 0,
                    commentCount = timelineItem?.commentsCount ?: 0,
                    reshareCount = timelineItem?.resharesCount ?: 0
                )
            }
        }

        fun openAuthor() {
            state.valueCompat.authorUri.takeIfNotEmpty()?.let { _openUriEvent.value = it }
        }

        fun openTopic() {
            state.valueCompat.topicUri.takeIfNotEmpty()?.let { _openUriEvent.value = it }
        }

        fun openReshared() {
            state.valueCompat.resharedUri.takeIfNotEmpty()?.let { _openUriEvent.value = it }
        }

        fun openResharedTopic() {
            state.valueCompat.resharedTopicUri.takeIfNotEmpty()?.let { _openUriEvent.value = it }
        }

        fun openCard() {
            state.valueCompat.cardUri.takeIfNotEmpty()?.let { _openUriEvent.value = it }
        }

        fun openCardTopic() {
            state.valueCompat.cardTopicUri.takeIfNotEmpty()?.let { _openUriEvent.value = it }
        }
    }
}
