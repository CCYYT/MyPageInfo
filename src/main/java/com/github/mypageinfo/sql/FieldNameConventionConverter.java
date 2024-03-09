package com.github.mypageinfo.sql;

public interface FieldNameConventionConverter {

    String convertToNewFieldNameFormat(String oldVariableName);

    String revertToOriginalFieldNameFormat(String newVariableName);
}
