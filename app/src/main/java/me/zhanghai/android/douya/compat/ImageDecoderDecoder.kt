/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.compat

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.drawable.AnimatedImageDrawable
import android.os.Build.VERSION_CODES.P
import androidx.annotation.RequiresApi
import androidx.core.graphics.decodeDrawable
import coil.bitmappool.BitmapPool
import coil.decode.DecodeResult
import coil.decode.DecodeUtils
import coil.decode.Decoder
import coil.decode.Options
import coil.drawable.ScaleDrawable
import coil.extension.repeatCount
import coil.size.PixelSize
import coil.size.Size
import okio.BufferedSource
import okio.sink
import kotlin.math.roundToInt

/**
 * Fixes [coil-kt/coil#269](https://github.com/coil-kt/coil/issues/269).
 */
@RequiresApi(P)
class ImageDecoderDecoder : Decoder {

    override fun handles(source: BufferedSource, mimeType: String?): Boolean {
        return DecodeUtils.isGif(source) || DecodeUtils.isAnimatedWebP(source)
    }

    override suspend fun decode(
        pool: BitmapPool,
        source: BufferedSource,
        size: Size,
        options: Options
    ): DecodeResult {
        val tempFile = createTempFile()

        try {
            var isSampled = false

            // Work around https://issuetracker.google.com/issues/139371066 by copying the source to a temp file.
            source.use { tempFile.sink().use { source.readAll(it) } }
            val decoderSource = ImageDecoder.createSource(tempFile)

            val baseDrawable = decoderSource.decodeDrawable { info, _ ->
                // It's safe to delete the temp file here.
                tempFile.delete()

                // Set the target size if the source image is larger than the target.
                if (size is PixelSize) {
                    val infoSize = info.size
                    val multiplier = DecodeUtils.computeSizeMultiplier(
                        srcWidth = infoSize.width,
                        srcHeight = infoSize.height,
                        destWidth = size.width,
                        destHeight = size.height,
                        scale = options.scale
                    )
                    if (multiplier < 1) {
                        isSampled = true
                        val targetWidth = (multiplier * infoSize.width).roundToInt()
                        val targetHeight = (multiplier * infoSize.height).roundToInt()
                        setTargetSize(targetWidth, targetHeight)
                    }
                }

                if (options.config != Bitmap.Config.HARDWARE) {
                    allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                }

                if (options.colorSpace != null) {
                    setTargetColorSpace(options.colorSpace)
                }

                memorySizePolicy = if (options.allowRgb565) {
                    ImageDecoder.MEMORY_POLICY_LOW_RAM
                } else {
                    ImageDecoder.MEMORY_POLICY_DEFAULT
                }
            }

            val drawable = if (baseDrawable is AnimatedImageDrawable) {
                baseDrawable.repeatCount = options.parameters.repeatCount() ?: AnimatedImageDrawable.REPEAT_INFINITE

                // Wrap AnimatedImageDrawable in a ScaleDrawable so it always scales to fill its bounds.
                ScaleDrawable(baseDrawable, options.scale)
            } else {
                baseDrawable
            }

            return DecodeResult(
                drawable = drawable,
                isSampled = isSampled
            )
        } finally {
            tempFile.delete()
        }
    }
}
