package org.example.model;

// Модель элемента из ответа GET /regions.
public record RegionDto(
        String city,
        Long localityId,
        String regionCode
) {
}
