/***
 *
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
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
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package br.com.caelum.vraptor.core;

import java.util.ArrayList;
import java.util.List;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * Default implmentation of a interceptor stack.
 * @author guilherme silveira
 *
 */
@RequestScoped
public class DefaultInterceptorStack implements InterceptorStack {

    private final List<InterceptorHandler> interceptors = new ArrayList<InterceptorHandler>();
    private final Container container;

    public DefaultInterceptorStack(Container container) {
        this.container = container;
    }

    /**
     * we do not use an iterator so an interceptor can hack the code to add new
     * interceptors on the fly
     */
    private int nextInterceptor = 0;

    public void add(Interceptor interceptor) {
        this.interceptors.add(new InstantiatedInterceptorHandler(interceptor));
    }

    public void next(ResourceMethod method, Object resourceInstance) throws InterceptionException {
        if (nextInterceptor == interceptors.size()) {
            return;
        }
        InterceptorHandler handler = interceptors.get(nextInterceptor++);
        handler.execute(this, method, resourceInstance);
    }

    public <T extends Interceptor> void add(Class<T> type) {
        this.interceptors.add(new ToInstantiateInterceptorHandler(container, type));
    }

    public void addAsNext(Interceptor interceptor) {
        this.interceptors.add(nextInterceptor, new InstantiatedInterceptorHandler(interceptor));
    }

}
