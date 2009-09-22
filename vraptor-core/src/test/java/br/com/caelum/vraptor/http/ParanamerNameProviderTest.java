package br.com.caelum.vraptor.http;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class ParanamerNameProviderTest {

    private ParanamerNameProvider provider;

    @Before
    public void setup() {
        this.provider = new ParanamerNameProvider();
    }

    @Test
    public void shouldNameObjectTypeAsItsSimpleName() throws SecurityException, NoSuchMethodException {
        assertThat(provider.parameterNamesFor(Horse.class.getMethod("runThrough", Field.class))[0], is(equalTo("f")));
    }

    @Test
    public void shouldNamePrimitiveTypeAsItsSimpleName() throws SecurityException, NoSuchMethodException {
        assertThat(provider.parameterNamesFor(Horse.class.getMethod("rest", int.class))[0], is(equalTo("hours")));
    }

    @Test
    public void shouldNameArrayAsItsSimpleTypeName() throws SecurityException, NoSuchMethodException {
        assertThat(provider.parameterNamesFor(Horse.class.getMethod("setLeg", int[].class))[0], is(equalTo("length")));
    }

    @Test
    public void shouldNameGenericCollectionUsingOf() throws SecurityException, NoSuchMethodException {
        assertThat(provider.parameterNamesFor(Cat.class.getDeclaredMethod("fightWith", List.class))[0], is(equalTo("cats")));
    }
    
    @Test
	public void shouldIgnoreChangesToTheReturnedArrayInSubsequentCalls() throws Exception {
    	String[] resultOfFirstCall = provider.parameterNamesFor(Horse.class.getMethod("setLeg", int[].class));
		assertThat(resultOfFirstCall[0], is(equalTo("length")));
		
		resultOfFirstCall[0] = "ASDF";
		
		String[] resultOfSecondCall = provider.parameterNamesFor(Horse.class.getMethod("setLeg", int[].class));
		assertThat(resultOfSecondCall[0], is(equalTo("length")));
	}

    static class Field {
    }

    public static class Horse {
        public void runThrough(Field f) {
        }

        public void rest(int hours) {
        }

        public void setLeg(int[] length) {
        }
    }

    public static class Cat {
        void fightWith(List<String> cats) {
        }
    }


}
