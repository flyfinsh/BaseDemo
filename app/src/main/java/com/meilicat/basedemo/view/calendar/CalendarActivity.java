package com.meilicat.basedemo.view.calendar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ViewFlipper;

import com.google.gson.Gson;
import com.meilicat.basedemo.R;
import com.meilicat.basedemo.base.BaseActivity;
import com.meilicat.basedemo.bean.LoginBean;
import com.meilicat.basedemo.conf.Constants;
import com.meilicat.basedemo.utils.DeviceConfiger;
import com.meilicat.basedemo.utils.HttpManager;
import com.meilicat.basedemo.utils.LogUtils;
import com.meilicat.basedemo.utils.T;
import com.meilicat.basedemo.utils.UIUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日历显示activity
 * 
 * @author cj
 * 
 */
public class CalendarActivity extends BaseActivity implements View.OnClickListener {

	private GestureDetector gestureDetector = null;
	private CalendarAdapter calV = null;
	private ViewFlipper flipper = null;
	private GridView gridView = null;
	private static int jumpMonth = 0; // 每次滑动，增加或减去一个月,默认为0（即显示当前月）
	private static int jumpYear = 0; // 滑动跨越一年，则增加或者减去一年,默认为0(即当前年)
	private int year_c = 0;
	private int month_c = 0;
	private int day_c = 0;
	private String currentDate = "";
	/** 每次添加gridview到viewflipper中时给的标记 */
	private int gvFlag = 0;
	/** 当前的年月，现在日历顶端 */
	private TextView currentMonth;
	/** 上个月 */
	private ImageView prevMonth;
	/** 下个月 */
	private ImageView nextMonth;

	/** 记录当前选择的日期*/
	private String mCurrentDate = "";
	private Button mOrderConfirm;
	private Button mOrderCancel;
	private Button mOrderNotPru;

	private Intent mIntent;
	private final long mCurrentTime;
	private String mSelectDay;
	private String mSelectTime;
	private Dialog mComfirmDialog;
	private String mId;
	private int mType;
	private Dialog mNoPur;

	public CalendarActivity() {
		LogUtils.i("日历的构造函数----");
		Date date = new Date();
		//获取当前的毫秒值

		mCurrentTime = System.currentTimeMillis();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
		currentDate = sdf.format(date); // 当期日期
		/**static的生命周期太长，需要重置一下下*/
		jumpMonth = 0;
		jumpYear = 0;

		LogUtils.i(currentDate);
		year_c = Integer.parseInt(currentDate.split("-")[0]);
		month_c = Integer.parseInt(currentDate.split("-")[1]);
		day_c = Integer.parseInt(currentDate.split("-")[2]);

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calendar);

		Intent intent = getIntent();
		mId = intent.getStringExtra("id");
		mType = intent.getIntExtra("type", -1);

		currentMonth = (TextView) findViewById(R.id.currentMonth);
		prevMonth = (ImageView) findViewById(R.id.prevMonth);
		nextMonth = (ImageView) findViewById(R.id.nextMonth);
		mOrderConfirm = (Button) findViewById(R.id.order_unhandler_calendar_confirm);
		mOrderCancel = (Button) findViewById(R.id.order_unhandler_calendar_cancel);
		mOrderNotPru = (Button) findViewById(R.id.order_unhandler_calendar_notpru);

		setListener();

		gestureDetector = new GestureDetector(this, new MyGestureListener());
		flipper = (ViewFlipper) findViewById(R.id.flipper);
		flipper.removeAllViews();
		calV = new CalendarAdapter(this, getResources(), jumpMonth, jumpYear, year_c, month_c, day_c);
		addGridView();
		gridView.setAdapter(calV);
		flipper.addView(gridView, 0);
		addTextToTopTextView(currentMonth);

