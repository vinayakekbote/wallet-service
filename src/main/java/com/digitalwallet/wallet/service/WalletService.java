package com.digitalwallet.wallet.service;

import com.digitalwallet.wallet.dto.requestDto.WalletCreateRequestDto;
import com.digitalwallet.wallet.util.CommonResponse;
import org.springframework.http.RequestEntity;

public interface WalletService {


    CommonResponse<String> createWallet(WalletCreateRequestDto requestDto);
}
