package com.example.backend.board.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardFileDto {

    @Schema(description = "파일 원본 이름", example = "계획서.pdf")
    private String filesName;

    @Schema(description = "파일 타입", example = "file")
    private String filesType;

    @Schema(description = "S3 Presigned URL 또는 키", example = "2025/04/14/uuid_계획서.pdf")
    private String filesUrl;
}
