package com.digitalwallet.wallet.service.serviceImplementaion;

import com.digitalwallet.wallet.dto.requestDto.CreditRequestDto;
import com.digitalwallet.wallet.dto.requestDto.WalletCreateRequestDto;
import com.digitalwallet.wallet.dto.requestDto.WithdrawRequestDto;
import com.digitalwallet.wallet.entity.Transaction;
import com.digitalwallet.wallet.entity.Wallet;
import com.digitalwallet.wallet.enums.CurrencyType;
import com.digitalwallet.wallet.enums.TransactionStatus;
import com.digitalwallet.wallet.enums.TransactionType;
import com.digitalwallet.wallet.enums.WalletStatus;
import com.digitalwallet.wallet.exception.InvalidAmountException;
import com.digitalwallet.wallet.exception.WalletAlreadyExistsException;
import com.digitalwallet.wallet.exception.WalletInactiveException;
import com.digitalwallet.wallet.exception.WalletNotFoundException;
import com.digitalwallet.wallet.repository.TransactionRepo;
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
import java.util.UUID;

@Service
@Transactional
public class WalletServiceImp implements WalletService {

    private static final Logger logger = LoggerFactory.getLogger(WalletServiceImp.class);

    private final WalletRepository repository;
    private final TransactionRepo transactionRepo;

    @Autowired
    public WalletServiceImp(WalletRepository repository, TransactionRepo transactionRepo){
        this.repository = repository;
        this.transactionRepo = transactionRepo;
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
    public CommonResponse<String> depositMoney(CreditRequestDto requestDto) {
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

        addTransaction( wallet, requestDto.getAmount(), TransactionType.WITHDRAW, balanceBefore, wallet.getBalance()
        );

        logger.info("Wallet credited Money successfully for Wallet : {}", requestDto.getWalletId());
        return CommonResponse.<String>builder()
                .success(true)
                .message("Money credited successfully.")
                .data("Money credited successfully.")
                .responseStatus(HttpStatus.OK.value())
                .localDateTime(LocalDateTime.now())
                .build();
    }

    @Override
    public CommonResponse<String> withdrawMoney(WithdrawRequestDto requestDto) {
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

        addTransaction( wallet, requestDto.getAmount(), TransactionType.DEPOSIT, balanceBefore, wallet.getBalance()
        );

        logger.info("Wallet tWithdraw Money successfully for Wallet : {}", requestDto.getWalletId());
        return CommonResponse.<String>builder()
                .success(true)
                .message("Money withdraw successfully.")
                .data("Money withdraw successfully.")
                .responseStatus(HttpStatus.OK.value())
                .localDateTime(LocalDateTime.now())
                .build();
    }

    private void addTransaction(Wallet wallet, BigDecimal amount, TransactionType transactionType, BigDecimal balanceBefore, BigDecimal balance) {
        Transaction transaction = Transaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .transactionType(transactionType)
                .walletId(wallet.getId())
                .amount(amount)
                .balanceBefore(balanceBefore)
                .balanceAfter(wallet.getBalance())
                .status(TransactionStatus.SUCCESS)
                .build();


        transactionRepo.save(transaction);
    }

}
