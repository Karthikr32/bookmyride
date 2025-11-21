package com.BusReservation.utils;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApiPageResponse<T> {

    private T content;
    private Integer totalPages;
    private Long totalElements;
    private Integer currentPage;
    private Integer pageSize;
    private boolean isFirst;
    private boolean isLast;

    public ApiPageResponse(T content, Integer totalPages, Long totalElements, Integer currentPage, Integer pageSize, boolean isFirst, boolean isLast) {
        this.content = content;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.currentPage = currentPage + 1;   // bcz, Pagination usually starts from 0
        this.pageSize = pageSize;
        this.isFirst = isFirst;
        this.isLast = isLast;
    }
}
