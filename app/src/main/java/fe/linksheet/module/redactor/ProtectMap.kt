package fe.linksheet.module.redactor
//
//import android.content.ComponentName
//import android.content.Intent
//import android.content.pm.ActivityInfo
//import android.content.pm.ResolveInfo
//
class ProtectMap(val prefix: String?) {
//    fun put(key: String, sensitivePart: SensitivePart) {
//
//    }
//
    fun put(key: String, input: Any?) {
//        val nested = ProtectMap(key)
//
//        when (input) {
//            is ComponentName -> {
//                nested.put("package", Package(input.packageName))
//                nested.put("class", Clazz(input.className))
//            }
//
//            is ActivityInfo -> nested.put("package", Package(input.packageName))
//            is Intent -> {
//                nested.put("action", input.action)
//                nested.put("categories", input.categories)
//                nested.put("component", input.component)
//                nested.put("flags", input.flags)
//                nested.put("package", input.`package`?.let { Package(it) })
//
//            }
//
//            is ResolveInfo -> {
//                val packageName = if (input.activityInfo != null) {
//                    input.activityInfo.packageName
//                } else if (input.serviceInfo != null) {
//                    input.serviceInfo.packageName
//                } else if (input.providerInfo != null) {
//                    input.providerInfo.packageName
//                } else null
//
//                nested.put("package", packageName)
//            }
//
//            else -> {
//                error("Can't handle $key $input")
//            }
//        }
    }
}
