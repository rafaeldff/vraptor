package br.com.caelum.vraptor.converter;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import org.apache.log4j.Logger;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.core.VRaptorRequest;

/**
 * Locale based date converter.
 * 
 * @author Guilherme Silveira
 */
@Convert(Date.class)
public class LocaleBasedDateConverter implements Converter<Date> {
    
    private final JstlWrapper jstlWrapper = new JstlWrapper();

    private static final Logger logger = Logger.getLogger(LocaleBasedDateConverter.class);

    private final VRaptorRequest request;
    
    public LocaleBasedDateConverter(VRaptorRequest request) {
        this.request = request;
    }

    public Date convert(String value, Class<? extends Date> type) {
        if (value == null || value.equals("")) {
            return null;
        }
        Locale locale = jstlWrapper.findLocale(request);
        if (locale == null) {
            locale = Locale.getDefault();
        }
        DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT, locale);
        try {
            return format.parse(value);
        } catch (ParseException e) {
            // TODO validation?
            throw new IllegalArgumentException("Unable to convert '" + value + "'.");
        }
    }

}