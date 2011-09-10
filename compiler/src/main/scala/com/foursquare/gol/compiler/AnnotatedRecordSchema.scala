package com.foursquare.gol.compiler

case class AnnotatedRecordSchema(
  schema: RecordSchema,
  className: String,
  packageName: String,
  fields: Seq[AnnotatedFieldSchema]
)
