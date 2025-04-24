package com.togedog.board.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
@DiscriminatorValue("I")
@Getter
@Setter
public class BoardInquiry extends Board {

    private String statusDescription = BoardInquiryStatus.RECEIVED.getStatusDescription();

    @Enumerated(value = EnumType.STRING)
    private BoardInquiryStatus boardInquiryStatus = BoardInquiryStatus.RECEIVED;

    @AllArgsConstructor
    public enum BoardInquiryStatus {
        RECEIVED("신고&문의 수신 완료"),
        COMPLETED("신고&문의 처리 완료");

        @Getter
        private String statusDescription;
    }
    public void setBoardInquiryStatus(BoardInquiryStatus boardInquiryStatus) {
        this.boardInquiryStatus = boardInquiryStatus;
        this.statusDescription = boardInquiryStatus.getStatusDescription();
    }
}