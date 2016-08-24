package com.meilicat.basedemo.utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.text.format.Time;
import android.util.Base64;

import com.nostra13.universalimageloader.utils.L;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author
 * 
 */
public class BasicTool {

	/**
	 * 检测字符串是否为空，
	 * 
	 * @param str
	 * @return 是空 返回 <code>false</code> 否则返回 <code>true</code>
	 */
	public static boolean isNotEmpty(String str) {
		if (null == str)
			return false;
		return !"".equals(str.trim());
	}

	public static boolean isEmptyForCart(JSONObject keyObj)
			throws JSONException {
		if (null == keyObj || "{}".equals(keyObj.toString())
				|| !BasicTool.isNotEmpty(keyObj.getString("value")))
			return false;
		else
			return true;
	}

	/**
	 * @function 邮箱验证,验证通过则返回ture，否则为false
	 * @param strEmail
	 * @return
	 */
	public static boolean isEmail(String strEmail) {
		String strPattern = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
		Pattern p = Pattern.compile(strPattern);
		Matcher m = p.matcher(strEmail);

		if (m.matches()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @function 手机号的验证,验证通过则返回ture，否则为false
	 * @param str
	 * @return
	 */
	public static boolean isCellphone(String str) {
		Pattern pattern = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Matcher matcher = pattern.matcher(str);

		if (matcher.matches()) {
			return true;
		} else {
			return false;
		}
	}
	/**
	 * 判断字符串中是否含有中文
	 * @param str 字符串
	 * @return true 有 false 无
	 */

	public static boolean isContainsChinese(String str)
	{
		String regEx = "[\u4e00-\u9fa5]";
		Pattern pat = Pattern.compile(regEx);
		Matcher matcher = pat.matcher(str);
		boolean flg = false;
		if (matcher.find())    {
			flg = true;
		}
		return flg;
	}

	/**
	 * 获取时间戳
	 * 
	 * @return
	 */
	public static String getCurString() {
		long msg = System.currentTimeMillis();

		return Long.toString(msg);

	}

	/**
	 * 将单个list转成json字符串
	 * 
	 * @param list
	 * @return
	 * @throws Exception
	 */
	public static String listToJsonString(List<Map<String, Object>> list)
			throws Exception {
		String jsonL = "";
		StringBuffer temp = new StringBuffer();
		temp.append("[");
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> m = list.get(i);
			if (i == list.size() - 1) {
				temp.append(mapToJsonObj(m));
			} else {
				temp.append(mapToJsonObj(m) + ",");
			}
		}
		if (temp.length() > 1) {
			temp = new StringBuffer(temp.substring(0, temp.length()));
		}
		temp.append("]");
		jsonL = temp.toString();
		return jsonL;
	}

	/**
	 * 将map数据解析出来，并拼接成json字符串
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static JSONObject mapToJsonObj(Map map) throws Exception {
		JSONObject json = null;
		StringBuffer temp = new StringBuffer();
		if (!map.isEmpty()) {
			temp.append("{");
			// 遍历map
			Set set = map.entrySet();
			Iterator i = set.iterator();
			while (i.hasNext()) {
				Map.Entry entry = (Map.Entry) i.next();
				String key = (String) entry.getKey();

				Object value = entry.getValue();

				temp.append("\"" + key + "\":");

				if (null == value || "".equals(value)) {
					temp.append("\"\"" + ", ");
				} else if (value instanceof Map<?, ?>) {
					temp.append(mapToJsonObj((Map<String, Object>) value) + ",");
				} else if (value instanceof List<?>) {
					temp.append(listToJsonString((List<Map<String, Object>>) value)
							+ ",");
				} else if (value instanceof String) {
					temp.append("\"" + String.valueOf(value) + "\",");
				} else {
					temp.append(String.valueOf(value) + ",");
				}

			}
			if (temp.length() > 1) {
				String mString = temp.toString();
				mString = mString.trim();

				temp = new StringBuffer(mString.substring(0,
						mString.length() - 1));
			}

			temp.append("}");

			json = new JSONObject(temp.toString());
		}
		return json;
	}

	/**
	 * 将json 对象转换为Map 对象
	 * 
	 * @param jsonString
	 * @return
	 */
	public static Map<String, Object> jsonToMap(String jsonString) {
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(jsonString);
			Iterator<String> keyIter = jsonObject.keys();
			String key;
			Object value;
			Map<String, Object> valueMap = new HashMap<String, Object>();
			while (keyIter.hasNext()) {
				key = (String) keyIter.next();
				value = jsonObject.get(key);
				valueMap.put(key, value);
			}
			return valueMap;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 把json 转换为 ArrayList 形式
	 * 
	 * @return
	 */
	public static List<Map<String, Object>> jsonArrToList(String jsonString) {
		List<Map<String, Object>> list = null;

		try {
			JSONArray jsonArray = new JSONArray(jsonString);

			JSONObject jsonObject;

			list = new ArrayList<Map<String, Object>>();

			for (int i = 0; i < jsonArray.length(); i++) {
				jsonObject = jsonArray.getJSONObject(i);
				list.add(jsonToMap(jsonObject.toString()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 
	 * @param gap
	 *            获取时间的间隔，要获取之前的日期则为负，如获取前一周的时间，则为-7；反之当前日期之后的则为正
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getDateBefore(int gap) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		Calendar now = Calendar.getInstance();

		now.setTime(new Date());
		now.set(Calendar.DATE, now.get(Calendar.DATE) + gap);

		return sdf.format(now.getTime());
	}

	/**
	 *            获取当前日期，格式：yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getCruDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		return sdf.format(new Date());
	}

	@SuppressLint("SimpleDateFormat")
	public static String getOrderTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

		return sdf.format(new Date());
	}

	/** 日期时间字符串转换为日期字符串 */
	@SuppressLint("SimpleDateFormat")
	public static String dateTimeToDate(String datatime) {
		SimpleDateFormat dataTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date;
		try {
			date = dataTime.parse(datatime);
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
			return sdf2.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static final double EARTH_RADIUS = 6378137;

	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}

	/**
	 * 根据两点间经纬度坐标（double值），计算两点间距离，单位为米
	 * 
	 * @param lng1
	 * @param lat1
	 * @param lng2
	 * @param lat2
	 * @return
	 */
	public static double getDistance(double lng1, double lat1, double lng2,
			double lat2) {
		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);
		double a = radLat1 - radLat2;
		double b = rad(lng1) - rad(lng2);
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
				+ Math.cos(radLat1) * Math.cos(radLat2)
				* Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000) / 10000;
		return s;
	}

	/** 缩放Bitmap图片 **/
	public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {

		int w = bitmap.getWidth();

		int h = bitmap.getHeight();

		Matrix matrix = new Matrix();

		float scaleWidth = ((float) width / w);

		float scaleHeight = ((float) height / h);

		matrix.postScale(scaleWidth, scaleHeight);// 利用矩阵进行缩放不会造成内存溢出

		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);

		return newbmp;

	}

	/** 把Bitmap用Base64编码并返回生成的字符串 */
	public static String bitmaptoString(Bitmap bitmap) {
		String string = null;

		ByteArrayOutputStream bStream = new ByteArrayOutputStream();

		bitmap.compress(CompressFormat.PNG, 100, bStream);

		byte[] bytes = bStream.toByteArray();

		string = Base64.encodeToString(bytes, Base64.DEFAULT);

		return string;
	}

	/**
	 * 把JSONObject转换为RequestParams，即key1=a&key2=b&key3=c形式
	 * 
	 * @param obj
	 *            待转化的JSONObject对象
	 * @param flag
	 *            标示位，第一次验证的时候祛除signMsg、referDataType以及referData字段,true:祛除，false
	 *            ：不祛除
	 * @return 转化后的RequestParams对象
	 */
	/*public static RequestParams jsonObjToParams(JSONObject obj, boolean flag) {
		RequestParams params = new RequestParams();

		Iterator<String> it = obj.keys();

		while (it.hasNext()) {
			String key = (String) it.next();

			if (flag) {
				if (key.equals("signMsg") || key.equals("referDataType")
						|| key.equals("referData")) {
					continue;
				}
			}

			try {
				if (flag) {
					if (!BasicTool.isNotEmpty(obj.getString(key))) {
						continue;
					}
				}

				params.put(key, obj.getString(key));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}*/
/*
		return params;
	}*/

	/**
	 * 根据Object obj以及key获取对应的value值
	 * 
	 * @param obj
	 *            目标对象
	 * @param key
	 *            待取值的key
	 * @return
	 */
	public static String getValue(JSONObject obj, String key) {
		String value = "";

		if (obj.has(key)) {
			try {
				value = obj.getString(key);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return value;
	}

	public static Drawable getDrawable(String urlpath) {
		Drawable d = null;
		try {
			URL url = new URL(urlpath);
			URLConnection conn = url.openConnection();
			conn.connect();
			InputStream in;
			in = conn.getInputStream();
			d = Drawable.createFromStream(in, "abc.jpg");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return d;
	}

	/*public static String parserXML(String strXML, String key) {
		String valueStr = "";

		SAXReader reader = new SAXReader();
		StringReader sr = new StringReader(strXML);
		InputSource is = new InputSource(sr);
		try {
			Document document = reader.read(is);

			Element root = document.getRootElement();

			valueStr = root.elementText(key);

		} catch (DocumentException e) {
			e.printStackTrace();
		}

		return valueStr;
	}
*/
	/**
	 * 日期时间比较
	 * 
	 * @param DATE 输入的日期、时间
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static boolean compare_date(String DATE, boolean hasHour) {
		Time time = new Time();
		time.setToNow();
		String DATE2;
		SimpleDateFormat df;
		if (hasHour) {
			DATE2 = time.year + "-" + (time.month + 1) + "-" + time.monthDay
					+ " " + time.hour + ":" + time.minute;
			df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		} else {
			DATE2 = time.year + "-" + (time.month + 1) + "-" + time.monthDay;
			df = new SimpleDateFormat("yyyy-MM-dd");
		}
		L.e("DATE2当前系统时间", DATE2);
		L.e("DATE", DATE);

		try {
			Date dt1 = df.parse(DATE);
			Date dt2 = df.parse(DATE2);

			if (hasHour) {
				if (dt1.getTime() >= dt2.getTime()) {
					return true;
				} else if (dt1.getTime() < dt2.getTime()) {
					return false;
				}

			} else {
				if (dt1.getTime() > dt2.getTime()) {
					return true;
				} else if (dt1.getTime() <= dt2.getTime()) {
					return false;
				}
			}

		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return true;
	}
}
