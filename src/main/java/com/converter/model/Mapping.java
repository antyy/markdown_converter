package com.converter.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Mapping {
    private String pattern;
    private String format;
    private String nodeStart;
    private String nodeEnd;
    private Boolean node;
}
