package br.com.caelum.vraptor.interceptor;

import java.lang.reflect.Method;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import br.com.caelum.vraptor.resource.ResourceClass;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.validator.ValidationMessage;

/**
 * Useful matchers to use while mocking and hamcresting tests with internal
 * vraptor information.
 * 
 * @author Guilherme Silveira
 */
public class VRaptorMatchers {

    public static TypeSafeMatcher<ResourceMethod> resourceMethod(final Method method) {
        return new TypeSafeMatcher<ResourceMethod>() {

            public boolean matchesSafely(ResourceMethod other) {
                return other.getMethod().equals(method);
            }

            public void describeTo(Description description) {
                description.appendText(" an instance of a resource method for method " + method.getName() + " declared at " + method.getDeclaringClass().getName());
            }

			@Override
			protected void describeMismatchSafely(ResourceMethod item, Description mismatchDescription) {
				mismatchDescription.appendText(" an instance of a resource method for method " + item.getMethod().getName() + " declared at " + item.getMethod().getDeclaringClass().getName());
			}

        };
    }

    public static Matcher<ResourceClass> resource(final Class<?> type) {
        return new BaseMatcher<ResourceClass>() {

            public boolean matches(Object item) {
                if (!(item instanceof ResourceClass)) {
                    return false;
                }
                ResourceClass other = (ResourceClass) item;
                return other.getType().equals(type);
            }

            public void describeTo(Description description) {
                description.appendText(" resource for " + type.getName());
            }

        };
    }

	public static Matcher<ValidationMessage> error(final String category, final String message) {
		return new TypeSafeMatcher<ValidationMessage>() {

			protected void describeMismatchSafely(ValidationMessage item, Description mismatchDescription) {
				mismatchDescription.appendText(" validation message='" +item.getMessage() + "', category = '"+item.getCategory()+"'");
			}

			protected boolean matchesSafely(ValidationMessage m) {
                return message.equals(m.getMessage()) && category.equals(m.getCategory());
            }

			public void describeTo(Description description) {
				description.appendText(" validation message='" +message + "', category = '"+category+"'");
			}
			
		};
	}

}
