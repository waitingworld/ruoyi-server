package com.ruoyi.common.core.domain.entity;

import org.springframework.security.core.GrantedAuthority;

public class SysGrantedAuthority implements GrantedAuthority {
    private String name;

    public SysGrantedAuthority() {
    }

    public SysGrantedAuthority(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getAuthority() {
        return name;
    }
}