		mIntent = getIntent();
	}

	@Override
	public void setContent() {

	}

	@Override
	public void initTitle() {

	}

	@Override
	public void initView() {

	}

	private class MyGestureListener extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			int gvFlag = 0; // 每次添加gridview到viewflipper中时给的标记
			if (e1.getX() - e2.getX() > 120) {
				// 像左滑动
				enterNextMonth(gvFlag);
				return true;
			} else if (e1.getX() - e2.getX() < -120) {
				// 向右滑动
				enterPrevMonth(gvFlag);
				return true;
			}
			return false;
		}
	}

	/**
	 * 移动到下一个月
	 * 
	 * @param gvFlag
	 */
	private void enterNextMonth(int gvFlag) {
		addGridView(); // 添加一个gridView
		jumpMonth++; // 下一个月

		calV = new CalendarAdapter(this, this.getResources(), jumpMonth, jumpYear, year_c, month_c, day_c);
		gridView.setAdapter(calV);
		addTextToTopTextView(currentMonth); // 移动到下一月后，将当月显示在头标题中
		gvFlag++;
		flipper.addView(gridView, gvFlag);
		flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_in));
		flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_out));
		flipper.showNext();
		flipper.removeViewAt(0);

		//每次切换月份的时候，将值重置
		mCurrentDate = "";
	}

	/**
	 * 移动到上一个月
	 * 
	 * @param gvFlag
	 */
	private void enterPrevMonth(int gvFlag) {
		addGridView(); // 添加一个gridView
		jumpMonth--; // 上一个月

		calV = new CalendarAdapter(this, this.getResources(), jumpMonth, jumpYear, year_c, month_c, day_c);
		gridView.setAdapter(calV);
		gvFlag++;
		addTextToTopTextView(currentMonth); // 移动到上一月后，将当月显示在头标题中
		flipper.addView(gridView, gvFlag);

		flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_in));
		flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_out));
		flipper.showPrevious();
		flipper.removeViewAt(0);

		//每次切换月份的时候，将值重置
		mCurrentDate = "";
	}

	/**
	 * 添加头部的年份 闰哪月等信息
	 * 
	 * @param view
	 */
	public void addTextToTopTextView(TextView view) {
		StringBuffer textDate = new StringBuffer();
		// draw = getResources().getDrawable(R.drawable.top_day);
		// view.setBackgroundDrawable(draw);
		textDate.append(calV.getShowYear()).append("年").append(calV.getShowMonth()).append("月").append("\t");
		view.setText(textDate);
	}

	private void addGridView() {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		// 取得屏幕的宽度和高度
		WindowManager windowManager = getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		int Width = display.getWidth();
		int Height = display.getHeight();

		gridView = new GridView(this);
		gridView.setNumColumns(7);
		gridView.setColumnWidth(40);
		// gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
		if (Width == 720 && Height == 1280) {
			gridView.setColumnWidth(40);
		}
		gridView.setGravity(Gravity.CENTER_VERTICAL);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		// 去除gridView边框
		gridView.setVerticalSpacing(1);
		gridView.setHorizontalSpacing(1);
		gridView.setOnTouchListener(new OnTouchListener() {
			// 将gridview中的触摸事件回传给gestureDetector

			public boolean onTouch(View v, MotionEvent event) {

				return CalendarActivity.this.gestureDetector.onTouchEvent(event);
			}
		});

		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				// 点击任何一个item，得到这个item的日期(排除点击的是周日到周六(点击不响应))
				int startPosition = calV.getStartPositon();
				int endPosition = calV.getEndPosition();
				if (startPosition <= position + 7 && position <= endPosition - 7) {
					String scheduleDay = calV.getDateByClickItem(position).split("\\.")[0]; // 这一天的阳历
					// String scheduleLunarDay =
					// calV.getDateByClickItem(position).split("\\.")[1];
					// //这一天的阴历
					String scheduleYear = calV.getShowYear();
					String scheduleMonth = calV.getShowMonth();
//					T.showShort(UIUtils.getContext(),scheduleYear + "-" + scheduleMonth + "-" + scheduleDay);
					//改变当前item的颜色

					calV.changeItemColor(position);
					// Toast.makeText(CalendarActivity.this, "点击了该条目",
					// Toast.LENGTH_SHORT).show();
					//每次选择后，都将选择的日期记录下来\
					int i = Integer.parseInt(scheduleMonth);
					if (i<10){
						scheduleMonth = "0"+i;
					}
					mCurrentDate = scheduleYear + "-" + scheduleMonth + "-" + scheduleDay;
				}
			}
		});
		gridView.setLayoutParams(params);
	}

	private void setListener() {
		prevMonth.setOnClickListener(this);
		nextMonth.setOnClickListener(this);

		mOrderConfirm.setOnClickListener(this);
		mOrderCancel.setOnClickListener(this);
		mOrderNotPru.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.nextMonth: // 下一个月
			enterNextMonth(gvFlag);
			break;
		case R.id.prevMonth: // 上一个月
			enterPrevMonth(gvFlag);
			break;

		case R.id.order_unhandler_calendar_confirm:
			//点击了确认按钮
			clickComfirm();

			break;

		case R.id.order_unhandler_calendar_cancel:
			//点击了取消的按钮
			clickCancel();
			break;

		case R.id.order_unhandler_calendar_notpru:
				//点击了取消的按钮
				clickNotPru();
				break;
		default:
			break;
		}
	}

	//点击了不生产
	private void clickNotPru() {
		/*if (mIntent != null){
			mIntent.putExtra("result","notPru");
			setResult(1, mIntent);
			finish();
		}*/
		showNoPruductDialog();

	}

	private void clickCancel() {
		finish();
	}

	private void clickComfirm() {
		try {

			if (mIntent != null){
				if (TextUtils.isEmpty(mCurrentDate)){
					//说明数据是空
					T.showShort(UIUtils.getContext(),"你还没有选择日期");
					return;
				}

				long time = getTime(mCurrentDate)+24*60*60*1000-1;

				if ((time - mCurrentTime) < 0){
					T.showShort(UIUtils.getContext(),"你选择的时间已过期");
					return;
				}
				showTimePicker();


			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

	//显示选择时间的控件
	private void showTimePicker() {
		 final TimePickerDialog timedialog = new TimePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT, new TimePickerDialog.OnTimeSetListener() {
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				mSelectDay = "";
				mSelectTime = "";
				if (hourOfDay<10){
					mSelectDay = "0"+hourOfDay;
				}else {
					mSelectDay = hourOfDay+"";
				}

				if (minute < 10){
					mSelectTime = "0"+minute;
				}else {
					mSelectTime = minute+"";
				}
				try{
					long time = getDetailTime(mCurrentDate + " " + mSelectDay+":"+mSelectTime+":00");
					if ((time - mCurrentTime) < 0){
						T.showShort(UIUtils.getContext(), "你选择的时间已过期");
						return;
					}
					if (mType == 1){
						mIntent.putExtra("result",mCurrentDate+" "+mSelectDay+":"+mSelectTime+":00");
						setResult(2, mIntent);
						finish();
					}else {
						showComfirmDialog();
					}

				}catch (Exception e){
					e.printStackTrace();
				}

			}
		}, 0, 0, true);

		timedialog.show();
	}

	private void showNoPruductDialog(){
		if (mNoPur != null){
			return;
		}
		mNoPur = new Dialog(this, R.style.common_dialog_theme);
		LayoutInflater inflater = LayoutInflater.from(UIUtils.getContext());
		View dialogContentView = inflater.inflate(R.layout.dialog_no_pruduct, null);
		TextView cancelTv = (TextView) dialogContentView.findViewById(R.id.no_product_cancel)
				;
		cancelTv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mNoPur.dismiss();
				mNoPur = null;
			}
		});
		TextView confirmTv = (TextView) dialogContentView.findViewById(R.id.no_product_comfirm);
		confirmTv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showLoadDialog();
				netForMark("");
			}
		});
		mNoPur.setContentView(dialogContentView);

		int topMargin = DeviceConfiger.dp2px(150);
		int width = DeviceConfiger.dp2px(300);
		Window dialogWindow = mNoPur.getWindow();
		dialogWindow.setGravity(Gravity.TOP);
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.y = topMargin;
		lp.width = width;
		dialogWindow.setAttributes(lp);

		mNoPur.setCanceledOnTouchOutside(false);
		mNoPur.show();
	}


	private void showComfirmDialog() {
		if (mComfirmDialog != null){
			return;
		}
		mComfirmDialog = new Dialog(this, R.style.common_dialog_theme);
		LayoutInflater inflater = LayoutInflater.from(UIUtils.getContext());
		View dialogContentView = inflater.inflate(R.layout.dialog_select_time, null);

		TextView contentTv = (TextView) dialogContentView.findViewById(R.id.select_time_text);
		TextView cancelTv = (TextView) dialogContentView.findViewById(R.id.select_time_cancel);
		cancelTv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mComfirmDialog.dismiss();
				mComfirmDialog = null;
			}
		});

		contentTv.setText(mCurrentDate+" "+mSelectDay+":"+mSelectTime+":00");

		TextView confirmTv = (TextView) dialogContentView.findViewById(R.id.select_time_comfirm);
		confirmTv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showLoadDialog();
				String thdate = mCurrentDate+" "+mSelectDay+":"+mSelectTime+":00";
				netForMark(thdate);

			}
		});

		mComfirmDialog.setContentView(dialogContentView);

		int topMargin = DeviceConfiger.dp2px(150);
		int width = DeviceConfiger.dp2px(300);
		Window dialogWindow = mComfirmDialog.getWindow();
		dialogWindow.setGravity(Gravity.TOP);
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.y = topMargin;
		lp.width = width;
		dialogWindow.setAttributes(lp);

		mComfirmDialog.setCanceledOnTouchOutside(false);
		mComfirmDialog.show();
	}


	/**
	 * 通过网络接口，请求是否mark成功
	 * */
	private void netForMark(final String THDate) {
		String url = "";
		if (TextUtils.isEmpty(THDate)){
			url = Constants.URLS.ORDER_MARKREADY+"id="+mId;
		}else {
			url = Constants.URLS.ORDER_MARKREADY+"id="+mId+"&THDate="+THDate;
		}

		LogUtils.i("url------------"+url);
		HttpManager manger = new HttpManager(UIUtils.getContext()){
			@Override
			protected void onSuccess(Object obj) {
				T.showShort(UIUtils.getContext(),"标记成功");
				if (obj != null){
					Gson gson = new Gson();
					LoginBean bean = gson.fromJson(obj + "", LoginBean.class);

					LogUtils.i("obj---------------"+obj);

					if (bean.msg == 1){
						//说明成功
						if (TextUtils.isEmpty(THDate)){
							mIntent.putExtra("result","notPru");
							setResult(1, mIntent);
							mNoPur.dismiss();
						}else {
							mIntent.putExtra("result", mCurrentDate + " " + mSelectDay+":"+mSelectTime + ":00");
							setResult(1, mIntent);
							mComfirmDialog.dismiss();
						}

						dismissLoadDialog();
						finish();
					}else {
						mComfirmDialog.dismiss();
						mComfirmDialog = null;
						dismissLoadDialog();
						T.showShort(UIUtils.getContext(),"标记失败，请重试");
					}
				}
			}

			@Override
			protected void onFail() {
				dismissLoadDialog();
				if (TextUtils.isEmpty(THDate)){
					mNoPur.dismiss();
					mNoPur = null;
				}else {
					mComfirmDialog.dismiss();
					mComfirmDialog = null;
				}

				T.showShort(UIUtils.getContext(),"标记失败，请重试");
			}

			@Override
			protected void onTimeOut() {
				dismissLoadDialog();
				if (TextUtils.isEmpty(THDate)){
					mNoPur.dismiss();
					mNoPur = null;
				}else {
					mComfirmDialog.dismiss();
					mComfirmDialog = null;
				}
				T.showShort(UIUtils.getContext(), "标记备齐时间超时，请重试");
			}
		};


		manger.get(url);

	}

	/**
	 * 改方法用于将时间戳和日期进行转换
	 *
	 * */

	private long getTime(String date) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");

		Date date1 = sdf.parse(date);
		long time = date1.getTime();

		return time;
	}

	private long getDetailTime(String date) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date1 = sdf.parse(date);
		long time = date1.getTime();
		return time;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}