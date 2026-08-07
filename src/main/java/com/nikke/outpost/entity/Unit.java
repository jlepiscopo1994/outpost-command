package com.nikke.outpost.entity;

import com.nikke.outpost.enums.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "units")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Unit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "origin_ip", nullable = false)
    private String originIp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Manufacturer manufacturer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Element element;

    @Enumerated(EnumType.STRING)
    @Column(name = "weapon_type", nullable = false)
    private WeaponType weaponType;

    @Enumerated(EnumType.STRING)
    @Column(name = "burst_type", nullable = false)
    private BurstType burstType;

    @Enumerated(EnumType.STRING)
    @Column(name = "class_type", nullable = false)
    private ClassType classType;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "unit", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TacticalLog> tacticalLogs = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

}
