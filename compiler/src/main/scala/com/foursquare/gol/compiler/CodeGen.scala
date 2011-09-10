package com.foursquare.gol.compiler

case class CodeGenResult(annotatedSchema: AnnotatedRecordSchema, code: String)

object CodeGen {
  def codeGen(schema: RecordSchema): CodeGenResult = {
    val annotated = Reflection.annotateRecord(schema)
    import annotated.{packageName}

    val gens =
      Vector(
        genBaseTrait _,
        genMetaObject _,
        genStrictClass _,
        // genLazyClass _,
        genMutableClass _,
        // genDecoratorClass _,
        genDeserializerClass _)
    val code = 
      (<template>
package {packageName}

import com.foursquare.gol.{{Record, Field, Deserializer, MetaRecord, MutableRecord, FieldMutation}}

{gens.map(f => f(annotated)).mkString("\n\n")}
      </template>).text.trim
    CodeGenResult(annotated, code)
  }

  def genBaseTrait(annotated: AnnotatedRecordSchema): String = {
    import annotated.{schema, fields, packageName, className}

    val traitFields = 
      fields.map({ field =>
        (<template>
  def {field.longName} : {field.tpe}
        </template>).text.trim
      })
    
    (<template>
trait {className} extends Record[{className}] {{
  override def meta = {className}

  {traitFields.mkString("\n  ").trim}

  override def canEqual(that: Any): Boolean = that.isInstanceOf[{className}]
}}
    </template>).text.trim
  }

  def genMetaObject(annotated: AnnotatedRecordSchema): String = {
    import annotated.{schema, fields, packageName, className}

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

    (<template>
object {className} extends MetaRecord[{className}] {{
  {metaFields.mkString("\n\n  ").trim}

  override val fields: Seq[Field[{className}, _]] = Vector(
    {fieldNames.mkString(",\n    ").trim}
  )
}}
    </template>).text.trim
  }

  def genMutableClass(annotated: AnnotatedRecordSchema): String = {
    import annotated.{schema, fields, packageName, className}

    val mutableFields =
      fields.map({ field =>
        (<template>
  override def {field.longName} : {field.tpe} =
    _mutations.get({className}.{field.longName}).map(_.newValue.asInstanceOf[{field.tpe}]).getOrElse(underlying.{field.longName})
  def {field.longName}_=(new{field.longName}: {field.tpe}): Unit =
    _mutations.put({className}.{field.longName}, FieldMutation(this.{field.longName}, new{field.longName}))
        </template>).text.trim
      })

    (<template>
class {className}Mutable protected (underlying: {className}) extends {className} with MutableRecord[{className}] {{
  {mutableFields.mkString("\n  ").trim}
}}
    </template>).text.trim
  }

  def genDeserializeField(decl: String, className: String)(field: AnnotatedFieldSchema): String = {
    val tpeName = field.baseTpe.replace('.', '$')

    val deserializeMethod =
      if (field.schema.isRepeated)
        "deserializeRepeated"
      else if (field.schema.isOptional)
        "deserializeOptional"
      else
        "deserializeRequired"

    (<template>
  override {decl} {field.longName} : {field.tpe} =
    d.{deserializeMethod}(obj, {className}.{field.longName}, d.deserialize${tpeName} _)
    </template>).text.trim
  }

  def genStrictClass(annotated: AnnotatedRecordSchema): String = {
    import annotated.{schema, fields, packageName, className}

    val strictFields = fields.map(genDeserializeField("val", className) _)

    (<template>
class {className}Strict[A] protected (obj: A, d: {className}Deserializer {{ type Obj = A }}) extends {className} {{
  {strictFields.mkString("\n  ").trim}
}}
    </template>).text.trim
  }

  def genLazyClass(annotated: AnnotatedRecordSchema): String = {
    import annotated.{schema, fields, packageName, className}

    val lazyFields = fields.map(genDeserializeField("lazy val", className) _)

    (<template>
class {className}Lazy[A] protected (obj: A, d: {className}Deserializer {{ type Obj = A }}) extends {className} {{
  {lazyFields.mkString("\n  ").trim}
}}
    </template>).text.trim
  }

  def genDecoratorClass(annotated: AnnotatedRecordSchema): String = {
    import annotated.{schema, fields, packageName, className}

    val decoratorFields =
      fields.map({ field =>
        (<template>
  override def {field.longName} : {field.tpe} = decorated.{field.longName}
        </template>).text.trim
      })

    (<template>
class {className}Decorator protected (decorated: {className}) extends {className} {{
  {decoratorFields.mkString("\n  ").trim}
}}
    </template>).text.trim
  }

  def genDeserializerClass(annotated: AnnotatedRecordSchema): String = {
    import annotated.{schema, fields, packageName, className}

    // TODO(jorge): HACK. Embeds between different packages will break.
    def isEmbeddedField(field: AnnotatedFieldSchema): Boolean =
      field.baseTpe.startsWith(packageName)

    def embeddedFieldName(field: AnnotatedFieldSchema): String =
      field.baseTpe.split('.').last

    val (embeddedFields, regularFields) = fields.partition(isEmbeddedField)

    val embeddedNames = embeddedFields.map(embeddedFieldName).distinct
    val embeddedNewMethodNames = className +: embeddedNames
    val selfType = embeddedNames.map(name => name+"Deserializer").mkString(" with ")

    val embeddedNewMethods =
      embeddedNewMethodNames.map({ name =>
        (<template>
  def new{name}(obj: Obj): Option[{name}]
        </template>).text.trim
      }).distinct

    val regularDeserializeMethods =
      regularFields.map({ field =>
        val tpeName = field.baseTpe.replace('.', '$')

        (<template>
  def deserialize${tpeName}(elem: Elem): Option[{field.baseTpe}]
        </template>).text.trim
      }).distinct

    val embeddedDeserializeMethods =
      embeddedFields.map({ field =>
        val tpeName = field.baseTpe.replace('.', '$')
        val name = embeddedFieldName(field)

        (<template>
  def deserialize${tpeName}(elem: Elem): Option[{field.baseTpe}] =
    deserializeObj(elem).flatMap(new{name})
        </template>).text.trim
      }).distinct

  val body =
    (<template>
  {embeddedNewMethods.mkString("\n  ")}

  {regularDeserializeMethods.mkString("\n  ")}
  {embeddedDeserializeMethods.mkString("\n  ")}
    </template>).text.trim

    (<template>
trait {className}Deserializer extends Deserializer {{ {if (!selfType.isEmpty) "self: " + selfType + " =>" else ""}
  {body}
}}
    </template>).text.trim
  }
}
