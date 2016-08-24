package com.meilicat.basedemo.event;

/**
 * Created by lizhiming on 2016/3/3.
 */
public class SelectTypeEvent {
    public String mIds;
    public String mTypeText;
    public SelectTypeEvent(String ids, String typeText){
        super();
        this.mIds = ids;
        this.mTypeText = typeText;
    }
}
