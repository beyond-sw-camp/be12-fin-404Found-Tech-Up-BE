package com.example.backend.noti.service;

import com.example.backend.noti.model.Noti;
import com.example.backend.noti.model.dto.NotiRequestDto;
import com.example.backend.noti.model.dto.NotiResponseDto;
import com.example.backend.noti.repository.NotiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class NotiService {
    private final NotiRepository notiRepository;

    public void create(NotiRequestDto dto) {
        notiRepository.save(dto.toEntity());
    }

    public List<NotiResponseDto> list() {
        List<Noti> result = notiRepository.findAll();

        return result.stream().map(NotiResponseDto::from).toList();
    }

    public NotiResponseDto read(Long notiIdx) {
        Noti noti = notiRepository.findById(notiIdx).orElseThrow();
        return NotiResponseDto.from(noti);
    }
}
