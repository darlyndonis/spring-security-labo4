package com.server.app.services;

import com.server.app.dto.response.Pagination;
import com.server.app.dto.response.PaginationMeta;
import com.server.app.entities.Activo;
import com.server.app.repositories.ActivoRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ActivoService {

    private final ActivoRepository activoRepository;

    @Transactional(readOnly = true)
    public Pagination<Activo> listarTodos(int page, int size) {
        Page<Activo> p = activoRepository.findAll(PageRequest.of(page, size));
        return new Pagination<>(p.getContent(),
                new PaginationMeta(p.getNumber(), p.getSize(), p.getTotalPages(), p.getTotalElements()));
    }
}