package ru.javawebinar.topjava.util;

import org.springframework.format.Formatter;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class CustomDateTimeFormatter implements Formatter<LocalDateTime> {
    @Override
    public LocalDateTime parse(String text, Locale locale) throws ParseException {
        return StringUtils.hasLength(text) ? LocalDateTime.parse(text, DateTimeFormatter.ISO_DATE_TIME) : null;
    }

    @Override
    public String print(LocalDateTime object, Locale locale) {
        return object == null ? "" : object.toString();
    }
}
