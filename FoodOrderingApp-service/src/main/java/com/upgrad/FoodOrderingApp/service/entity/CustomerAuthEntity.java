package com.upgrad.FoodOrderingApp.service.entity;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "customer_auth")
@NamedQueries({
        @NamedQuery(name = "getEntityByToken", query = "SELECT u FROM CustomerAuthEntity u WHERE u.accessToken=:accessToken")
})
public class CustomerAuthEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "uuid")
    @Size(max = 200)
    private String uuid;

    @Column(name = "access_token")
    @Size(max = 500)
    private String accessToken;

    @Column(name = "login_at")
    private ZonedDateTime loginAt;

    @Column(name = "logout_at")
    private ZonedDateTime logoutAt;

    @Column(name = "expires_at")
    private ZonedDateTime expiresAt;

    @ManyToOne()
    @JoinColumn(name = "customer_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private CustomerEntity customer;

    public CustomerAuthEntity() {
    }

    public String toString() {
        String obj = "CustomerAuthEntity Object {\n";
        obj += "  id: " + this.id + ",\n";
        obj += "  uuid: " + this.uuid + ",\n";
        obj += "  accessToken: " + this.accessToken + ",\n";
        obj += "  customer: " + this.customer + ",\n";
        obj += "  loginAt: " + this.loginAt + ",\n";
        obj += "  logoutAt: " + this.logoutAt + ",\n";
        obj += "  expiresAt: " + this.expiresAt + ",\n";
        obj += "}";
        return obj;
    }

    public ZonedDateTime getExpiresAt() {
        return this.expiresAt;
    }

    public ZonedDateTime getLogoutAt() {
        return this.logoutAt;
    }

    public ZonedDateTime getLoginAt() {
        return this.loginAt;
    }

    public CustomerEntity getCustomer() {
        return this.customer;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public String getUuid() {
        return this.uuid;
    }

    public Integer getId() {
        return this.id;
    }

    public void setExpiresAt(ZonedDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public void setLogoutAt(ZonedDateTime logoutAt) {
        this.logoutAt = logoutAt;
    }

    public void setLoginAt(ZonedDateTime loginAt) {
        this.loginAt = loginAt;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
