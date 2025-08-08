package com.perfact.be.domain.alt.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ClovaSearchResponseDto {
    private Status status;
    private Result result;

    @Getter
    @Setter
    @ToString
    public static class Status {
        private String code;
        private String message;
    }

    @Getter
    @Setter
    @ToString
    public static class Result {
        private Message message;
        private int inputLength;
        private int outputLength;
        private String stopReason;
        private long seed;
    }

    @Getter
    @Setter
    @ToString
    public static class Message {
        private String role;
        private String content;
    }
}
