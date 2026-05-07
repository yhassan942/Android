package com.example.android.model;
import java.io.Serializable;
import java.util.Locale;

public class Tag implements Serializable {
    public enum Type { PERSON, LOCATION }
    private Type type;
    private String value;

    public Tag() {}

    public Tag(Type type, String value) {
        this.type = type;
        this.value = value;
    }

    public Type getType() { return type; }
    public String getValue() { return value; }
    public String getValueLower() { return value == null ? "" : value.toLowerCase(Locale.ROOT); }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Tag)) return false;
        Tag t = (Tag)o;
        return type == t.type && getValueLower().equals(t.getValueLower());
    }

    @Override
    public int hashCode() {
        return (type.name() + ":" + getValueLower()).hashCode();
    }
}
