/*
 * Copyright 2010-2018 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.jvm.internal;

import kotlin.SinceKotlin;
import kotlin.reflect.KCallable;
import kotlin.reflect.KFunction;

@SuppressWarnings("rawtypes")
public class FunctionReference extends CallableReference implements FunctionBase, KFunction {
    private final int arity;

    public FunctionReference(int arity) {
        this.arity = arity;
    }

    @SinceKotlin(version = "1.1")
    public FunctionReference(int arity, Object receiver) {
        super(receiver);
        this.arity = arity;
    }

    @Override
    public int getArity() {
        return arity;
    }

    @Override
    @SinceKotlin(version = "1.1")
    protected KFunction getReflected() {
        return (KFunction) super.getReflected();
    }

    @Override
    @SinceKotlin(version = "1.1")
    protected KCallable computeReflected() {
        return Reflection.function(this);
    }

    @Override
    @SinceKotlin(version = "1.1")
    public boolean isInline() {
        return getReflected().isInline();
    }

    @Override
    @SinceKotlin(version = "1.1")
    public boolean isExternal() {
        return getReflected().isExternal();
    }

    @Override
    @SinceKotlin(version = "1.1")
    public boolean isOperator() {
        return getReflected().isOperator();
    }

    @Override
    @SinceKotlin(version = "1.1")
    public boolean isInfix() {
        return getReflected().isInfix();
    }

    @Override
    @SinceKotlin(version = "1.1")
    public boolean isSuspend() {
        return getReflected().isSuspend();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof FunctionReference) {
            FunctionReference other = (FunctionReference) obj;

            return (getOwner() == null ? other.getOwner() == null : getOwner().equals(other.getOwner())) &&
                   getName().equals(other.getName()) &&
                   getSignature().equals(other.getSignature()) &&
                   Intrinsics.areEqual(getBoundReceiver(), other.getBoundReceiver());
        }
        if (obj instanceof KFunction) {
            return obj.equals(compute());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return ((getOwner() == null ? 0 : getOwner().hashCode() * 31) + getName().hashCode()) * 31 + getSignature().hashCode();
    }

    @Override
    public String toString() {
        KCallable reflected = compute();
        if (reflected != this) {
            return reflected.toString();
        }

        // TODO: consider adding the class name to toString() for constructors
        return "<init>".equals(getName())
               ? "constructor" + Reflection.REFLECTION_NOT_AVAILABLE
               : "function " + getName() + Reflection.REFLECTION_NOT_AVAILABLE;
    }
}
