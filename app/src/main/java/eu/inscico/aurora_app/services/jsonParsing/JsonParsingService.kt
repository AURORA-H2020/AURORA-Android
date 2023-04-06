package eu.inscico.aurora_app.services.jsonParsing

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type


interface JsonParsingService {

    val dateFormatPattern: String

    fun <T : Any> toJson(input: T): String

    fun <T : Any> fromJson(jsonString: String?, type: Type): T?

    data class MultipleTypeVariants(
        val variants: List<Type>
    )

}

abstract class TypeToken<T>

object TypeUtils {
    inline fun <reified T : Any> getType() = object : TypeToken<T>() {}::class
        .java
        .genericSuperclass
        .let { it as ParameterizedType }
        .actualTypeArguments
        .first()
}
