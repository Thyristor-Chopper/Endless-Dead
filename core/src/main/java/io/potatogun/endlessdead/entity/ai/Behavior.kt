package io.potatogun.endlessdead.entity.ai;

interface Behavior {
	fun update(delta: Float): Result;

	enum class Result {
		SUCCEEDED,
		FAILED,
		REJECTED;
	}
}
