package io.potatogun.gdxhelper.widget.skin;

import com.badlogic.gdx.graphics.g2d.NinePatch;

data class ProgressBarSkin(@JvmField val bar: NinePatch, @JvmField val smoothFill: NinePatch, @JvmField val chunkedFill: NinePatch = smoothFill);
