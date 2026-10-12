package com.smxplore.proto.infrastructure.adapters.out.mongo.document;

/**
 * Attribute stored as a sub-document instead of a dynamic key in order to
 * avoid MongoDB restrictions on keys ('.' and '$'). {@code value} may hold a
 * simple value (String, Boolean, Number) or a nested {@link AttributeDocument}.
 */
public record AttributeDocument(String name, Object value) {
}
