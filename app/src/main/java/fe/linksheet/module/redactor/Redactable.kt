package fe.linksheet.module.redactor

interface Redactable<T> {
    @Deprecated("Use buildString")
    fun process(builder: StringBuilder, redactor: Redactor): StringBuilder

     fun buildString(builder: ProtectedStringBuilder){}
//
//    fun bundle(): Map<String, Any?> {
//        return emptyMap()
//    }

//    fun values(map: ProtectMap){}

//    fun tryBundle(input: Any) {
//        Bundler.tryBundle()
//    }

//    open fun bundle()

    companion object {
//        fun dumpObject(prefix: String, builder: StringBuilder, hasher: LogHasher, obj: Any?): StringBuilder {
//            return builder.apply {
//                append(prefix)
//                dumpObject(this, hasher, obj)
//            }
//        }

//        fun dumpObject(stringBuilder: StringBuilder, hasher: LogHasher, obj: Any?): StringBuilder? {
//            if (obj == null) return stringBuilder
//
//            if (obj is String) return stringBuilder.append(obj)
//            if (obj is Boolean) return stringBuilder.append(obj)
//            if (obj is LogDumpable) return obj.redact(stringBuilder, hasher)
//
//            if (obj is List<*>) {
//                return stringBuilder.squareWrapped {
//                    obj.forEachWithInfo { element, _, _, last ->
//                        val result = dumpObject(this, hasher, element)
//                        if (result != null && !last) {
//                            this.append(",")
//                        }
//                    }
//                }
//            }
//
//            return when (obj) {
//                is Intent -> IntentDumpable.process(stringBuilder, obj, hasher)
//                is LibRedirectService -> LibRedirectServiceDumpable.process(stringBuilder, obj, hasher)
//                else -> null
//            }
//        }

//        fun <T> dumpObject(builder: StringBuilder, hasher: LogHasher, obj: T, processor: HashProcessor<T>): StringBuilder {
//            return hasher.hash(builder, obj, processor)
//        }
    }
}

//interface Redactable<T> {
//    fun process(builder: StringBuilder, instance: T, hasher: LogHasher): StringBuilder
//}


//object LibRedirectServiceDumpable : Redactable<LibRedirectService> {
//    override fun process(builder: StringBuilder, instance: LibRedirectService, hasher: LogHasher): StringBuilder {
//        return hasher.hash(builder, instance.key, HashProcessor.NoOpProcessor)
//    }
//}


//data class MapDumpable<K, V, DK, DV>(
//    val map: Map<K, V>,
//    val keyName: String,
//    val valueName: String,
//    val keySelector: (K) -> DK,
//    val valueSelector: (V) -> DV,
//    val keyProcessor: HashProcessor<DK>,
//    val valueProcessor: HashProcessor<DV>
//) : Redactable {
//    override fun process(builder: StringBuilder, hasher: LogHasher): StringBuilder {
//        return builder.commaSeparated {
//            map.forEach { (key, value) ->
//                item {
//                    curlyWrapped {
//                        hasher.hash(this, "$keyName=", keySelector(key), keyProcessor)
//                        append(",")
//                        hasher.hash(this, "$valueName=", valueSelector(value), valueProcessor)
//                    }
//                }
//            }
//        }
//    }
//}

//fun <K, V, DK, DV> Map<K, V>.toDumpable(
//    keyName: String,
//    valueName: String,
//    keySelector: (K) -> DK,
//    valueSelector: (V) -> DV,
//    keyProcessor: HashProcessor<DK>,
//    valueProcessor: HashProcessor<DV>
//) = MapDumpable(this, keyName, valueName, keySelector, valueSelector, keyProcessor, valueProcessor)
