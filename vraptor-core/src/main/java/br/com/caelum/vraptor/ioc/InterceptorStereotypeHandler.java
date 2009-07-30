package br.com.caelum.vraptor.ioc;

import java.lang.annotation.Annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.VRaptorException;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.interceptor.InterceptorRegistry;
import br.com.caelum.vraptor.interceptor.InterceptorSequence;

@ApplicationScoped
public class InterceptorStereotypeHandler implements StereotypeHandler {
	private static final Logger logger = LoggerFactory.getLogger(InterceptorStereotypeHandler.class);
	private final InterceptorRegistry registry;

	public InterceptorStereotypeHandler(InterceptorRegistry registry) {
		this.registry = registry;
	}

	public Class<? extends Annotation> stereotype() {
		return Intercepts.class;
	}

	public void handle(Class<?> type) {
		if (Interceptor.class.isAssignableFrom(type)) {
            registerInterceptor(type);
        } else if (InterceptorSequence.class.isAssignableFrom(type)) {
            registerInterceptorSequence(type);
        } else {
            throw new VRaptorException("Annotation " + Intercepts.class + " found in " + type
                    + ", but it is neither an Interceptor nor an InterceptorSequence.");
        }
	}

	@SuppressWarnings("unchecked")
	private void registerInterceptor(Class<?> type) {
		logger.debug("Found interceptor for " + type);
		Class<? extends Interceptor> interceptorType = (Class<? extends Interceptor>) type;
		registry.register(interceptorType);
	}
	
	@SuppressWarnings("unchecked")
	private void registerInterceptorSequence(Class<?> type) {
		logger.debug("Found interceptor sequence for " + type);
		Class<? extends InterceptorSequence> interceptorSequenceType = (Class<? extends InterceptorSequence>) type;
		registry.register(parseSequence(interceptorSequenceType));
	}

	private Class<? extends Interceptor>[] parseSequence(Class<? extends InterceptorSequence> type) {
		try {
			InterceptorSequence sequence = type.getConstructor().newInstance();
			return sequence.getSequence();
		} catch (Exception e) {
			throw new VRaptorException("Problem ocurred while instantiating an interceptor sequence", e);
		}
	}
}
