package com.base.app.mapper;

import com.base.app.dto.SampleDto;

import java.util.List;

public interface SampleMapper {

    List<SampleDto> findAll();

    SampleDto findById(Long id);

    int insert(SampleDto sampleDto);

    int update(SampleDto sampleDto);

    int deleteById(Long id);

}
