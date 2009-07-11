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
package br.com.caelum.vraptor.view;

import javax.servlet.http.HttpServletRequest;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.resource.ResourceClass;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class DefaultPathResolverTest {

    private Mockery mockery;
    private ResourceMethod method;
    private ResourceClass resource;
    private HttpServletRequest request;
	private AcceptHeaderToFormat acceptHeaderToFormat;

    @Before
    public void config() {
        this.mockery = new Mockery();
        this.method = mockery.mock(ResourceMethod.class);
        this.request = mockery.mock(HttpServletRequest.class);
        this.resource = mockery.mock(ResourceClass.class);
        this.acceptHeaderToFormat = mockery.mock(AcceptHeaderToFormat.class);
     
        mockery.checking(new Expectations(){{
        	allowing(acceptHeaderToFormat).getFormat("application/json"); will(returnValue("json"));
        	allowing(acceptHeaderToFormat).getFormat(with(any(String.class))); will(returnValue("html"));
        }});
    }
    
    @Test
    public void shouldUseResourceTypeAndMethodNameToResolveJsp() throws NoSuchMethodException {
        mockery.checking(new Expectations() {
            {
            	allowing(request).getHeader("Accept"); will(returnValue(null));
                one(method).getResource(); will(returnValue(resource));
                one(method).getMethod(); will(returnValue(DogController.class.getDeclaredMethod("bark")));
                one(resource).getType(); will(returnValue(DogController.class));
                one(request).getParameter("_format"); will(returnValue(null));
            }
        });
        DefaultPathResolver resolver = new DefaultPathResolver(request, acceptHeaderToFormat);
        String result = resolver.pathFor(method);
        MatcherAssert.assertThat(result, Matchers.is(Matchers.equalTo("/WEB-INF/jsp/dog/bark.jsp")));
        mockery.assertIsSatisfied();
    }
    
    @Test
    public void shouldUseTheFormatParameterIfSupplied() throws NoSuchMethodException {
        mockery.checking(new Expectations() {
            {
            	allowing(request).getHeader("Accept"); will(returnValue(null));
                one(method).getResource(); will(returnValue(resource));
                one(method).getMethod(); will(returnValue(DogController.class.getDeclaredMethod("bark")));
                one(resource).getType(); will(returnValue(DogController.class));
                one(request).getParameter("_format"); will(returnValue("json"));
            }
        });
        DefaultPathResolver resolver = new DefaultPathResolver(request, acceptHeaderToFormat);
        String result = resolver.pathFor(method);
        MatcherAssert.assertThat(result, Matchers.is(Matchers.equalTo("/WEB-INF/jsp/dog/bark.json.jsp")));
        mockery.assertIsSatisfied();
    }
    
    @Test
    public void shouldIgnoreHtmlFormat() throws NoSuchMethodException {
        mockery.checking(new Expectations() {
            {
            	allowing(request).getHeader("Accept"); will(returnValue(null));
                one(method).getResource(); will(returnValue(resource));
                one(method).getMethod(); will(returnValue(DogController.class.getDeclaredMethod("bark")));
                one(resource).getType(); will(returnValue(DogController.class));
                one(request).getParameter("_format"); will(returnValue("html"));
            }
        });
        DefaultPathResolver resolver = new DefaultPathResolver(request, acceptHeaderToFormat);
        String result = resolver.pathFor(method);
        MatcherAssert.assertThat(result, Matchers.is(Matchers.equalTo("/WEB-INF/jsp/dog/bark.jsp")));
        mockery.assertIsSatisfied();
    }
    
    @Test
    public void useAcceptHeaderWhenFormatIsNotDefined() throws NoSuchMethodException {
        mockery.checking(new Expectations() {
            {
            	allowing(request).getHeader("Accept"); will(returnValue("application/json"));
                one(method).getResource(); will(returnValue(resource));
                one(method).getMethod(); will(returnValue(DogController.class.getDeclaredMethod("bark")));
                one(resource).getType(); will(returnValue(DogController.class));
                one(request).getParameter("_format"); will(returnValue(null));
            }
        });
        DefaultPathResolver resolver = new DefaultPathResolver(request, acceptHeaderToFormat);
        String result = resolver.pathFor(method);
        MatcherAssert.assertThat(result, Matchers.is(Matchers.equalTo("/WEB-INF/jsp/dog/bark.json.jsp")));
        mockery.assertIsSatisfied();
    }
    
    @Test
    public void formatParamOverridesAcceptHeader() throws NoSuchMethodException {
        mockery.checking(new Expectations() {
            {
            	allowing(request).getHeader("Accept"); will(returnValue("application/json"));
                one(method).getResource(); will(returnValue(resource));
                one(method).getMethod(); will(returnValue(DogController.class.getDeclaredMethod("bark")));
                one(resource).getType(); will(returnValue(DogController.class));
                one(request).getParameter("_format"); will(returnValue("html"));
            }
        });
        DefaultPathResolver resolver = new DefaultPathResolver(request, acceptHeaderToFormat);
        String result = resolver.pathFor(method);
        MatcherAssert.assertThat(result, Matchers.is(Matchers.equalTo("/WEB-INF/jsp/dog/bark.jsp")));
        mockery.assertIsSatisfied();
    }
    
    @Test
    public void formatParamOverridesAcceptHeader2() throws NoSuchMethodException {
        mockery.checking(new Expectations() {
            {
            	allowing(request).getHeader("Accept"); will(returnValue("text/html"));
                one(method).getResource(); will(returnValue(resource));
                one(method).getMethod(); will(returnValue(DogController.class.getDeclaredMethod("bark")));
                one(resource).getType(); will(returnValue(DogController.class));
                one(request).getParameter("_format"); will(returnValue("json"));
            }
        });
        DefaultPathResolver resolver = new DefaultPathResolver(request, acceptHeaderToFormat);
        String result = resolver.pathFor(method);
        MatcherAssert.assertThat(result, Matchers.is(Matchers.equalTo("/WEB-INF/jsp/dog/bark.json.jsp")));
        mockery.assertIsSatisfied();
    }
}
