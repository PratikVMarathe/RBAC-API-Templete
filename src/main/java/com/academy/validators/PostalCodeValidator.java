package com.academy.validators;

import java.util.Locale;

import com.academy.models.User;
import com.academy.utils.PostalCodePatterns;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PostalCodeValidator implements ConstraintValidator<ValidPostalCode, User> {

    @Override
    public void initialize(ValidPostalCode constraintAnnotation) {
        // Initialization code, if needed
    }

    @Override
    public boolean isValid(User userProfile, ConstraintValidatorContext context) {
        if (userProfile == null) {
            return true; // Or false, based on your requirements
        }

        String country = userProfile.getCountry();
        String zipcode = userProfile.getZipcode();

        if (country == null || zipcode == null || country.isEmpty() || zipcode.isEmpty()) {
            return true; // Fields are optional; adjust if fields are mandatory
        }

        String countryCode = getCountryCode(country);
        if (countryCode == null) {
            // Unknown country; you can decide to invalidate or skip
            return false;
        }

        String regex = PostalCodePatterns.getRegexForCountry(countryCode);
        if (regex == null) {
            // No pattern defined for this country; decide on validity
            return false;
        }

        boolean matches = zipcode.matches(regex);
        if (!matches) {
            // Customize error message to indicate which field failed
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                   .addPropertyNode("zipcode")
                   .addConstraintViolation();
        }

        return matches;
    }

    private String getCountryCode(String countryName) {
        // Convert country name to ISO 3166-1 alpha-2 country code
        for (String iso : PostalCodePatterns.COUNTRY_POSTAL_CODE_REGEX.keySet()) {
            Locale locale = new Locale("", iso);
            if (locale.getDisplayCountry().equalsIgnoreCase(countryName)) {
                return iso;
            }
        }
        return null;
    }
}

