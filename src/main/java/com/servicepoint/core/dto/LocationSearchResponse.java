package com.servicepoint.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocationSearchResponse {
    private List<ProviderWithUser> providers;
    private Integer total;
    private Integer limit;
    private Integer offset;
    private Double searchRadius;
    private String searchSubject;
    private SearchMetadata metadata;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchMetadata {
        private Double centerLatitude;
        private Double centerLongitude;
        private String executionTime;
        private Boolean hasMore;
    }
}