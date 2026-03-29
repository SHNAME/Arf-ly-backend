package com.capstone.arfly.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //일반 로그인을 받을 때는 특수문자는 제외한다.
    @Column(nullable = false, unique = true)
    private String userId;

    //System
    private String password;

    @Builder.Default
    @Column(nullable = false, unique = true)
    private String nickName = String.valueOf(UUID.randomUUID());

    @Column(unique = true)//null 가능
    private String phoneNumber;

    @Column(unique = true) //null 가능 -> Oauth에는 전화번호 인증이 필요하지 않기 때문에
    private String firebaseUid;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.USER;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    private String socialId;

    @Builder.Default
    @Column(nullable = false)
    private double latitude = 35.832870;

    @Builder.Default
    @Column(nullable = false)
    private double longitude = 128.757416;

    //null = false;
    private String road_address;

    @Builder.Default
    private boolean notificationEnabled = true;

}