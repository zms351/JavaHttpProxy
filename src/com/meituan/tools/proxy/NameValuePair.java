package com.meituan.tools.proxy;

public class NameValuePair {
    
    public NameValuePair(String name,String value) {
        this.name=name;
        this.value=value;
        assert (name!=null && name.length()>0);
    }
    
    private String name;
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        String s=this.name;
        if(value!=null) {
            s+=": "+value;
        }
        return s;
    }
    
}
