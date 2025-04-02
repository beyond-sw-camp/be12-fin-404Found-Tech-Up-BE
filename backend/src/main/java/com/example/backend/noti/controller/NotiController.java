package com.example.backend.noti.controller;

import com.example.backend.noti.model.dto.NotiRequestDto;
import com.example.backend.noti.model.dto.NotiResponseDto;
import com.example.backend.noti.service.NotiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/noti")
@Tag(name = "알림 기능", description = "알림 관리 API")
public class NotiController {
    private final NotiService notiService;

    @Operation(
            summary = "알림 등록",
            description = "제목, 내용과 함께 알림을 등록합니다."
    )
    @PostMapping("/create")
    public void create(@RequestBody NotiRequestDto dto) {
        notiService.create(dto);
    }

    @Operation(
            summary = "알림 리스트보기",
            description = "알림 목록을 확인합니다."
    )
    @GetMapping("/list")
    public ResponseEntity<List<NotiResponseDto>> list() {
        List<NotiResponseDto> response = notiService.list();

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "알림 상세보기",
            description = "notiIdx를 전달받아 알림 하나의 정보를 확인합니다."
    )
    @GetMapping("/read/{notiIdx}")
    public ResponseEntity<NotiResponseDto> read(@PathVariable Long notiIdx) {
        NotiResponseDto response = notiService.read(notiIdx);

        return ResponseEntity.ok(response);
    }
}
