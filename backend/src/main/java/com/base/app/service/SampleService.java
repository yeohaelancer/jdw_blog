package com.base.app.service;

import com.base.app.dto.SampleDto;
import com.base.app.mapper.SampleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SampleService {

    private final SampleMapper sampleMapper;

    public List<SampleDto> findAll() {
        return sampleMapper.findAll();
    }

    public SampleDto findById(Long id) {
        return sampleMapper.findById(id);
    }

    @Transactional
    public SampleDto create(SampleDto sampleDto) {
        sampleMapper.insert(sampleDto);
        return sampleDto;
    }

    @Transactional
    public void update(Long id, SampleDto sampleDto) {
        sampleDto.setId(id);
        sampleMapper.update(sampleDto);
    }

    @Transactional
    public void delete(Long id) {
        sampleMapper.deleteById(id);
    }

}
