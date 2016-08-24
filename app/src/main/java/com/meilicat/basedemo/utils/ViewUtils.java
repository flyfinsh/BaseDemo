package com.meilicat.basedemo.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.widget.Toast;

import com.meilicat.basedemo.base.BaseApplication;
import com.meilicat.basedemo.view.DividerDrawable;

/**
 * Created by lizhiming on 2016/1/25.
 */
public class ViewUtils {

    /**
     * 创建选中有分割线的TAB条目背景，未选中为透明。
     *
     *            选中分割线的颜色。
     * @return
     */
    public static Drawable createTabItemDividerBg(int dividerColor) {
        int hight = DeviceConfiger.dp2px(5);
        // 默认
        ColorDrawable normalDrawable = new ColorDrawable(Color.parseColor("#00000000"));
        // 选中
        DividerDrawable selectDrawable = new DividerDrawable();
        selectDrawable.setDividerColor(dividerColor);
        selectDrawable.setStrokeWidth(hight);
        Drawable itemDrawable = addStateDrawable(//
                new int[] { android.R.attr.state_selected, android.R.attr.state_checked, -1 },//
                new Drawable[] { selectDrawable, selectDrawable, normalDrawable });
        return itemDrawable;
    }

    /**
     * 创建StateListDrawablw对象。
     *
     * @param states
     * @param drawables
     * @return StateListDrawable 返回类型
     */
    private static StateListDrawable addStateDrawable(int[] states, Drawable[] drawables) {
        StateListDrawable sd = new StateListDrawable();
        Drawable normalDrawable = null;
        // 注意该处的顺序，只要有一个状态与之相配，背景就会被换掉
        // 所以不要把大范围放在前面了，如果sd.addState(new[]{},normal)放在第一个的话，就没有什么效果了
        for (int i = 0; i < states.length; i++) {
            if (states[i] != -1) {
                sd.addState(new int[] { android.R.attr.state_enabled, states[i] }, drawables[i]);
                sd.addState(new int[] { states[i] }, drawables[i]);
            } else {
                normalDrawable = drawables[i];
            }
        }
        sd.addState(new int[]{android.R.attr.state_enabled}, normalDrawable);
        sd.addState(new int[]{}, normalDrawable);
        return sd;
    }


    /**
     * 创建tab条目的文字颜色
     *
     * @param normalColor
     * @param selectColor
     * @return
     */
    public static ColorStateList createTabItemTextColor(int normalColor, int selectColor) {
        return createColorStateList(new int[]{android.R.attr.state_selected, android.R.attr.state_checked,
                android.R.attr.state_pressed, -1}, new int[]{selectColor, selectColor, selectColor, normalColor});
    }

    /** 对TextView设置不同状态时其文字颜色。 */
    private static ColorStateList createColorStateList(int[] states, int[] colors) {
        int[] tempColors = colors;
        int[][] tempStates = new int[colors.length][];

        for (int i = 0; i < tempColors.length; i++) {
            if (states[i] == -1) {
                tempStates[i] = new int[] { android.R.attr.state_enabled };
            } else {
                tempStates[i] = new int[] { android.R.attr.state_enabled, states[i] };
            }
        }
        ColorStateList colorList = new ColorStateList(tempStates, tempColors);
        return colorList;
    }

    /**
     * 获取app版本号
     *
     * @return
     */
    public static String getVersionName() {
        PackageManager manager = UIUtils.getContext().getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(UIUtils.getContext().getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void showToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }
}
