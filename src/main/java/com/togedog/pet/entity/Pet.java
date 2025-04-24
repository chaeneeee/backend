package com.togedog.pet.entity;

import com.togedog.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long petId;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    public void setMember(Member member) {
        this.member = member;
        if (!member.getPets().contains(this)) {
            member.addPet(this);
        }
    }

    @Column(name = "pet_name", nullable = false)
    private String petName;

    @Column(name = "pet_birth", nullable = false)
    private String petBirth;

    @Column(name = "pet_breed", nullable = false)
    private String petBreed;

    @Column(name = "pet_personality", nullable = false)
    private String petPersonality;

    @Column(name = "pet_profile", nullable = false)
    private String petProfileImage;

    @Column(name = "pet_neutered", nullable = false)
    private String petNeutered;

    @Column(name = "pet_gender", nullable = false)
    private String petGender;

    @Column(name = "pet_size", nullable = false)
    private String petSize;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
