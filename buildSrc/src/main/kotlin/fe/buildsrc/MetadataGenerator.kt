package fe.buildsrc

import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec
import javax.lang.model.element.Modifier
import kotlin.reflect.KClass

object MetadataClassGenerator {
    private fun createConstantField(clazz: KClass<*>, name: String, vararg value: Any?, format: String = "\$L"): FieldSpec {
        return FieldSpec.builder(clazz.java, name).constantField(value = value, format = format)
    }

    private fun FieldSpec.Builder.constantField(vararg value: Any?, format: String = "\$L"): FieldSpec {
        return addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
            .initializer(format, *value)
            .build()
    }

    fun build(
        fetchedAt: Long = System.currentTimeMillis(),
        gitHash: String,
        packageName: String = "fe.clearurlskt",
    ): JavaFile {
        val fetchedAtField = createConstantField(Long::class, "FETCHED_AT", "${fetchedAt}L")
        val gitHashField = createConstantField(String::class, "RULES_GIT_HASH", gitHash, format = "\$S")

        val typeSpec = TypeSpec.classBuilder("ClearURLsMetadata")
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addField(fetchedAtField)
            .addField(gitHashField)
            .build()

        return JavaFile.builder(packageName, typeSpec).build()
    }
}

