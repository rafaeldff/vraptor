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

import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.ResourceBundleDescription;

/**
 * Hamcrest based validation support.
 *
 * @author Guilherme Silveira
 */
public class Validations {

    private final List<Message> errors = new ArrayList<Message>();
	private final ResourceBundle bundle;

    public Validations(ResourceBundle bundle) {
		this.bundle = bundle;
	}

    public Validations() {
    	this(ResourceBundle.getBundle("messages"));
    }

    public <T> boolean that(T id, Matcher<? super T> matcher) {
        return that("", null, id, matcher);
    }

    public <T> boolean that(String category, T id, Matcher<T> matcher) {
        return that(category, null, id, matcher);
    }

    public <T> boolean that(String category, String reason, T actual, Matcher<? super T> matcher) {
        if (!matcher.matches(actual)) {
            if (reason != null) {
                errors.add(new ValidationMessage(getString(reason), category));
            } else {
                Description description = new ResourceBundleDescription(bundle);
                description.appendDescriptionOf(matcher);
                errors.add(new ValidationMessage(description.toString(), category));
            }
            return false;
        }
        return true;
    }

    public void that(String category, String reason, boolean assertion) {
        if (!assertion) {
            errors.add(new ValidationMessage(getString(reason), category));
        }
    }
    
    public String getString(String key) {
    	try {
    		return bundle.getString(key);
    	} catch(MissingResourceException e) {
    		return "???" + key + "???";
    	}
    }

    /**
     * Returns the list of errors.
     */
    public List<Message> getErrors() {
        return errors;
    }

    /**
     * Adds a list of errors to the error list.
     * @return 
     */
    public Validations and(List<Message> errors) {
        this.errors.addAll(errors);
        return this;
    }

    /**
     * Adds a single error message to the error list.
     */
    public Validations and(Message error) {
        this.errors.add(error);
        return this;
    }

    public <T> If<T> that(T instance) {
        return new If<T>(instance, this);
    }

    /**
     * Can be overriden to add extra validations processes.
     */
    public void check() {
    }

    public static <T> org.hamcrest.Matcher<T> is(org.hamcrest.Matcher<T> matcher) {
        return ShouldBe.shouldBe(matcher);
    }

}
