package br.com.caelum.vraptor.http;

import java.lang.reflect.AccessibleObject;

/**
 * Provides all parameter names for an specific java method.
 * 
 * @author Guilherme Silveira
 */
public interface ParameterNameProvider {

    String[] parameterNamesFor(AccessibleObject methodOrConstructor);

}
