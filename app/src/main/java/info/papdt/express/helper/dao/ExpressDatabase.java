package info.papdt.express.helper.dao;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import info.papdt.express.helper.support.Express;
import info.papdt.express.helper.support.ExpressResult;
import info.papdt.express.helper.support.Utility;

public class ExpressDatabase {

	private ArrayList<Express> mExpressArray;
	private Context context;

	private final static String TAG = "ExpressDatabase";

	public ExpressDatabase(Context context) {
		this.context = context;
		mExpressArray = new ArrayList<>();
	}

	public void addExpress(String jsonStr) {
		ExpressResult res = ExpressResult.buildFromJSON(jsonStr);
		Express exp = new Express(res.expSpellName, res.mailNo);
		exp.setData(jsonStr);
		this.addExpress(exp);
	}

	public void addExpress(Express express) {
		mExpressArray.add(express);
	}

	public Express getExpress(int index){
		return mExpressArray.get(index);
	}

	public void deleteExpress(int position){
		mExpressArray.remove(position);
	}

	public int size() {
		return mExpressArray.size();
	}

	public int findExpress(String companyCode, String mailNumber){
		boolean ok = false;
		int i;

		for (i = 0; i < mExpressArray.size(); i++){
			if (mExpressArray.get(i).getCompanyCode().equals(companyCode)
					&& mExpressArray.get(i).getMailNumber().equals(mailNumber)){
				ok = true;
				break;
			}
		}

		if (ok) return i; else return -1;
	}

	public void init(){
		String jsonData;
		try {
			jsonData = Utility.readFile(context, "data.json");
		} catch (IOException e) {
			jsonData = "{\"data\":[]}";
			Log.i(TAG, "文件不存在,初始化新的文件.");
			e.printStackTrace();
		}
		Log.i(TAG, "读入json数据结果:");
		Log.i(TAG, jsonData);
		JSONObject jsonObj = null;
		JSONArray jsonArray = null;

		try {
			jsonObj = new JSONObject(jsonData);
		} catch (JSONException e) {
			Log.e(TAG, "无法解析json");
			e.printStackTrace();
			return ;
		}

		try {
			jsonArray = jsonObj.getJSONArray("data");
		} catch (JSONException e) {
			Log.e(TAG, "数据格式丢失, 缺少 data 数组");
			e.printStackTrace();
			return ;
		}

		Express newExpress;
		for (int i = 0; i < jsonArray.length(); i++){
			try {
				newExpress = new Express(jsonArray.getJSONObject(i).getString("companyCode"),
						jsonArray.getJSONObject(i).getString("mailNumber"));
				newExpress.setData(jsonArray.getJSONObject(i).getString("cache"));
				mExpressArray.add(newExpress);
			} catch (JSONException e){
				Log.e(TAG, "第"+i+"组数据格式出现错误");
				e.printStackTrace();
			}
		}
	}

	public void save() throws IOException{
		StringBuffer str = new StringBuffer();
		str.append("{\"data\":[");
		for (int i = 0; i < mExpressArray.size(); i++){
			str.append("{\"companyName\":\"");
			str.append(mExpressArray.get(i).getCompanyCode());
			str.append("\",");

			str.append("\"date\":\"");
			str.append(mExpressArray.get(i).getMailNumber());
			str.append("\",");

			str.append("\"cache\":\"");
			str.append(mExpressArray.get(i).getData());
			str.append("\"}");
			if (i < mExpressArray.size() -1) str.append(",");
		}
		str.append("]}");
		Utility.saveFile(context, "data.json", str.toString());
	}

}
