package io.potatogun.gdxhelper.widget.skin;

import com.badlogic.gdx.graphics.g2d.NinePatch;

data class ButtonSkin(@JvmField val normal: NinePatch, @JvmField val hover: NinePatch = normal, @JvmField val pressed: NinePatch = normal, @JvmField val disabled: NinePatch = normal);
