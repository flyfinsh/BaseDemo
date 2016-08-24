package com.meilicat.basedemo.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.meilicat.basedemo.R;
import com.meilicat.basedemo.activity.usercenter.DownloadService;

/**
 * Created by lizhiming on 2016/2/22.
 */
public class DialogUtils {

    public static void showCheckVersionDialog(final Context context, final String newVersionUrl) {

        final Dialog dialog = new Dialog(context, R.style.common_dialog_theme);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogContentView = inflater.inflate(R.layout.dialog_version_check, null);

        final boolean isHavNew;
        if(TextUtils.isEmpty(newVersionUrl)) {
            isHavNew = false;
        } else {
            isHavNew = true;
        }

        TextView contentTv = (TextView) dialogContentView.findViewById(R.id.check_version_content_tv);
        TextView cancelTv = (TextView) dialogContentView.findViewById(R.id.check_version_cancel_tv);
        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        View lineV = dialogContentView.findViewById(R.id.check_version_line);
        if(isHavNew) {
            contentTv.setText("有新版本发布，是否现在更新？");
            cancelTv.setVisibility(View.VISIBLE);
            lineV.setVisibility(View.VISIBLE);
        } else {
            contentTv.setText("您所使用的已是最新版本");
            cancelTv.setVisibility(View.GONE);
            lineV.setVisibility(View.GONE);
        }

        TextView confirmTv = (TextView) dialogContentView.findViewById(R.id.check_version_ok_tv);
        confirmTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isHavNew) {
                    Intent intent = new Intent(context, DownloadService.class);
                    intent.putExtra("url", newVersionUrl);
                    context.startService(intent);
                }
                dialog.dismiss();
            }
        });

        dialog.setContentView(dialogContentView);

        int topMargin = DeviceConfiger.dp2px(150);
        int width = DeviceConfiger.dp2px(300);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.TOP);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = topMargin;
        lp.width = width;
        dialogWindow.setAttributes(lp);

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

}
