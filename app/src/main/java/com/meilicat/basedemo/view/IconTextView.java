package com.meilicat.basedemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.meilicat.basedemo.utils.FontsManager;


/**
 * Created by baoli on 2015/12/4.
 */
public class IconTextView extends TextView{
    public IconTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public IconTextView(Context context) {
        super(context);
        init(context);
    }

    public IconTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    private void init(Context context){
        this.setTypeface(FontsManager.getTypeface(context));
    }

}
