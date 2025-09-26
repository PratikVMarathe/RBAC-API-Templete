package com.academy.utils;

import java.util.HashMap;
import java.util.Map;

public class PostalCodePatterns {
    public static final Map<String, String> COUNTRY_POSTAL_CODE_REGEX = new HashMap<>();

    static {
        COUNTRY_POSTAL_CODE_REGEX.put("US", "^[0-9]{5}(?:-[0-9]{4})?$"); // USA
        COUNTRY_POSTAL_CODE_REGEX.put("IN", "^[1-9][0-9]{5}$"); // India
        COUNTRY_POSTAL_CODE_REGEX.put("GB", "^(GIR 0AA|[A-Z]{1,2}[0-9][0-9A-Z]? ?[0-9][A-Z]{2})$"); // UK
        COUNTRY_POSTAL_CODE_REGEX.put("CA", "^[A-Za-z]\\d[A-Za-z][ -]?\\d[A-Za-z]\\d$"); // Canada
        COUNTRY_POSTAL_CODE_REGEX.put("DE", "^[0-9]{5}$"); // Germany
        // Add more countries and their postal code regex patterns as needed
    }

    public static String getRegexForCountry(String countryCode) {
        return COUNTRY_POSTAL_CODE_REGEX.get(countryCode.toUpperCase());
    }
}
