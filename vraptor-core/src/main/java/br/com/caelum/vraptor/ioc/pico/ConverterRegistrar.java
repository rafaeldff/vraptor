/***
 *
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. Neither the name of the
 * copyright holders nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written
 * permission.
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
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package br.com.caelum.vraptor.ioc.pico;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.VRaptorException;
import br.com.caelum.vraptor.core.BaseComponents;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * Prepares all classes annotated with @Convert to be used as converters.
 *
 * @author Guilherme Silveira
 * @author Fabio Kung
 */
@ApplicationScoped
public class ConverterRegistrar implements Registrar {

    private final Logger logger = LoggerFactory.getLogger(ConverterRegistrar.class);
    private final Converters converters;

    public ConverterRegistrar(Converters converters) {
        this.converters = converters;
    }

    @SuppressWarnings({"unchecked"})
    public void registerFrom(Scanner scanner) {
        logger.info("Registering all custom converters annotated with @Convert");
        Collection<Class<?>> converterTypes = scanner.getTypesWithAnnotation(Convert.class);

        for (Class<?> converterType : converterTypes) {
            if (!Converter.class.isAssignableFrom(converterType)) {
                throw new VRaptorException("converter " + converterType + "does not implement Converter");
            }
            logger.debug("found converter: " + converterType);
            converters.register((Class<? extends Converter<?>>) converterType);
        }
    }
}
