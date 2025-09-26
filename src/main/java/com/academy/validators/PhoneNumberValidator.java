// src/main/java/com/example/project/validator/PhoneNumberValidator.java

package com.academy.validators;

import com.academy.models.User;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, User> {

    private PhoneNumberUtil phoneUtil;

    @Override
    public void initialize(ValidPhoneNumber constraintAnnotation) {
        phoneUtil = PhoneNumberUtil.getInstance();
    }

    @Override
    public boolean isValid(User userProfile, ConstraintValidatorContext context) {
        if (userProfile == null) {
            return true; // Consider null as valid; use @NotNull if needed
        }

        String mobileNumber = userProfile.getMobileNumber();
        String country = userProfile.getCountry();

        if (mobileNumber == null || mobileNumber.trim().isEmpty()) {
            return true; // Consider empty as valid; use @NotBlank if needed
        }

        if (country == null || country.trim().isEmpty()) {
            // Cannot validate without country information
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Country must be provided for phone number validation")
                   .addPropertyNode("country")
                   .addConstraintViolation();
            return false;
        }

        // Convert country name to ISO 3166-1 alpha-2 country code
        String countryCode = getCountryCode(country.trim());

        if (countryCode == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Unknown country: " + country)
                   .addPropertyNode("country")
                   .addConstraintViolation();
            return false;
        }

        try {
            // Parse the phone number with the country code
            com.google.i18n.phonenumbers.Phonenumber.PhoneNumber number = phoneUtil.parse(mobileNumber, countryCode);

            // Check if the number is a possible number and a valid number
            boolean isValid = phoneUtil.isPossibleNumber(number) && phoneUtil.isValidNumber(number);

            if (!isValid) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Invalid phone number for country: " + country)
                       .addPropertyNode("mobileNumber")
                       .addConstraintViolation();
            }

            return isValid;

        } catch (NumberParseException e) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Error parsing phone number: " + e.getMessage())
                   .addPropertyNode("mobileNumber")
                   .addConstraintViolation();
            return false;
        }
    }

    /**
     * Converts a country name to its ISO 3166-1 alpha-2 country code.
     * Returns null if the country name is not recognized.
     */
    private String getCountryCode(String countryName) {
        for (String iso : java.util.Locale.getISOCountries()) {
            java.util.Locale locale = new java.util.Locale("", iso);
            if (locale.getDisplayCountry().equalsIgnoreCase(countryName)) {
                return iso;
            }
        }
        return null;
    }
}
