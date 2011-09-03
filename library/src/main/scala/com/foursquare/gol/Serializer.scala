package com.foursquare.gol

trait Serializer[T <: Record[T], Source] {
  def deserialize[F](source: Source, field: Field[T, F]): F
  protected def unsafeCastField[F](field: Field[T, _], opt: Option[_]): F = {
    val result =
      if (field.optional)
        opt
      else
        opt.getOrElse(throw new Exception("Serialization error: field %s is required" format field.longName))
    result.asInstanceOf[F]
  }
}
