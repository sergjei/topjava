package ru.javawebinar.topjava.util;

import org.springframework.format.Formatter;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.Locale;

public class CustomDateFormatter implements Formatter<LocalDate> {
    @Override
    public LocalDate parse(String text, Locale locale) throws ParseException {
        if (StringUtils.hasLength(text)) {
            return LocalDate.parse(text, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE);
        }
        return null;
    }

    @Override
    public String print(LocalDate object, Locale locale) {
        return object == null ? "" : object.toString();
    }
}
