/*
 * Copyright 2010-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.lightTree.fir.modifier

import com.intellij.lang.LighterASTNode
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.expressions.impl.FirAbstractAnnotatedElement
import org.jetbrains.kotlin.fir.lightTree.fir.modifier.ModifierSets.VARIANCE_MODIFIER
import org.jetbrains.kotlin.types.Variance

class TypeProjectionModifier(
    session: FirSession,
    psi: PsiElement? = null,

    private var varianceModifier: VarianceModifier = VarianceModifier.INVARIANT
) : FirAbstractAnnotatedElement(session, psi) {
    fun addModifier(modifier: LighterASTNode) {
        val tokenType = modifier.tokenType
        when {
            VARIANCE_MODIFIER.contains(tokenType) -> this.varianceModifier = VarianceModifier.valueOf(modifier.toString().toUpperCase())
        }
    }

    fun getVariance(): Variance {
        return varianceModifier.toVariance()
    }
}
