package com.capstone.arfly.diagnosis.domain;


import com.capstone.arfly.common.domain.BaseCreatedEntity;
import com.capstone.arfly.pet.domain.Pet;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DiagnosisReport extends BaseCreatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @Column(nullable = false)
    private String diseaseName;

    @Column(nullable = false)
    private Double probability;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String management;

}
