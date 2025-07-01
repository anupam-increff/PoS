package com.increff.pos.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RowWrapper<T> {
    private int rowNum;
    private T data;
}
