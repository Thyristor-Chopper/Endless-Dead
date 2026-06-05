package io.potatogun.gdxhelper.widget.skin;

import com.badlogic.gdx.graphics.g2d.NinePatch;

/**
 * 미터기(진행률 표시기)의 스킨이다.
 *
 * @param bar			미터기 틀의 9-patch 텍스처
 * @param smoothFill	smooth 스타일의 채움 9-patch 텍스처
 * @param chunkedFill	chunked 스타일의 채움 9-patch 텍스처
 */
data class ProgressBarSkin(@JvmField val bar: NinePatch, @JvmField val smoothFill: NinePatch, @JvmField val chunkedFill: NinePatch = smoothFill);
