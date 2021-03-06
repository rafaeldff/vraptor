package br.com.caelum.vraptor.vraptor2;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.RequestExecution;
import br.com.caelum.vraptor.core.URLParameterExtractorInterceptor;
import br.com.caelum.vraptor.interceptor.ExecuteMethodInterceptor;
import br.com.caelum.vraptor.interceptor.InstantiateInterceptor;
import br.com.caelum.vraptor.interceptor.InterceptorListPriorToExecutionExtractor;
import br.com.caelum.vraptor.interceptor.OutjectResult;
import br.com.caelum.vraptor.interceptor.ParametersInstantiatorInterceptor;
import br.com.caelum.vraptor.interceptor.ResourceLookupInterceptor;
import br.com.caelum.vraptor.interceptor.download.DownloadInterceptor;
import br.com.caelum.vraptor.interceptor.multipart.MultipartInterceptor;
import br.com.caelum.vraptor.vraptor2.outject.OutjectionInterceptor;

/**
 * A vraptor 2 request execution process.
 *
 * @author Guilherme Silveira
 */
public class VRaptor2RequestExecution implements RequestExecution {
    private final InterceptorStack interceptorStack;
    private final InstantiateInterceptor instantiator;
    private final boolean shouldRegisterHibernateValidator;

    public VRaptor2RequestExecution(InterceptorStack interceptorStack, InstantiateInterceptor instantiator, Config config) {
        this.interceptorStack = interceptorStack;
        this.instantiator = instantiator;
        this.shouldRegisterHibernateValidator = config.hasPlugin("org.vraptor.plugin.hibernate.HibernateValidatorPlugin");
    }

    public void execute() throws InterceptionException {
        interceptorStack.add(ResourceLookupInterceptor.class);
        interceptorStack.add(URLParameterExtractorInterceptor.class);
        interceptorStack.add(InterceptorListPriorToExecutionExtractor.class);
        interceptorStack.add(DownloadInterceptor.class);
        interceptorStack.add(MultipartInterceptor.class);
        interceptorStack.add(instantiator);
        interceptorStack.add(ParametersInstantiatorInterceptor.class);
        if(shouldRegisterHibernateValidator) {
            interceptorStack.add(HibernateValidatorPluginInterceptor.class);
        }
        interceptorStack.add(ValidatorInterceptor.class);
        interceptorStack.add(ExecuteMethodInterceptor.class);
        interceptorStack.add(OutjectResult.class);
        interceptorStack.add(OutjectionInterceptor.class);
        interceptorStack.add(AjaxInterceptor.class);
        interceptorStack.add(ViewInterceptor.class);
        interceptorStack.next(null, null);
    }
}
