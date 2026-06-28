package com.digitalwallet.wallet.controller;

import com.digitalwallet.wallet.dto.requestDto.WalletCreateRequestDto;
import com.digitalwallet.wallet.service.WalletService;
import com.digitalwallet.wallet.util.CommonResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wallet")
public class WalletController {

    private final WalletService walletService;

    @Autowired
    private WalletController(WalletService walletService){
        this.walletService=walletService;
    }

    @PostMapping("/create")
    public ResponseEntity<CommonResponse<String>> createWallet(@Valid @RequestBody WalletCreateRequestDto requestDto) {

        CommonResponse<String> response = walletService.createWallet(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
