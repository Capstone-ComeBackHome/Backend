package com.comebackhome.disease.domain;


import com.comebackhome.common.domain.BaseEntity;
import com.comebackhome.user.domain.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Diagnosis extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID",nullable = false)
    private User user;

    public static Diagnosis from(Long userId){
        return Diagnosis.builder()
                .user(User.builder().id(userId).build())
                .build();
    }

}
