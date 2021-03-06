package com.mjuteam2.TeamOne.caution.domain;

import com.mjuteam2.TeamOne.caution.dto.CautionResponse;
import com.mjuteam2.TeamOne.member.domain.Member;
import com.mjuteam2.TeamOne.util.domain.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Caution extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "caution_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_member_id")
    private Member requestMember; // 차단을 요청한 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_member_id")
    private Member targetMember; // 차단된 사용자

    @Builder
    public Caution(Member requestMember, Member cautionedMember) {
        this.requestMember = requestMember;
        this.targetMember = cautionedMember;
    }

    public CautionResponse toResponse() {
        return CautionResponse.builder()
                .cautionId(id)
                .requestMember(requestMember.toResponse())
                .cautionedMember(targetMember.toResponse())
                .createdDate(getCreatedDate())
                .build();
    }
}
