package com.foursquare.muddl

sealed class DeserializationException(message: String) extends RuntimeException(message)
case class MissingRequiredFieldException(field: UntypedField) extends DeserializationException("Missing required field: " + field.shortName)
case class CouldntDeserializeException(field: UntypedField, value: Any) extends DeserializationException("Couldn't deserialize field: " + field.shortName + " (value: "+value+")")

/**
 * This trait defines the basics of deserialization. Deserializer implementations must support
 * Objects, Arrays, and Elements. They must also support required, optional, and repeated fields.
 */
trait Deserializer {
  type Obj
  type Arr
  type Elem

  def getObjElem(obj: Obj, field: Field[_, _]): Option[Elem]
  def getArrElems[T](arr: Arr, f: Elem => T): Seq[T]
  def deserializeArr(elem: Elem): Option[Arr]
  def deserializeObj(elem: Elem): Option[Obj]

  def deserializeRequired[F](obj: Obj, field: Field[_, F], f: Elem => Option[F]): F = {
    val elem = getObjElem(obj, field).getOrElse(throw MissingRequiredFieldException(field))
    val result = f(elem).getOrElse(throw CouldntDeserializeException(field, elem))
    result
  }

  def deserializeOptional[F](obj: Obj, field: Field[_, Option[F]], f: Elem => Option[F]): Option[F] = {
    val elemOpt = getObjElem(obj, field)
    val resultOpt = elemOpt.map(elem => f(elem).getOrElse(throw CouldntDeserializeException(field, elem)))
    resultOpt
  }

  def deserializeRepeated[F](obj: Obj, field: Field[_, Seq[F]], f: Elem => Option[F]): Seq[F] = {
    val elemOpt = getObjElem(obj, field)
    val arrOpt = elemOpt.map(elem => deserializeArr(elem).getOrElse(throw CouldntDeserializeException(field, elem)))
    val result = arrOpt.map(arr => getArrElems(arr, elem => f(elem).getOrElse(throw CouldntDeserializeException(field, elem)))).getOrElse(Nil)
    result
  }
}
