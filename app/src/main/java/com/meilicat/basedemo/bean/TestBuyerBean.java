package com.meilicat.basedemo.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cj on 2016/2/15.
 * 这是用于测试的买手模块的数据bean
 */
public class TestBuyerBean {
    public String brandName;
    public String brandUrl;
    public String brandType;
    public String brandAddress;
    public String brandSaler;
    public String brandNumber;
    public boolean brandCheck;

    public List<TestBean> orderData = new ArrayList<>();

}
