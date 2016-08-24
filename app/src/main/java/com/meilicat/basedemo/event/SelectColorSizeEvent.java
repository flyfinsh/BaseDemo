package com.meilicat.basedemo.event;

/**
 * Created by lizhiming on 2016/3/3.
 */
public class SelectColorSizeEvent {
    public String mColor;
    public String mSize;
    public SelectColorSizeEvent(String color, String size){
        super();
        this.mColor = color;
        this.mSize = size;
    }
}
