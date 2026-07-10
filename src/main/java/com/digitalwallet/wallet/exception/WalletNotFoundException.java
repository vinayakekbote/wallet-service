package com.digitalwallet.wallet.exception;

public class WalletNotFoundException extends RuntimeException{

    public WalletNotFoundException(String message) {
        super(message);
    }
}
