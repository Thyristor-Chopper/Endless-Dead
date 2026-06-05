package io.potatogun.gdxhelper.widget.skin;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;

/**
 * 버튼의 스킨이다.
 *
 * @param normal				기본 상태에서의 9-patch 텍스처
 * @param hover					마우스를 올렸을 때의 9-patch 텍스처
 * @param pressed				누르고 있는 동안의 9-patch 텍스처
 * @param disabled				비활성화된 단추의 9-patch 텍스처
 * @param captionColor			단추 글자 색
 * @param disabledCaptionColor	비활성화된 단추 글자 색
 */
data class ButtonSkin(@JvmField val normal: NinePatch, @JvmField val hover: NinePatch = normal, @JvmField val pressed: NinePatch = normal, @JvmField val disabled: NinePatch = normal, @JvmField val captionColor: Color = Color.BLACK, @JvmField val disabledCaptionColor: Color = Color.GRAY);
