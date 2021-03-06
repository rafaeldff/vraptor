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

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

public class ShouldBe<T> implements Matcher<T> {
	private final Matcher<T> matcher;

	public ShouldBe(Matcher<T> matcher) {
		this.matcher = matcher;
	}

	public boolean matches(Object arg) {
		return matcher.matches(arg);
	}

	public void describeTo(Description description) {
		description.appendText("should be ").appendDescriptionOf(matcher);
	}
	
	@Factory
	public static <T> Matcher<T> shouldBe(Matcher<T> matcher) {
		return new ShouldBe<T>(matcher);
	}

	@Factory
	public static <T> Matcher<? super T> shouldBe(T value) {
		return shouldBe(equalTo(value));
	}

	@Factory
	public static <T> Matcher<? super T> shouldBe(Class<T> type) {
		return shouldBe(instanceOf(type));
	}

	public void _dont_implement_Matcher___instead_extend_BaseMatcher_() {
		
	}

	public void describeMismatch(Object item, Description description) {
    	matcher.describeMismatch(item, description);
	}
}
