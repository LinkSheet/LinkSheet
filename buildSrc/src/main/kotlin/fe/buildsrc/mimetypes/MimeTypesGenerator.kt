package fe.buildsrc.mimetypes

import com.squareup.javapoet.JavaFile
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.WildcardTypeName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.buildCodeBlock
import com.squareup.kotlinpoet.withIndent
import javax.lang.model.element.Modifier

object MimeTypesGenerator {
    private val EXTENSION_MAP_TYPE = Map::class.asClassName().parameterizedBy(
        String::class.asClassName(),
        Array::class.asClassName().parameterizedBy(String::class.asClassName())
    )

    private fun createArrayOf(values: Collection<String>): CodeBlock? {
        if (values.isEmpty()) {
            return null
//            return CodeBlock.of("emptyArray()")
        }
        val format = values.joinToString(separator = ", ", transform = { "%S" })
        return CodeBlock.of("arrayOf($format)", *values.toTypedArray())
    }

    private fun MimeType.toPairCodeBlock(): CodeBlock? {
        val array = createArrayOf(globPatterns) ?: return null

        return buildCodeBlock {
            add("%S to ", type)
            add(array)
        }
    }

    fun build(
        packageName: String,
        mimeTypes: List<MimeType>
    ): FileSpec {
        val codeBlock = CodeBlock.builder()
            .beginControlFlow("lazy(mode = %T.SYNCHRONIZED)", LazyThreadSafetyMode::class.asTypeName())
            .add("mapOf(")
            .withIndent {
                for (it in mimeTypes) {
                    val block = it.toPairCodeBlock() ?: continue
                    add(block)
                    add(",")
                }
            }
            .add(")")
            .endControlFlow()
            .build()
        val extensionMap = PropertySpec
            .builder("MAP", EXTENSION_MAP_TYPE)
            .delegate(codeBlock)
            .build()

        val file = FileSpec.builder(packageName, "ApacheTikaMimeTypes")
            .addType(TypeSpec.objectBuilder("ApacheTikaMimeTypes").addProperty(extensionMap).build())
            .build()

        return file
    }
}
