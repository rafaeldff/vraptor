/***
 * 
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. Neither the name of the
 * copyright holders nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written
 * permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package br.com.caelum.vraptor.validator;

import org.hamcrest.Matcher;

/**
 * Executes some piece of code only if the matching is true. If its false, add
 * the description for the false check as an error.
 * 
 * @author Guilherme Silveira
 */
public class If<T> {

    @SuppressWarnings("unchecked")
    private static final Then NOTHING = new Then(new Validations()) {
        @Override
        public void otherwise(Validations validations) {
            // does nothing
        }
    };

    private final T instance;
    private final Validations actual;

    public If(T instance, Validations actual) {
        this.instance = instance;
        this.actual = actual;
    }

    @SuppressWarnings("unchecked")
    public Then<T> shouldBe(Matcher matcher) {
        if (!actual.that(instance, matcher)) {
            return NOTHING;
        }
        return new Then<T>(actual);
    }

    @SuppressWarnings("unchecked")
    public Then<T> shouldBe(String category, Matcher matcher) {
        if (!actual.that(category, instance, matcher)) {
            return NOTHING;
        }
        return new Then<T>(actual);
    }

    @SuppressWarnings("unchecked")
    public Then<T> shouldBe(String category, String reason, Matcher matcher) {
        if (!actual.that(category, reason, instance, matcher)) {
            return NOTHING;
        }
        return new Then<T>(actual);
    }

}
