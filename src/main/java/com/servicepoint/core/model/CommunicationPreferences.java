package com.servicepoint.core.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor

@Setter
@Getter

@Entity
@Table(name = "communication_preferences")
public class CommunicationPreferences {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer preferenceId;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean email;

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean sms;

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean appNotifications;

}
