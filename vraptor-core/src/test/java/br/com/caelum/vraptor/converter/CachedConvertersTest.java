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
package br.com.caelum.vraptor.converter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.ioc.Container;

public class CachedConvertersTest {

    private Mockery mockery;
    private CachedConverters converters;
    private Converters delegate;
    private Converter<?> converter;
    private Container container;

    @Before
    public void setup() {
        this.mockery = new Mockery();
        this.delegate = mockery.mock(Converters.class);
        this.converter = mockery.mock(Converter.class);
        this.converters = new CachedConverters(delegate);
        this.container = mockery.mock(Container.class);
        mockery.checking(new Expectations() {
            {
                one(delegate).to(CachedConvertersTest.class, container); will(returnValue(converter));
            }
        });
    }

	@Test
    public void shouldUseTheProvidedConverterDuringFirstRequest() {
		@SuppressWarnings("unchecked")
        Converter found = converters.to(CachedConvertersTest.class, container);
        assertThat(found, is(equalTo(this.converter)));
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldUseTheSameConverterOnFurtherRequests() {
        mockery.checking(new Expectations(){{
            one(container).instanceFor(converter.getClass()); will(returnValue(converter));
        }});
        @SuppressWarnings("unchecked")
        Converter found = converters.to(CachedConvertersTest.class, container);
        assertThat(converters.to(CachedConvertersTest.class, container), is(equalTo(found)));
        mockery.assertIsSatisfied();
    }
    
    @Test
	public void existsForWillReturnTrueIfTypeIsAlreadyCached() throws Exception {
    	mockery.checking(new Expectations(){{
    		one(container).instanceFor(converter.getClass()); will(returnValue(converter));
    		
    		allowing(delegate).existsFor(CachedConvertersTest.class, container);
    		will(returnValue(true));
    	}});
    	
    	assertTrue(converters.existsFor(CachedConvertersTest.class, container));
	}
    
    @Test
    public void existsForWillReturnTrueIfDelegateAlsoReturnsTrue() throws Exception {
    	mockery.checking(new Expectations(){{
    		atLeast(1).of(delegate).existsFor(CachedConvertersTest.class, container);
    		will(returnValue(true));
    	}});
    	
    	assertTrue(converters.existsFor(CachedConvertersTest.class, container));
    }

}
