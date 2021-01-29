package com.upgrad.quora.service.entity;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "user_auth")
@NamedQueries(
        {
                @NamedQuery(name = "userAuthEntityByAccessToken", query = "select u from UserAuthEntity u where u.accessToken = :accessToken")
        }
)
public class UserAuthEntity {

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "uuid")
    private String uuid;

    @JoinColumn(name = "user_id")
    @ManyToOne
    private UserEntity userId;

    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "expires_at")
    private ZonedDateTime expiresAt;

    @Column(name = "logout_at")
    private ZonedDateTime logoutAt;

    @Column(name = "login_at")
    private ZonedDateTime loginAt;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public UserEntity getUserId() {
        return userId;
    }

    public void setUserId(UserEntity userId) {
        this.userId = userId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public ZonedDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(ZonedDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public ZonedDateTime getLogoutAt() {
        return logoutAt;
    }

    public void setLogoutAt(ZonedDateTime logoutAt) {
        this.logoutAt = logoutAt;
    }

    public ZonedDateTime getLoginAt() {
        return loginAt;
    }

    public void setLoginAt(ZonedDateTime loginAt) {
        this.loginAt = loginAt;
    }
}
