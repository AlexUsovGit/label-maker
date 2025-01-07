package com.uaa.labelmaker.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class ProductData {
    @JsonProperty("productGroupType")
    private String productGroupType;

    @JsonProperty("gtinProductAttributes")
    private Map<String, ProductAttributes> gtinProductAttributes;

    @Data
    public static class ProductAttributes {
        private String inn;
        private String name;
        private String brand;
        private String color;
        private boolean isKit;
        private boolean isSet;
        private String level;
        private String model;
        private String fullName;
        private boolean isSimple;
        private String tnVedCode;
        private boolean isTechGtin;
        private int multiplier;
        private String packageType;
        private String productSize;
        private String tnVedCode10;
        private boolean goodMarkFlag;
        private boolean goodTurnFlag;
        private String materialDown;
        private long ncCreateDate;
        private int productGroup;
        private long firstSignDate;
        private String materialUpper;
        private String materialLining;
        private String productTypeDesc;
    }
}
