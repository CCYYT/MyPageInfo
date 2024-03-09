package com.github.mypageinfo.util;


import com.github.mypageinfo.sql.FieldNameConventionConverter;

/*
* 小驼峰 与 下划线命名 的互相转换
* */
public class NameConventionConverter implements FieldNameConventionConverter {
    @Override
    public String convertToNewFieldNameFormat(String oldVariableName) {
        StringBuilder snakeCase = new StringBuilder();
        for (int i = 0; i < oldVariableName.length(); i++) {
            char c = oldVariableName.charAt(i);
            if (Character.isUpperCase(c)) {
                snakeCase.append("_");
                snakeCase.append(Character.toLowerCase(c));
            } else {
                snakeCase.append(c);
            }
        }
        return snakeCase.toString();
    }

    @Override
    public String revertToOriginalFieldNameFormat(String newVariableName) {
        StringBuilder camelCase = new StringBuilder();
        boolean nextUpperCase = false;
        for (int i = 0; i < newVariableName.length(); i++) {
            char c = newVariableName.charAt(i);
            if (c == '_') {
                nextUpperCase = true;
            } else {
                if (nextUpperCase) {
                    camelCase.append(Character.toUpperCase(c));
                    nextUpperCase = false;
                } else {
                    camelCase.append(Character.toLowerCase(c));
                }
            }
        }
        return camelCase.toString();
    }
}
