package com.ruoyi.common.core.domain.entity;


import lombok.Data;

import java.util.List;

@Data
public class Diction {
    private String id;
    private String label;
    private String value;
    private String parentId;
    private List<Diction> children;
    private int sortNum;
}
