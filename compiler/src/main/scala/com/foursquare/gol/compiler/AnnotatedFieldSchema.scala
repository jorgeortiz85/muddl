package com.foursquare.gol

case class AnnotatedFieldSchema(schema: FieldSchema[_], longName: String) {
  def number: Int = schema.number
  def shortName: String = schema.shortName
  def baseTpe: String = schema.manifest.toString
  def tpe: String = if (schema.isOptional) "Option["+baseTpe+"]" else baseTpe
}
