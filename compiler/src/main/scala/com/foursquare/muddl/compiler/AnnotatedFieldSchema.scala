package com.foursquare.muddl.compiler

/**
 * This class gives a full description of a record schema's field.
 */
case class AnnotatedFieldSchema(schema: FieldSchema[_], longName: String) {
  def number: Int = schema.number
  def shortName: String = schema.shortName
  def baseTpe: String = schema.manifest.toString
  def tpe: String = 
    if (schema.isRepeated)
      "Seq["+baseTpe+"]"
    else if (schema.isOptional)
      "Option["+baseTpe+"]"
    else
      baseTpe
}
