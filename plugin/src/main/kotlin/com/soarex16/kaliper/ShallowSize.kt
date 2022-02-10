package com.soarex16.kaliper

import arrow.meta.CliPlugin
import arrow.meta.Meta
import arrow.meta.invoke
import arrow.meta.quotes.Transform
import arrow.meta.quotes.classDeclaration
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtPropertyAccessor
import org.jetbrains.kotlin.psi.psiUtil.anyDescendantOfType
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.typeBinding.createTypeBindingForReturnType
import org.jetbrains.kotlin.types.KotlinType

val Meta.generateShallowSize: CliPlugin
    get() = "Generate shallowSize function" {
        meta(
            classDeclaration(this, { element.isData() }) {
                val className = this.name.toString()
                val packageName = value.fqName?.parent() ?: FqName.ROOT

                val bindingContext = ctx.bindingTrace?.bindingContext!!

                val fieldTypes = getClassFields(this.value, bindingContext)

                val dataClassSize = fieldTypes.sumOf { getTypeSize(it.second) }

                val packageString = if (packageName.isRoot) "" else "package $packageName\n"

                Transform.newSources("""
                    |$packageString
                    |fun ${className}.shallowSize(): Int = $dataClassSize
                """.trimMargin().file("${className}_shallowSize"))
            }
        )
    }

/**
 * A backing field will be generated for a property if it uses the default implementation
 * of at least one of the accessors, or if a custom accessor references it through the field identifier.
 */
fun KtProperty.hasBackingField() = this.accessors.size == 0 || this.accessors.any { it.refersFieldIdentifier() }

fun KtPropertyAccessor.refersFieldIdentifier() = anyDescendantOfType<KtNameReferenceExpression> { it.text == "field" }

fun getClassFields(
    klass: KtClass,
    ctx: BindingContext
): List<Pair<String, KotlinType?>> {
    val constructorParams = klass.primaryConstructor?.valueParameters
    val bodyParams = klass.getProperties().filter { it.hasBackingField() }

    val ctorTypesInfo = constructorParams?.map { Pair(it.name!!, it.createTypeBindingForReturnType(ctx)?.type) } ?: listOf()
    val bodyPropsTypeInfo = bodyParams.map { Pair(it.name!!, it.createTypeBindingForReturnType(ctx)?.type) }

    return ctorTypesInfo + bodyPropsTypeInfo
}
