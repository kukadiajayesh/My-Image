package com.app.photobook.uploadhelper;

public class CustomParam {

    String fieldName, value;
    FIELDTYPE fieldType;

    public CustomParam(String fieldName, String value, FIELDTYPE fieldType) {
        this.fieldName = fieldName;
        this.value = value;
        this.fieldType = fieldType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getValue() {
        return value;
    }

    public FIELDTYPE getFieldType() {
        return fieldType;
    }

    public static enum FIELDTYPE {
        STRING, FILE
    }

}
