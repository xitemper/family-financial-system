package com.bishe.dto;

import java.time.LocalDate;

public class TransactionSummary {
    private LocalDate transactionDate;
    private double totalAmount;

    public TransactionSummary(LocalDate transactionDate, double totalAmount) {
        this.transactionDate = transactionDate;
        this.totalAmount = totalAmount;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }
}