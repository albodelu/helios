package helios.typeclasses

import arrow.core.Either
import helios.core.Json

sealed class DecodingError
data class StringDecodingError(val value: Json) : DecodingError()
data class BooleanDecodingError(val value: Json) : DecodingError()
data class NumberDecodingError(val value: Json) : DecodingError()
data class KeyNotFound(val name: String) : DecodingError()

interface Decoder<out A> {
  fun decode(value: Json): Either<DecodingError, A>
}