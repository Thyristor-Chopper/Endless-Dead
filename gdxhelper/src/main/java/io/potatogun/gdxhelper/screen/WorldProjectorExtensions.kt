@file:JvmName("WorldProjectorUtils")
package io.potatogun.gdxhelper.screen;

import com.badlogic.gdx.graphics.Color;

inline fun WorldProjector.drawSubtitles(message: String) {
	(this as? SubtitlesDrawable)?.drawSubtitles(message);
}

inline fun WorldProjector.drawSubtitles(message: String, duration: Float) {
	(this as? SubtitlesDrawable)?.drawSubtitles(message, duration);
}

inline fun WorldProjector.drawSubtitles(message: String, color: Color) {
	(this as? SubtitlesDrawable)?.drawSubtitles(message, color);
}

inline fun WorldProjector.drawSubtitles(message: String, duration: Float, color: Color) {
	(this as? SubtitlesDrawable)?.drawSubtitles(message, duration, color);
}
