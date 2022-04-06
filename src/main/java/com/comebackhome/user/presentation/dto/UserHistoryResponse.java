package com.comebackhome.user.presentation.dto;

import com.comebackhome.user.domain.Sex;
import com.comebackhome.user.domain.User;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserHistoryResponse {

    private int age;

    private Sex sex;

    private int height;

    private int weight;

    private String history;

    private String FamilyHistory;

    private String drugHistory;

    private String socialHistory;

    private String traumaHistory;

    public static UserHistoryResponse from(User user){
        return UserHistoryResponse.builder()
                .age(user.getAge())
                .sex(user.getSex())
                .height(user.getHeight())
                .weight(user.getWeight())
                .history(user.getHistory())
                .FamilyHistory(user.getFamilyHistory())
                .drugHistory(user.getDrugHistory())
                .socialHistory(user.getSocialHistory())
                .traumaHistory(user.getTraumaHistory())
                .build();
    }
}
