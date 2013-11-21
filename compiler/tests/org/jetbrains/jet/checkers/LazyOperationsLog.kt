/*
 * Copyright 2010-2014 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.jet.checkers

import org.jetbrains.jet.lang.resolve.name.FqName
import org.jetbrains.jet.lang.resolve.name.Name
import org.jetbrains.jet.lang.descriptors.Named
import java.util.IdentityHashMap
import org.jetbrains.jet.lang.resolve.scopes.JetScope
import org.jetbrains.jet.lang.resolve.java.structure.impl.JavaTypeImpl
import org.jetbrains.jet.lang.resolve.java.structure.impl.JavaClassImpl
import java.util.ArrayList
import org.jetbrains.jet.utils.Printer
import org.jetbrains.jet.lang.resolve.java.structure.JavaNamedElement
import org.jetbrains.jet.descriptors.serialization.ProtoBuf
import org.jetbrains.jet.descriptors.serialization.TypeDeserializer
import org.jetbrains.jet.descriptors.serialization.context.DeserializationContext
import org.jetbrains.jet.lang.types.JetType
import org.jetbrains.jet.lang.resolve.DescriptorUtils

private fun createObjectCounter(): (Any) -> Int {
    val ids = IdentityHashMap<Any, Int>()
    return {
        o -> ids.getOrPut(o, { ids.size() })
    }
}

class LazyOperationsLog(
        val stringSanitizer: (String) -> String
) {
    val ids = IdentityHashMap<Any, Int>()
    fun objectId(o: Any): Int = ids.getOrPut(o, { ids.size() })

    private class Record(
            val lambda: Any,
            val data: org.jetbrains.jet.checkers.LoggingStorageManager.CallData
    )

    private val records = ArrayList<Record>()

    public val addRecordFunction: (lambda: Any, data: org.jetbrains.jet.checkers.LoggingStorageManager.CallData) -> Unit = {
        lambda, data ->
        records.add(Record(lambda, data))
    }

    public fun getText(): String {
        val groupedByOwner = records.groupByTo(IdentityHashMap()) {
            val owner = it.data.fieldOwner
            if (owner is JetScope) owner.getContainingDeclaration() else owner
        }.map { Pair(it.getKey(), it.getValue()) }

        val sortedByOwner = groupedByOwner.map {
            val (owner, records) = it
            renderOwner(owner, records)
        }.sortBy(stringSanitizer)

        return sortedByOwner.join("\n")
    }

    private fun renderOwner(owner: Any?, records: List<Record>): String {
        val sb = StringBuilder()
        with (Printer(sb)) {
            println(toString(owner), " {")
            indent {
                records.map { renderRecord(it) }.sortBy(stringSanitizer).forEach {
                    println(it)
                }
            }
            println("}")
        }
        return sb.toString()
    }

    private fun renderRecord(record: Record): String {
        val data = record.data
        val sb = StringBuilder()

        sb.append(data.field?.getName() ?: "<name not found>")

        if (!data.arguments.isEmpty()) {
            sb.append(data.arguments.map { toString(it) }.join(", ", "(", ")"))
        }

        sb.append(" = ${toString(data.result)}")

        if (data.fieldOwner is JetScope) {
            sb.append(" // through ${toString(data.fieldOwner)}")
        }

        return sb.toString()
    }

    private fun toString(o: Any?): String {
        if (o == null) return "null"

        val sb = StringBuilder()
        if (o is FqName || o is Name) {
            sb.append("'$o'")
        }
        if (sb.length() != 0) {
            sb.append(": ")
        }

        val id = objectId(o)

        val aClass = o.javaClass
        sb.append(if (aClass.isAnonymousClass()) aClass.getName() else aClass.getSimpleName()).append("@$id")
        when {
            o is Named -> sb.append("['${o.getName()}']")
            o.javaClass.getSimpleName() == "LazyJavaClassifierType" -> {
                val javaType = o.field<JavaTypeImpl<*>>("javaType")
                sb.append("['${javaType.getPsi().getPresentableText()}']")
            }
            o.javaClass.getSimpleName() == "LazyJavaClassTypeConstructor" -> {
                val javaClass = o.field<Any>("this\$0").field<JavaClassImpl>("jClass")
                sb.append("['${javaClass.getPsi().getName()}']")
            }
            o.javaClass.getSimpleName() == "DeserializedType" -> {
                val typeDeserializer = o.field<TypeDeserializer>("this\$0")
                val context = typeDeserializer.field<DeserializationContext>("context")
                val typeProto = o.field<ProtoBuf.Type>("typeProto")
                val text = when (typeProto.getConstructor().getKind()) {
                    ProtoBuf.Type.Constructor.Kind.CLASS -> context.nameResolver.getFqName(typeProto.getConstructor().getId()).asString()
                    ProtoBuf.Type.Constructor.Kind.TYPE_PARAMETER -> {
                        val classifier = (o as JetType).getConstructor().getDeclarationDescriptor()
                        "" + classifier.getName() + " in " + DescriptorUtils.getFqName(classifier.getContainingDeclaration())
                    }
                    else -> "???"
                }
                sb.append("['$text']")
            }
            o is JavaNamedElement -> {
                sb.append("['${o.getName()}']")
            }
            o is JavaTypeImpl<*> -> {
                sb.append("['${o.getPsi().getPresentableText()}']")
            }
            o is Collection<*> -> {
                if (o.isEmpty()) {
                    sb.append("[empty]")
                }
                else {
                    val size = o.size()
                    sb.append("[$size] { ")
                    for ((i, item) in o.withIndices()) {
                        sb.append(toString(item))
                        if (i < size - 1) sb.append(", ")
                        if (i > 2) {
                            if (i < size - 1) sb.append("...")
                            break
                        }
                    }
                    sb.append(" }")
                }
            }
        }
        return sb.toString()
    }
}

private fun <T> Any.field(name: String): T {
    val field = this.javaClass.getDeclaredField(name)
    field.setAccessible(true)
    [suppress("UNCHECKED_CAST")]
    return field.get(this) as T
}

private fun Printer.indent(body: Printer.() -> Unit): Printer {
    pushIndent()
    body()
    popIndent()
    return this
}