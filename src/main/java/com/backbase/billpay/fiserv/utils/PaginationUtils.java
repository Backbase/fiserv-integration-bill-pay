package com.backbase.billpay.fiserv.utils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Utility methods for pagination.
 */
public class PaginationUtils {
    
    private static final int DEFAULT_FROM = 0;
    private static final int DEFAULT_SIZE = 1000;
    
    private PaginationUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Paginate the given list to return a sublist of the specified size and section.
     * @param <T> Object type within the list
     * @param list List to be paginated
     * @param from Section of the paginated list to return
     * @param size Size of each paginated section
     * @return paginatedList Paginated list
     */
    public static <T> List<T> paginateList(List<T> list, Integer from, Integer size) {
        int pageSize = (size == null || size == 0) ? DEFAULT_SIZE : size;
        int pageFrom = from == null ? DEFAULT_FROM : from;
        Map<Integer, List<T>> map = IntStream.range(0, (list.size() + pageSize - 1) / pageSize).boxed()
                        .collect(Collectors.toMap(i -> i, 
                            i -> list.subList(i * pageSize, Math.min(pageSize * (i + 1), list.size()))));
        int numOfPages = map.size();
        return pageFrom >= numOfPages ? Collections.emptyList() : map.get(pageFrom);
    }

}
