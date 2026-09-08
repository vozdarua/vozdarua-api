package io.vozdarua.model.dto;

import java.util.List;

public record CityMetricsDTO(Long cityId, long total,
                              List<NameCountDTO> byStatus, List<NameCountDTO> byCategory,
                              List<NameCountDTO> byNeighborhood, List<NameCountDTO> bySeverity) {}
