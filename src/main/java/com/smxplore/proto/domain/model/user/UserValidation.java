package com.smxplore.proto.domain.model.user;

import com.smxplore.proto.domain.exceptions.InvalidUserDataException;

import java.time.Instant;
import java.util.UUID;
import java.util.regex.Pattern;

public final class UserValidation {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^[0-9+()\\-\\s]{7,20}$");

    private UserValidation() {
    }

    public static UUID requireId(UUID value) {
        if (value == null) {
            throw new InvalidUserDataException("The user id is required.");
        }
        return value;
    }

    public static Instant requireInstant(Instant value, String field) {
        if (value == null) {
            throw new InvalidUserDataException("The field '" + field + "' is required.");
        }
        return value;
    }

    public static <T> T requireNonNull(T value, String field) {
        if (value == null) {
            throw new InvalidUserDataException("The field '" + field + "' is required.");
        }
        return value;
    }

    public static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new InvalidUserDataException("The field '" + field + "' is required.");
        }
        return value.trim();
    }

    public static String validateFullName(String value) {
        String normalized = requireText(value, "fullName");
        if (normalized.length() < 2) {
            throw new InvalidUserDataException("The field 'fullName' must have at least 2 characters.");
        }
        return normalized;
    }

    public static String validateEmail(String value) {
        String normalized = requireText(value, "email").toLowerCase();
        if (!EMAIL_PATTERN.matcher(normalized).matches()) {
            throw new InvalidUserDataException("The email '" + value + "' has an invalid format.");
        }
        return normalized;
    }

    public static String validatePhone(String value) {
        String normalized = requireText(value, "phone");
        if (!PHONE_PATTERN.matcher(normalized).matches()) {
            throw new InvalidUserDataException("The phone '" + value + "' has an invalid format.");
        }
        return normalized;
    }

    public static String validatePasswordHash(String value) {
        return requireText(value, "passwordHash");
    }
}
