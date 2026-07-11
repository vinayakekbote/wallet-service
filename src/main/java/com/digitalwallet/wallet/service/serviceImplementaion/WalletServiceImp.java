package com.digitalwallet.wallet.service.serviceImplementaion;

import com.digitalwallet.wallet.dto.requestDto.CreditRequestDto;
import com.digitalwallet.wallet.dto.requestDto.WalletCreateRequestDto;
import com.digitalwallet.wallet.dto.requestDto.WalletDto;
import com.digitalwallet.wallet.dto.requestDto.WithdrawRequestDto;
import com.digitalwallet.wallet.entity.Wallet;
import com.digitalwallet.wallet.enums.CurrencyType;
import com.digitalwallet.wallet.enums.WalletStatus;
import com.digitalwallet.wallet.exception.InvalidAmountException;
import com.digitalwallet.wallet.exception.WalletAlreadyExistsException;
import com.digitalwallet.wallet.exception.WalletInactiveException;
import com.digitalwallet.wallet.exception.WalletNotFoundException;
import com.digitalwallet.wallet.repository.WalletRepository;
import com.digitalwallet.wallet.service.WalletService;
import com.digitalwallet.wallet.util.CommonResponse;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Transactional
public class WalletServiceImp implements WalletService {

    private static final Logger logger = LoggerFactory.getLogger(WalletServiceImp.class);

    private final WalletRepository repository;

    @Autowired
    public WalletServiceImp(WalletRepository repository){
        this.repository = repository;
    }

    @Override
    public CommonResponse<String> createWallet(WalletCreateRequestDto requestDto) {

        logger.info("Creating wallet for user: {}", requestDto.getUserId());

        CommonResponse<String> response = new CommonResponse<>();

        if (repository.existsByUserId(requestDto.getUserId())) {
            throw new WalletAlreadyExistsException(
                    "Wallet already exists for this user.");
        }

        Wallet wallet = new Wallet();
        wallet.setUserId(requestDto.getUserId());
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setCurrency(CurrencyType.INR);
        wallet.setStatus(WalletStatus.ACTIVE);
        wallet.setWalletNo("WALLET-" + System.currentTimeMillis());

        repository.save(wallet);

        response.setSuccess(true);
        response.setMessage("Wallet created successfully.");
        response.setData("Wallet created successfully.");
        response.setResponseStatus(HttpStatus.CREATED.value());
        response.setLocalDateTime(LocalDateTime.now());

        logger.info("Wallet created successfully for user: {}", requestDto.getUserId());

        return response;
    }

    @Transactional
    @Override
    public CommonResponse<WalletDto> depositMoney(CreditRequestDto requestDto) {
        logger.info("Amount crediting  for walletId : {}", requestDto.getWalletId());

        Wallet wallet = repository.findById(requestDto.getWalletId())
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found with id: " + requestDto.getWalletId()));

        if (wallet.getStatus() != WalletStatus.ACTIVE) {
            throw new WalletInactiveException("Wallet is not active.");
        }

        if (requestDto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be greater than zero.");
        }

        BigDecimal balanceBefore = wallet.getBalance();

        wallet.setBalance(wallet.getBalance().add(requestDto.getAmount()));
        wallet = repository.save(wallet);

        WalletDto walletDto = new WalletDto();
        walletDto.setWalletId(wallet.getId());
        walletDto.setBalanceBefore(balanceBefore);
        walletDto.setBalance(wallet.getBalance());

        logger.info("Wallet credited Money successfully for Wallet : {}", requestDto.getWalletId());
        return CommonResponse.<WalletDto>builder()
                .success(true)
                .message("Money credited successfully.")
                .data(walletDto)
                .responseStatus(HttpStatus.OK.value())
                .localDateTime(LocalDateTime.now())
                .build();
    }

    @Override
    public CommonResponse<WalletDto> withdrawMoney(WithdrawRequestDto requestDto) {
        logger.info("Amount Withdrawing for walletId : {}", requestDto.getWalletId());

        Wallet wallet = repository.findById(requestDto.getWalletId())
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found with id: " + requestDto.getWalletId()));

        if (wallet.getStatus() != WalletStatus.ACTIVE) {
            throw new WalletInactiveException("Wallet is not active.");
        }

        if (requestDto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be greater than zero.");
        }

        BigDecimal balanceBefore = wallet.getBalance();

        wallet.setBalance(wallet.getBalance().subtract(requestDto.getAmount()));
        wallet = repository.save(wallet);

        WalletDto walletDto = new WalletDto();
        walletDto.setWalletId(wallet.getId());
        walletDto.setBalanceBefore(balanceBefore);
        walletDto.setBalance(wallet.getBalance());

        logger.info("Wallet tWithdraw Money successfully for Wallet : {}", requestDto.getWalletId());
        return CommonResponse.<WalletDto>builder()
                .success(true)
                .message("Money withdraw successfully.")
                .data(walletDto)
                .responseStatus(HttpStatus.OK.value())
                .localDateTime(LocalDateTime.now())
                .build();
    }

}
