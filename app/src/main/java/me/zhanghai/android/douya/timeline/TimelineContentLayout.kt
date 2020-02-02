/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.timeline

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LiveData
import me.zhanghai.android.douya.api.info.SizedImage
import me.zhanghai.android.douya.api.info.Status
import me.zhanghai.android.douya.api.util.normalOrClosest
import me.zhanghai.android.douya.api.util.subtitleWithEntities
import me.zhanghai.android.douya.api.util.textWithEntities
import me.zhanghai.android.douya.arch.DistinctMutableLiveData
import me.zhanghai.android.douya.arch.ResumedLifecycleOwner
import me.zhanghai.android.douya.databinding.TimelineContentLayoutBinding
import me.zhanghai.android.douya.link.UriHandler
import me.zhanghai.android.douya.util.layoutInflater
import org.threeten.bp.ZonedDateTime

class TimelineContentLayout : ConstraintLayout {

    private val binding = TimelineContentLayoutBinding.inflate(context.layoutInflater, this, true)

    val viewModel = ViewModel()

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr)

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        binding.lifecycleOwner = ResumedLifecycleOwner()
        binding.viewModel = viewModel
    }

    fun bind(status: Status) = viewModel.bind(status)

    inner class ViewModel {

        private val _avatar = DistinctMutableLiveData("")
        val avatar: LiveData<String> = _avatar

        private val _author = DistinctMutableLiveData("")
        val author: LiveData<String> = _author

        private val _time = DistinctMutableLiveData<ZonedDateTime?>(null)
        val time: LiveData<ZonedDateTime?> = _time

        private val _activity = DistinctMutableLiveData("")
        val activity: LiveData<String> = _activity

        private val _title = DistinctMutableLiveData("")
        val title: LiveData<String> = _title

        private val _text = DistinctMutableLiveData<CharSequence>("")
        val text: LiveData<CharSequence> = _text

        private val _hasReshared = DistinctMutableLiveData(false)
        val hasReshared: LiveData<Boolean> = _hasReshared

        private val _resharedDeleted = DistinctMutableLiveData(false)
        val resharedDeleted: LiveData<Boolean> = _resharedDeleted

        private val _resharedAuthor = DistinctMutableLiveData("")
        val resharedAuthor: LiveData<String> = _resharedAuthor

        private val _resharedActivity = DistinctMutableLiveData("")
        val resharedActivity: LiveData<String> = _resharedActivity

        private val _resharedText = DistinctMutableLiveData<CharSequence>("")
        val resharedText: LiveData<CharSequence> = _resharedText

        private val _hasCard = DistinctMutableLiveData(false)
        val hasCard: LiveData<Boolean> = _hasCard

        private val _cardOwner = DistinctMutableLiveData("")
        val cardOwner: LiveData<String> = _cardOwner

        private val _cardActivity = DistinctMutableLiveData("")
        val cardActivity: LiveData<String> = _cardActivity

        private val _cardImage = DistinctMutableLiveData("")
        val cardImage: LiveData<String> = _cardImage

        private val _cardTitle = DistinctMutableLiveData("")
        val cardTitle: LiveData<String> = _cardTitle

        private val _cardText = DistinctMutableLiveData<CharSequence>("")
        val cardText: LiveData<CharSequence> = _cardText

        var cardUri = ""

        private val _image = DistinctMutableLiveData<SizedImage?>(null)
        val image: LiveData<SizedImage?> = _image

        private val _imageCount = DistinctMutableLiveData(0)
        val imageCount: LiveData<Int> = _imageCount

        fun bind(status: Status) {
            _avatar.value = status.author!!.avatar
            _author.value = status.author.name
            _activity.value = status.activity
            _time.value = status.createTime
            _title.value = ""
            _text.value = status.textWithEntities
            _hasReshared.value = status.resharedStatus != null
            _resharedDeleted.value = status.resharedStatus?.deleted ?: false
            _resharedAuthor.value = status.resharedStatus?.author?.name ?: ""
            _resharedActivity.value = status.resharedStatus?.activity ?: ""
            _resharedText.value = status.resharedStatus?.textWithEntities ?: ""
            val contentStatus = status.resharedStatus ?: status
            val card = contentStatus.card
            _hasCard.value = card != null
            _cardOwner.value = card?.ownerName ?: ""
            _cardActivity.value = card?.activity ?: ""
            val images = card?.imageBlock?.images?.map { it.image!! }?.ifEmpty { null }
                ?: contentStatus.images
            _cardImage.value = if (images.isEmpty()) card?.image?.normalOrClosest?.url ?: "" else ""
            _cardTitle.value = card?.title ?: ""
            _cardText.value = card?.subtitleWithEntities?.ifEmpty { null } ?: card?.url ?: ""
            cardUri = card?.uri?.ifEmpty { null } ?: card?.url ?: ""
            _image.value = if (images.size == 1) images.first() else null
            _imageCount.value = images.size
        }

        fun open() {
            // TODO
        }

        fun openReshared() {
            // TODO
        }

        fun openCard() {
            if (cardUri.isNotEmpty()) {
                UriHandler.open(cardUri, context)
            }
        }
    }
}
