package com.zo.platform.starter.console.version;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhoump
 * @date 2026/11/6
 * @purpose
 */
@Data
@AllArgsConstructor
public class Version implements Serializable {

    private static final long serialVersionUID = 5499506349925884959L;

    String name;
    String code;


    @Override
    public String toString() {
        return this.name + "\t" + this.code + "\t" +"已加载";
    }


}
