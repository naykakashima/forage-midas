package com.jpmc.midascore.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class TransactionRecord {

    @Id
    @GeneratedValue()
    private long id;

    @Column(nullable = false)
    private float amount;

    protected TransactionRecord() {

    }

    public TransactionRecord(long id, float amount) {
        this.id = id;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "TransactionRecord{" + "id=" + id + ", amount=" + amount + '}';
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }



}
