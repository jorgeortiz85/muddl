package com.foursquare.gol

class DeserializationException(message: String) extends RuntimeException(message)

trait Deserializer {
  def deserializeRequired[F](value: Option[F], field: Field[_, _]): F =
    value.getOrElse(throw new DeserializationException("Serialization error: field %s is required" format field.longName))
}
