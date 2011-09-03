package com.foursquare.gol

case class AnnotatedRecordSchema(
  schema: RecordSchema,
  className: String,
  packageName: String,
  fields: Seq[AnnotatedFieldSchema]
)