package com.example.backend.search;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final ProductIndexRepository productIndexRepository;
}
