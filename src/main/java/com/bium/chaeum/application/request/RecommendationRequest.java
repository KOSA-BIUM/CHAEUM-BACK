package com.bium.chaeum.application.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * author: 이상우
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationRequest {

    private String startDate;
    private String endDate;

}