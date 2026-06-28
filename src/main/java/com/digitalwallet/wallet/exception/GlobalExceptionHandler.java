package com.digitalwallet.wallet.exception;

import com.digitalwallet.wallet.util.CommonResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WalletAlreadyExistsException.class)
    public ResponseEntity<CommonResponse<String>> handleWalletAlreadyExists(WalletAlreadyExistsException exception){
        CommonResponse<String> commonResponse = new CommonResponse<>();

        commonResponse.setMessage(exception.getMessage());
        commonResponse.setResponseStatus(HttpStatus.CONFLICT.value());
        commonResponse.setData("Wallet Already Exists");
        commonResponse.setLocalDateTime(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CONFLICT.value()).body(commonResponse);
    }

}
