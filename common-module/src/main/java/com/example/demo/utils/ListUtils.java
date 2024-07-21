package com.example.demo.utils;

import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ListUtils {
    public static boolean isEmpty(List<? extends Object> list) {
        return list == null || list.isEmpty();
    }
}
