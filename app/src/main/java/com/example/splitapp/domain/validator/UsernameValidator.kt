package com.example.splitapp.domain.validator

import java.text.Normalizer

object UsernameValidator {
    private val VALID_REGEX = Regex("^[a-z0-9_]{3,15}$")

    fun validate(username: String): Result<Unit> {
        val normalized = normalize(username)
        if (!VALID_REGEX.matches(normalized))
            return Result.failure(Exception("Solo letras minúsculas, números y _ · entre 3 y 15 caracteres · sin espacios"))
        return Result.success(Unit)
    }

    fun normalize(username: String): String =
        Normalizer.normalize(
            username.lowercase().replace(" ", "").trim(),
            Normalizer.Form.NFKC
        )
}
