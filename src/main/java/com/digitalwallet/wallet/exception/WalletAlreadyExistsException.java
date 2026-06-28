package com.digitalwallet.wallet.exception;

public class WalletAlreadyExistsException extends RuntimeException {

    public WalletAlreadyExistsException(String message){
        super(message);
    }

}
