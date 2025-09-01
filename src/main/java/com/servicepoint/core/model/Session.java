package com.servicepoint.core.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor

@Setter
@Getter

@Entity
@Table(name = "sessions")
public class Session {

    @Id
    private String sessionId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String refreshToken;

    @Column(nullable = false)
    private String userAgent;

    @Column(nullable = false)
    private String clientIp;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isBlocked;

    @Column(nullable = false)
    private Timestamp expiresAt;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;

}
