package com.smxplore.proto.domain.model.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record Page<T>(List<T> content, int page, int pageSize, long totalElements) {
    public Page(List<T> content, int page, int pageSize, long totalElements){
        if (page < 0)
            throw new IllegalArgumentException("Page cannot be less than zero");
        if (pageSize < 0)
            throw new IllegalArgumentException("Page size cannot be less than zero");
        if (totalElements < 0)
            throw new IllegalArgumentException("Total elements cannot be less than zero");
        this.content = content == null ? new ArrayList<T>() : content;
        this.page = page;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
    }

    public static <T> Page<T> empty(int page, int pageSize){
        return new Page<>(Collections.emptyList(), page, pageSize, 0L);
    }

    public static <T> Page<T> of(List<T> content, int page, int pageSize, long totalElements){
        return new Page<>(content, page, pageSize, totalElements);
    }

    public int getTotalPages() {
        if (pageSize == 0) return 0;
        return (int) Math.ceil((double) totalElements / (double) pageSize);
    }

    public boolean isEmpty() {
        return content.isEmpty();
    }

    public int getPageNumberOfElements(){
        return content.size();
    }

    public boolean isLastPage() {
        return page >=  getTotalPages() - 1;
    }

    public boolean isFirstPage() {
        return page == 0;
    }

    public boolean hasPreviousPage() {
        return page > 0;
    }

    public boolean hasNextPage() {
        return page + 1 < getTotalPages();
    }

}
