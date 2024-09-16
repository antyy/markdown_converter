package com.converter.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MapperConfig {
    private String version;
    private String sourceFormat;
    private String targetFormat;
    private List<Mapping> mappings;
}
