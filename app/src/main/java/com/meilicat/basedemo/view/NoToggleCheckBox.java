package com.meilicat.basedemo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.CheckBox;

public class NoToggleCheckBox extends CheckBox {

    public NoToggleCheckBox(Context context)
    {
        super(context);
    }

    public NoToggleCheckBox(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public NoToggleCheckBox(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    // 复写父类的toggle方法点击不自动切换状态
    @Override
    public void toggle() {
        // super.toggle();

    }

    public void manualTaggle() {
        super.toggle();
//        setChecked(!isChecked());
    }

}
