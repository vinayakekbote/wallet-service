package com.digitalwallet.wallet.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonResponse<T> {

    private boolean success;

    private String message;

    private T data;

    private LocalDateTime localDateTime;

    private Integer responseStatus;
}
