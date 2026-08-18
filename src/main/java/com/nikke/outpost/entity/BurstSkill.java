package com.nikke.outpost.entity;

import com.nikke.outpost.enums.BurstType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BurstSkill {

    @Column(name = "name")
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "burst_type")
    BurstType burstType;

    // Number of seconds before burst skill can be used again
    @Column(name = "cooldown")
    private Integer cooldown;
}
