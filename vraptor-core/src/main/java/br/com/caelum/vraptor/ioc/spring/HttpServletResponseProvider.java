package br.com.caelum.vraptor.ioc.spring;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import org.springframework.beans.factory.FactoryBean;

import javax.servlet.http.HttpServletResponse;

/**
 * Provides the current javax.servlet.http.HttpServletResponse object, provided that Spring has registered it for the
 * current Thread.
 *
 * @author Fabio Kung
 * @see org.springframework.web.context.request.ServletWebRequest
 */
@ApplicationScoped
public class HttpServletResponseProvider implements FactoryBean {

    public Object getObject() throws Exception {
        return VRaptorRequestHolder.currentRequest().getResponse();
    }

    public Class getObjectType() {
        return HttpServletResponse.class;
    }

    public boolean isSingleton() {
        return false;
    }
}