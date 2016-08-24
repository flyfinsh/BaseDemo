package com.meilicat.basedemo.view;

import android.content.Context;
import android.graphics.Color;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meilicat.basedemo.R;
import com.meilicat.basedemo.utils.LogUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by cj on 2016/1/21.
 * 自定义一个输入框
 *  TODO 这个输入框暂时没有用。。。。以后再删除他吧
 */
public class CustomEditText extends LinearLayout {


    @Bind(R.id.custom_editext_iv1)
    IconTextView mCustomEditextIv1;
    @Bind(R.id.custom_editext_edit)
    EditText mCustomEditextEdit;
    @Bind(R.id.custom_editext_iv2)
    TextView mCustomEditextIv2;
    @Bind(R.id.custom_editext_iv3)
    TextView mCustomEditextIv3;
    @Bind(R.id.custom_edittext)
    LinearLayout mCustomEdittext;

    public CustomEditText(Context context) {
        super(context);
        initView(context);

        initEvent();
    }

    /**
     * 初始化事件
     */
    private void initEvent() {


    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    /**
     * 初始化view
     */
    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.custom_edittext, this);
        LogUtils.i("hero", "初始化view");
        ButterKnife.bind(this);

        mCustomEditextEdit.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    //如果有焦点,

                    mCustomEdittext.setBackground(getResources().getDrawable(R.drawable.bg_edittext_focused));
                    changeIvState(true);
                } else {
                    String trim = mCustomEditextEdit.getText().toString().trim();
                    if (TextUtils.isEmpty(trim)) {
                        //说明是空的
                        mCustomEdittext.setBackground(getResources().getDrawable(R.drawable.bg_edittext_normal));
                        changeIvState(false);
                    }
                    changeIvState(false);
                }
            }
        });

    }

    private void changeIvState(boolean isSelected) {
        if (isSelected) {
            //说明是选中状态
            mCustomEditextIv1.setTextColor(Color.RED);
//            mCustomEditextIv3.setSelected(true);
//            mCustomEditextIv2.setSelected(true);
        } else {
            mCustomEditextIv1.setTextColor(Color.GRAY);
//            mCustomEditextIv3.setSelected(false);
//            mCustomEditextIv2.setSelected(false);
        }
    }

    /**
     * 这是设置提示字的方法
     */
    public void setHint(String hint) {
        mCustomEditextEdit.setHint(hint);
    }

    /**
     * 获得输入内容的方法
     */
    public String getText() {
        return mCustomEditextEdit.getText().toString();
    }

    /**
     * 提供设置第二个图片隐藏与否的方法
     */
    public void setIV2Gone() {
        mCustomEditextIv2.setVisibility(View.INVISIBLE);
    }

    /**
     * 提供设置第一个控件的图片的方法
     * 传入一个string的资源id
     */
    public void setIV1Src(int resId) {
        mCustomEditextIv1.setText(resId);
    }

    /**
     * 设置第三个控件的图片的方法
     * 传入一个string的资源id
     */
    public void setIV3Src(int resId) {
        mCustomEditextIv3.setText(resId);
    }

    /**
     * 设置第二个图片的点击事件
     */
    public void setIV2OnClick() {
        mCustomEditextIv2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击的时候，就删除edittext中的文字
                mCustomEditextEdit.setText("");
            }
        });
    }

    /**
     * 设置第三个图标的点击事件
     */

    public void setIV3OnClick(OnClickListener listener) {
        mCustomEditextIv3.setOnClickListener(listener);
    }

    /**
     * 设置edittext是否明文
     */

    public void setContentShow(boolean isShow) {
        if (isShow) {
            //说明 要显示明文
            mCustomEditextEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else {
            mCustomEditextEdit.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }
    }

    /**
     * 外部去设置EditTex的内容
     */
    public void setContent(String content) {
        mCustomEditextEdit.setText(content);
    }

}
