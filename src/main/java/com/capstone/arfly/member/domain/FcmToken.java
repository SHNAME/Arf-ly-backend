package com.capstone.arfly.member.domain;

import com.capstone.arfly.common.domain.BaseCreatedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class FcmToken extends BaseCreatedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "member_id",nullable = false)
    private Member member;


    @Column(nullable = false,unique = true)
    private String token;


    @Enumerated(EnumType.STRING)
    private DeviceType deviceType;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime lastLoginAt= LocalDateTime.now();








}
