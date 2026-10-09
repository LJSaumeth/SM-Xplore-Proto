package com.smxplore.proto.domain.model.types;

public record PageRequest(int page, int size, String sortBy, SortOrder sortOrder) {
    public enum SortOrder {
        ASC,
        DESC,
        NONE
    }

    public static PageRequest of(int page, int pageSize, String sortBy, SortOrder sortOrder) {
        return new PageRequest(page, pageSize, sortBy, sortOrder);
    }

    public static PageRequest of(int page, int pageSize){
        return new PageRequest(page, pageSize, null, SortOrder.NONE);
    }
}
