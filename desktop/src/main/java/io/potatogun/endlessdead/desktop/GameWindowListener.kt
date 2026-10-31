package io.potatogun.endlessdead.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowAdapter;

import io.potatogun.endlessdead.Constants;
import io.potatogun.endlessdead.EndlessDead;

import org.lwjgl.util.tinyfd.TinyFileDialogs;

class GameWindowListener(private val game: EndlessDead) : Lwjgl3WindowAdapter() {
	private var closeDialogShowing = false;

	override fun closeRequested(): Boolean {
		if(!game.gameManager.isPlaying && !game.gameManager.isPaused && !game.gameManager.isGameOver)
			return true;

		closeDialogShowing = true;
		val result = TinyFileDialogs.tinyfd_messageBox(Constants.GAME_TITLE, "Are you sure you want to quit ${Constants.GAME_TITLE}?  ", "yesno", "question", true);
		return result;
	}

	override fun focusGained() {
		if(closeDialogShowing)
			closeDialogShowing = false;
	}

	override fun focusLost() {
		if(game.gameManager.isPlaying && !closeDialogShowing)
			game.gameManager.pause();

		if(closeDialogShowing)
			closeDialogShowing = false;
	}
}
