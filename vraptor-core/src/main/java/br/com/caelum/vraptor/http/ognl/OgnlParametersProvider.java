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
package br.com.caelum.vraptor.http.ognl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import ognl.NoSuchPropertyException;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlRuntime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.converter.ConversionError;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.http.InvalidParameterException;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.http.ParametersProvider;
import br.com.caelum.vraptor.http.TypeCreator;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.validator.Message;
import br.com.caelum.vraptor.validator.ValidationMessage;
import br.com.caelum.vraptor.view.DefaultLogicResult;
import br.com.caelum.vraptor.vraptor2.Info;

/**
 * Provides parameters using ognl to parse expression values into parameter
 * values.
 *
 * @author guilherme silveira
 */
@RequestScoped
public class OgnlParametersProvider implements ParametersProvider {

	private final TypeCreator creator;

	private final Container container;

	private final Converters converters;

	private final ParameterNameProvider provider;

	private static final Logger logger = LoggerFactory.getLogger(OgnlParametersProvider.class);

	private final HttpServletRequest request;

	private final EmptyElementsRemoval removal;

	public OgnlParametersProvider(TypeCreator creator, Container container, Converters converters,
			ParameterNameProvider provider, HttpServletRequest request, EmptyElementsRemoval removal) {
		this.creator = creator;
		this.container = container;
		this.converters = converters;
		this.provider = provider;
		this.request = request;
		this.removal = removal;
		OgnlRuntime.setNullHandler(Object.class, new ReflectionBasedNullHandler());
		OgnlRuntime.setPropertyAccessor(List.class, new ListAccessor());
		OgnlRuntime.setPropertyAccessor(Object[].class, new ArrayAccessor());
	}

	public Object[] getParametersFor(ResourceMethod method, List<Message> errors, ResourceBundle bundle) {
		Object root = createRoot(method, errors, bundle);
		removal.removeExtraElements();
		Type[] types = method.getMethod().getGenericParameterTypes();
		Object[] result = new Object[types.length];
		String[] names = provider.parameterNamesFor(method.getMethod());
		for (int i = 0; i < types.length; i++) {
			try {
				result[i] = root.getClass().getMethod("get" + Info.capitalize(names[i])).invoke(root);
			} catch (InvocationTargetException e) {
				throw new InvalidParameterException("unable to retrieve values to invoke method", e.getCause());
			} catch (Exception e) {
				throw new InvalidParameterException("unable to retrieve values to invoke method", e);
			}
		}
		return result;
	}

	private Object createRoot(ResourceMethod method, List<Message> errors, ResourceBundle bundle) {
		HttpSession session = request.getSession();
		Object parameters = session.getAttribute(DefaultLogicResult.FLASH_PARAMETERS);
		if (parameters != null) {
			session.removeAttribute(DefaultLogicResult.FLASH_PARAMETERS);
			return parameters;
		} else {
			return createViaOgnl(method, errors, bundle);
		}
	}

	private Object createViaOgnl(ResourceMethod method, List<Message> errors,
			ResourceBundle bundle) {
		Class<?> type = creator.typeFor(method);
		Object root;
		try {
			root = type.getDeclaredConstructor().newInstance();
		} catch (Exception ex) {
			throw new InvalidParameterException("unable to instantiate type" + type.getName(), ex);
		}
		OgnlContext context = (OgnlContext) Ognl.createDefaultContext(root);
		context.setTraceEvaluations(true);
		context.put(Container.class, this.container);

		VRaptorConvertersAdapter adapter = new VRaptorConvertersAdapter(converters, bundle);
		Ognl.setTypeConverter(context, adapter);
		for (Enumeration<?> enumeration = request.getParameterNames(); enumeration.hasMoreElements();) {
			String key = (String) enumeration.nextElement();
			String[] values = request.getParameterValues(key);
			try {
				if (logger.isDebugEnabled()) {
					logger.debug("Applying " + key + " with " + Arrays.toString(values));
				}
				Ognl.setValue(key, context, root, values.length == 1 ? values[0] : values);
			} catch (ConversionError ex) {
				errors.add(new ValidationMessage(ex.getMessage(), key));
			} catch (NoSuchPropertyException ex) {
				// TODO optimization: be able to ignore or not
				if (logger.isDebugEnabled()) {
					logger.debug("Ignoring exception", ex);
				}
			} catch (OgnlException e) {
				throw new InvalidParameterException("unable to parse expression '" + key + "'", e);
			}
		}
		return root;
	}
}
