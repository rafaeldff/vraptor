package br.com.caelum.vraptor.http.iogi;

import iogi.Iogi;
import iogi.parameters.Parameter;
import iogi.parameters.Parameters;
import iogi.reflection.Target;
import iogi.util.DefaultLocaleProvider;
import iogi.util.NullDependencyProvider;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.http.ParametersProvider;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.validator.Message;

@RequestScoped
public class IogiParametersProvider implements ParametersProvider {
	private final ParameterNameProvider nameProvider;
	private final HttpServletRequest servletRequest;

	public IogiParametersProvider(ParameterNameProvider provider, HttpServletRequest parameters) {
		this.nameProvider = provider;
		this.servletRequest = parameters;
	}
	
	@Override
	public Object[] getParametersFor(ResourceMethod method, List<Message> errors, ResourceBundle bundle) {
		Method javaMethod = method.getMethod();
		
		Parameters parameters = parseParameters(servletRequest);
		List<Target<Object>> targets = createTargets(javaMethod);

		List<Object> arguments = instantiateParameters(parameters, targets);
		
		return arguments.toArray();
	}

	private List<Object> instantiateParameters(Parameters parameters, List<Target<Object>> targets) {
		List<Object> arguments = new ArrayList<Object>();
		Iogi iogi = new Iogi(new NullDependencyProvider(), new DefaultLocaleProvider());
		for (Target<Object> target : targets) {
			Object newObject = iogi.instantiate(target, parameters);
			arguments.add(newObject);
		}
		return arguments;
	}

	private List<Target<Object>> createTargets(Method javaMethod) {
		List<Target<Object>> targets = new ArrayList<Target<Object>>(); 
		
		Type[] parameterTypes = javaMethod.getGenericParameterTypes();
		String[] parameterNames = nameProvider.parameterNamesFor(javaMethod);
		for (int i = 0; i < methodArity(javaMethod); i++) {
			Target<Object> newTarget = new Target<Object>(parameterTypes[i], parameterNames[i]);
			targets.add(newTarget);
		}
		
		return targets;
	}

	public int methodArity(Method method) {
		return method.getGenericParameterTypes().length;
	}

	private Parameters parseParameters(HttpServletRequest request) {
		List<Parameter> parameterList = new ArrayList<Parameter>();
		
		Enumeration<?> enumeration = request.getParameterNames();
		while (enumeration.hasMoreElements()) {
			String parameterName = (String) enumeration.nextElement();
			String[] parameterValues = request.getParameterValues(parameterName);
			for (String value : parameterValues) {
				Parameter newParameter = new Parameter(parameterName, value);
				parameterList.add(newParameter);
			}
		}
		
		return new Parameters(parameterList);
	}

}
/*
 Q: Localization.getLocale() or JstlWrapper.findLocale()? PS: C&P code from converters.JstlWrapper in JstlLocaleProvider.
 OBS: pass nameProvider to Iogi
 OBS: integrar DI do VRAptor
 */
