package br.com.caelum.vraptor.http;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class DefaultParameterNameProvider implements ParameterNameProvider{

    private String extractName(ParameterizedType type) {
        Type raw = type.getRawType();
        String name = extractName((Class<?>) raw) + "Of";
        Type[] types = type.getActualTypeArguments();
        for(Type t : types) {
            name += extractName((Class<?>) t);
        }
        return name;
    }

    private String extractName(Class<?> type) {
        if(type.isArray()) {
            return type.getComponentType().getSimpleName();
        }
        return type.getSimpleName();
    }

    private String nameFor(Type type) {
        if(type instanceof ParameterizedType) {
            return extractName((ParameterizedType) type);
        }
        return extractName((Class<?>) type);
    }

    public String[] parameterNamesFor(AccessibleObject method) {
        Type[] parameterTypes = parameterTypes(method);
        String[] names = new String[parameterTypes.length];
        for (int i = 0; i < names.length; i++) {
            names[i] = nameFor(parameterTypes[i]);
        }
        return names;
    }

	@SuppressWarnings("unchecked")
	private Type[] parameterTypes(AccessibleObject methodOrConstructor) {
		if (methodOrConstructor instanceof Method)
			return ((Method)methodOrConstructor).getGenericParameterTypes();
		else if (methodOrConstructor instanceof Constructor<?>)
			return ((Constructor)methodOrConstructor).getGenericParameterTypes();
		else 
			throw new IllegalArgumentException("Expecting a method or constructor, " +
					"instead got " + methodOrConstructor);
	}

}
