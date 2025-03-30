package com.harbourtech.cryptoworld.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

@Service
public class TransactionValidator {

    private final Web3j web3;

    public TransactionValidator() {
        this.web3 = Web3j.build(new HttpService("https://ethereum.publicnode.com"));
    }

    public TransactionInfo getTransactionInfo(String txHash) {
        try {
            EthTransaction ethTransaction = web3.ethGetTransactionByHash(txHash).send();
            Transaction tx = ethTransaction.getTransaction().orElse(null);

            if (tx == null) {
                throw new IllegalArgumentException("Transaction not found for hash: " + txHash);
            }

            TransactionReceipt receipt = web3.ethGetTransactionReceipt(txHash).send()
                    .getTransactionReceipt()
                    .orElse(null);

            if (receipt == null) {
                throw new IllegalStateException("Transaction not yet mined or receipt unavailable");
            }

            if (!"0x1".equals(receipt.getStatus())) {
                throw new IllegalStateException("Transaction failed (status not 0x1)");
            }

            String from = tx.getFrom();
            String to = tx.getTo();
            BigInteger valueInWei = tx.getValue();
            BigDecimal valueInEth = new BigDecimal(valueInWei).divide(BigDecimal.TEN.pow(18));

            return new TransactionInfo(from, to, valueInEth);

        } catch (IOException e) {
            throw new RuntimeException("Blockchain communication error", e);
        }
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TransactionInfo {
        private String from;
        private String to;
        private BigDecimal amountEth;
    }
}
