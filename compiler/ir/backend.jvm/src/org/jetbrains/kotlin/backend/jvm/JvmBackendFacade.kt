/*
 * Copyright 2010-2016 JetBrains s.r.o.
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

package org.jetbrains.kotlin.backend.jvm

import org.jetbrains.kotlin.backend.jvm.lower.JvmFileClassProvider
import org.jetbrains.kotlin.codegen.CompilationErrorHandler
import org.jetbrains.kotlin.codegen.state.GenerationState
import org.jetbrains.kotlin.progress.ProgressIndicatorAndCompilationCanceledStatus
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi2ir.Psi2IrTranslator
import org.jetbrains.kotlin.psi2ir.generators.GeneratorContext

object JvmBackendFacade {
    fun compileCorrectFiles(state: GenerationState, errorHandler: CompilationErrorHandler) {
        ProgressIndicatorAndCompilationCanceledStatus.checkCanceled()
        state.beforeCompile()
        ProgressIndicatorAndCompilationCanceledStatus.checkCanceled()
        doGenerateFiles(state.files, state, errorHandler)
    }

    fun doGenerateFiles(files: Collection<KtFile>, state: GenerationState, errorHandler: CompilationErrorHandler) {
        // TODO multifile classes support

        val psi2ir = Psi2IrTranslator()
        val psi2irContext = psi2ir.createGeneratorContext(state.module, state.bindingContext)
        val jvmBackendContext = createJvmBackendContext(psi2ir, state)

        val jvmBackend = JvmBackend(jvmBackendContext)

        val irModuleFragment = psi2ir.generateModuleFragment(psi2irContext, files)

        for (irFile in irModuleFragment.files) {
            try {
                jvmBackend.generateFile(irFile)
                state.afterIndependentPart()
            }
            catch (e: Throwable) {
                errorHandler.reportException(e, null) // TODO ktFile.virtualFile.url
            }
        }
    }

    private fun createJvmBackendContext(psi2ir: Psi2IrTranslator, state: GenerationState): JvmBackendContext {
        val jvmFileClassProvider = JvmFileClassProvider()
        psi2ir.add(jvmFileClassProvider)

        val jvmBackendContext = JvmBackendContext(state, jvmFileClassProvider)

        return jvmBackendContext
    }
}
