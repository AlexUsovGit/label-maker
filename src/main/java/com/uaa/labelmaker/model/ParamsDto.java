package com.uaa.labelmaker.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ParamsDto {
    private String importer;
    private String producent;
    private String country;
    private String tp;
    private String gost;
    private String garant;
    private String sex;
    private String color;
}
