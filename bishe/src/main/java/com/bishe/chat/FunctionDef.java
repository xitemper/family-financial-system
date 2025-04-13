package com.bishe.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class FunctionDef {
    private String name;
    private String description;
    private Map<String, ParameterDef> parameters = new LinkedHashMap<>();

    public FunctionDef(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public FunctionDef() {

    }

    public FunctionDef addParameter(String name, String type, String description) {
        parameters.put(name, new ParameterDef(type, description));
        return this;
    }

    @Data
    @AllArgsConstructor
    public static class ParameterDef {
        private String type;
        private String description;
    }
}