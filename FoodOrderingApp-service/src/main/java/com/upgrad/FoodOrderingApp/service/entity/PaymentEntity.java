package com.upgrad.FoodOrderingApp.service.entity;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "payment")
@NamedQueries({
        @NamedQuery(name = "getPayments", query = "select p from PaymentEntity p"),
        @NamedQuery(name = "paymentById", query = "select p from PaymentEntity p where p.id=:id"),
        @NamedQuery(name = "paymentByUuid", query = "select p from PaymentEntity p where p.uuid=:uuid"),
})
public class PaymentEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "uuid")
    private UUID uuid;

    @Column(name = "payment_name")
    private String paymentName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getPaymentName() {
        return paymentName;
    }

    public void setPaymentName(String paymentName) {
        this.paymentName = paymentName;
    }
}
