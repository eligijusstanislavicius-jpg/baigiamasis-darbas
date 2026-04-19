package com.feelsent.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FavoriteWishesResponse {
    private List<FavoriteWishResponse> wishes;
    private long count;
    private long remaining;
    private int max;
}