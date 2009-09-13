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

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.test.VRaptorMockery;

public class GenericNullHandlerTest {

	private VRaptorMockery mockery;
	private EmptyElementsRemoval removal;
	private Container container;

	@Before
	public void setup() {
		this.mockery = new VRaptorMockery(true);
		this.removal = mockery.mock(EmptyElementsRemoval.class);
		this.container = mockery.mock(Container.class);
		mockery.checking(new Expectations() {
			{
				allowing(container).instanceFor(EmptyElementsRemoval.class); will(returnValue(removal));
			}
		});
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldDenyMostInterfaces() throws Exception {
		GenericNullHandler handler = new GenericNullHandler();
		handler.instantiate(TheInterface.class, container);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldDenyMostAbstractClasses() throws Exception {
		GenericNullHandler handler = new GenericNullHandler();
		handler.instantiate(AbstractClass.class, container);
	}

	@Test
	public void shouldInstantiateGregorianCalendarForAbstractCalendarType()
			throws Exception {
		GenericNullHandler handler = new GenericNullHandler();
		Calendar calendar = handler.instantiate(Calendar.class, container);
		assertThat(calendar, is(notNullValue()));
		assertThat(calendar, is(instanceOf(GregorianCalendar.class)));
	}

	@Test
	public void shouldInstantiateArrayListForCollectionInterface()
			throws Exception {
		mockery.checking(new Expectations() {
			{
				one(removal).add(new ArrayList<Object>());
			}
		});
		GenericNullHandler handler = new GenericNullHandler();
		Collection<?> collection = handler.instantiate(Collection.class, container);
		assertThat(collection, is(notNullValue()));
		assertThat(collection, is(instanceOf(ArrayList.class)));
	}

	@Test
	public void shouldInstantiateArrayListForListInterface() throws Exception {
		mockery.checking(new Expectations() {
			{
				one(removal).add(new ArrayList<Object>());
			}
		});
		GenericNullHandler handler = new GenericNullHandler();
		List<?> list = handler.instantiate(List.class, container);
		assertThat(list, is(notNullValue()));
		assertThat(list, is(instanceOf(ArrayList.class)));
	}

	@Test
	public void shouldInstantiateLinkedListForQueueInterface() throws Exception {
		mockery.checking(new Expectations() {
			{
				one(removal).add(new LinkedList<Object>());
			}
		});
		GenericNullHandler handler = new GenericNullHandler();
		Queue<?> queue = handler.instantiate(Queue.class, container);
		assertThat(queue, is(notNullValue()));
		assertThat(queue, is(instanceOf(LinkedList.class)));
	}

	@Test
	public void shouldInstantiateHashSetListForSetInterface() throws Exception {
		mockery.checking(new Expectations() {
			{
				one(removal).add(new HashSet<Object>());
			}
		});
		GenericNullHandler handler = new GenericNullHandler();
		Set<?> set = handler.instantiate(Set.class, container);
		assertThat(set, is(notNullValue()));
		assertThat(set, is(instanceOf(HashSet.class)));
	}

	@Test
	public void shouldInstantiateTreeSetListForSortedSetInterface()
			throws Exception {
		mockery.checking(new Expectations() {
			{
				one(removal).add(new TreeSet<Object>());
			}
		});
		GenericNullHandler handler = new GenericNullHandler();
		Set<?> set = handler.instantiate(SortedSet.class, container);
		assertThat(set, is(notNullValue()));
		assertThat(set, is(instanceOf(TreeSet.class)));
	}

}

interface TheInterface {
}

abstract class AbstractClass {
}
