package br.com.caelum.vraptor.vraptor2;

import java.lang.reflect.AccessibleObject;

import org.vraptor.annotations.Logic;

import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.http.ParanamerNameProvider;
import br.com.caelum.vraptor.ioc.ApplicationScoped;

/**
 * Looks up for the Logic annotation on the method, if its found and containing
 * the parameters value, its value is used. Otherwise, fallsback to the
 * delegated provider. The default delegate provider is the paranamer based.
 * 
 * @author Guilherme Silveira
 */
@ApplicationScoped
public class LogicAnnotationWithParanamerParameterNameProvider implements ParameterNameProvider {

    private final ParameterNameProvider delegate;

    public LogicAnnotationWithParanamerParameterNameProvider() {
        this(new ParanamerNameProvider());
    }

    public LogicAnnotationWithParanamerParameterNameProvider(ParameterNameProvider delegate) {
        this.delegate = delegate;
    }

    public String[] parameterNamesFor(AccessibleObject method) {
        if (method.isAnnotationPresent(Logic.class)) {
            return method.getAnnotation(Logic.class).parameters();
        }
        return delegate.parameterNamesFor(method);
    }

}
