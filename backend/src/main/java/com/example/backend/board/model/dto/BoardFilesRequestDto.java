package com.example.backend.board.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardFilesRequestDto {
    private Long boardIdx;
    private String filesName;
    private String filesUrl;
    private String filesType;
}

