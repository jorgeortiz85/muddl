package com.foursquare.gol

case class CodeGenResult(annotatedSchema: AnnotatedRecordSchema, code: String)

object CodeGen {
  def codeGen(schema: RecordSchema): CodeGenResult = {
    val annotated = Reflection.annotateRecord(schema)
    val fields = annotated.fields
    val packageName = annotated.packageName
    val className = annotated.className

    val traitFields = 
      fields.map({ field =>
        (<template>
  def {field.longName} : {field.tpe}
        </template>).text.trim
      })

    val mutableFields =
      fields.map({ field =>
        (<template>
  override def {field.longName} : {field.tpe} =
    _mutations.get({className}.{field.longName}).map(_.newValue.asInstanceOf[{field.tpe}]).getOrElse(underlying.{field.longName})
  def {field.longName}_=(new{field.longName}: {field.tpe}): Unit =
    _mutations.put({className}.{field.longName}, FieldMutation(this.{field.longName}, new{field.longName}))
        </template>).text.trim
      })

    val metaFields =
      fields.map({ field =>
        (<template>
  val {field.longName} : Field[{className}, {field.tpe}] =
    Field[{className}, {field.tpe}](
      _.{field.longName},
      {field.number},
      "{field.shortName}",
      "{field.longName}",
      {field.schema.isOptional},
      manifest[{field.tpe}])
        </template>).text.trim
      })

    val fieldNames =
      fields.map(f => className + "." + f.longName)

    val strictFields =
      fields.map({ field =>
        (<template>
  override val {field.longName} : {field.tpe} =
    serializer.deserialize(source, {className}.{field.longName})
        </template>).text.trim
      })

    val lazyFields =
      strictFields.map(str => str.patch(str.indexOfSlice("val "), "lazy ", 0))

    val decoratorFields =
      fields.map({ field =>
        (<template>
  override def {field.longName} : {field.tpe} = decorated.{field.longName}
        </template>).text.trim
      })

    val serializerPatterns =
      fields.map({ field =>
        val tpeName = field.baseTpe.replace('.', '$')
        (<template>
    case f @ {className}.{field.longName} => unsafeCastField(f, hooks.deserialize${tpeName}(source, f))
        </template>).text.trim
      })

    val serializerHookMethods =
      fields.map({ field =>
        val tpeName = field.baseTpe.replace('.', '$')
        (<template>
  def deserialize${tpeName}(source: Source, field: Field[_, _]): Option[{field.baseTpe}]
        </template>).text.trim
      }).distinct

    val code =
      (<template>
package {packageName}

import com.foursquare.gol.{{Record, Field, Serializer, MetaRecord, MutableRecord, FieldMutation}}

trait {className} extends Record[{className}] {{
  override def meta = {className}

  {traitFields.mkString("\n  ").trim}
}}

class {className}Mutable protected (underlying: {className}) extends {className} with MutableRecord[{className}] {{
  {mutableFields.mkString("\n  ").trim}
}}

object {className} extends MetaRecord[{className}] {{
  {metaFields.mkString("\n\n  ").trim}

  override val fields: Seq[Field[{className}, _]] = Vector(
    {fieldNames.mkString(",\n    ").trim}
  )
}}

class {className}Decorator protected (decorated: {className}) extends {className} {{
  {decoratorFields.mkString("\n  ").trim}
}}

class {className}Strict[Source] protected (source: Source, serializer: Serializer[{className}, Source]) extends {className} {{
  {strictFields.mkString("\n  ").trim}
}}

class {className}Lazy[Source] protected (source: Source, serializer: Serializer[{className}, Source]) extends {className} {{
  {lazyFields.mkString("\n  ").trim}
}}

class {className}Serializer[S] protected (hooks: {className}SerializerHooks {{ type Source = S }}) extends Serializer[{className}, S] {{
  // TODO(jorge): Do we need to be concerned about boxing?
  override def deserialize[F](source: S, field: Field[{className}, F]): F = field match {{
    {serializerPatterns.mkString("\n    ").trim}
  }}
}}

trait {className}SerializerHooks {{
  type Source

  {serializerHookMethods.mkString("\n  ").trim}
}}
      </template>).text.trim

    CodeGenResult(annotated, code)
  }
}
