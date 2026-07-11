package com.digitalwallet.wallet.service;

import com.digitalwallet.wallet.dto.requestDto.CreditRequestDto;
import com.digitalwallet.wallet.dto.requestDto.WalletCreateRequestDto;
import com.digitalwallet.wallet.dto.requestDto.WalletDto;
import com.digitalwallet.wallet.dto.requestDto.WithdrawRequestDto;
import com.digitalwallet.wallet.util.CommonResponse;

public interface WalletService {


    CommonResponse<String> createWallet(WalletCreateRequestDto requestDto);

    CommonResponse<WalletDto> depositMoney(CreditRequestDto requestDto);

    CommonResponse<WalletDto> withdrawMoney(WithdrawRequestDto requestDto);
}
