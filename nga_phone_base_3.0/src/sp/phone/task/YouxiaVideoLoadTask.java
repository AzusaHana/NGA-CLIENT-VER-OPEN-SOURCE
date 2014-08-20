package sp.phone.task;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import gov.anzong.androidnga.R;
import sp.phone.fragment.ProgressDialogFragment;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.MD5Util;
import sp.phone.utils.StringUtil;
import sp.phone.utils.YouKuLoadFunction;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

public class YouxiaVideoLoadTask extends AsyncTask<String, Integer, String> {

	final FragmentActivity fa;
	final String origurl;
	static final String dialogTag = "load_youxia";
	public YouxiaVideoLoadTask(FragmentActivity fa, String origurl) {
		super();
		this.fa = fa;
		this.origurl = origurl;
	}

	private boolean startIntent = true;

	@Override
	protected void onPreExecute() {
		// create progress view
		ProgressDialogFragment pd = new ProgressDialogFragment();

		Bundle args = new Bundle();
		final String content = fa.getResources().getString(
				R.string.load_youxia_video);
		args.putString("content", content);
		pd.setArguments(args);
		pd.show(fa.getSupportFragmentManager(), dialogTag);
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(String result) {
		if (!startIntent) {
			Toast.makeText(fa.getBaseContext(), R.string.videoplay_createwindow_error,
					Toast.LENGTH_LONG).show();
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(origurl));
			boolean isIntentSafe = fa.getPackageManager()
					.queryIntentActivities(intent, 0).size() > 0;
			if (isIntentSafe)
				fa.startActivity(intent);
			return;
		}

		if (result != null) {
		    try{
		        Intent intent = new Intent();
		        ComponentName comp = new ComponentName("gov.anzong.mediaplayer","gov.anzong.mediaplayer.ReceiveIntentActivity");
		        intent.setComponent(comp);
		        intent.putExtra("uri", result);
		        intent.putExtra("title", "������Ƶ");
				fa.startActivity(intent);
		    }catch(Exception e){
		    	//TODO
		    	Toast.makeText(fa.getBaseContext(), R.string.videoplay_ngaplayernotinstall_error,
						Toast.LENGTH_LONG).show();
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(origurl));
				boolean isIntentSafe = fa.getPackageManager()
						.queryIntentActivities(intent, 0).size() > 0;
				if (isIntentSafe)
					fa.startActivity(intent);
		    }
		} else {
				Toast.makeText(fa.getBaseContext(), R.string.videoplay_urlgetfailed_error,
						Toast.LENGTH_LONG).show();
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(origurl));
				boolean isIntentSafe = fa.getPackageManager()
						.queryIntentActivities(intent, 0).size() > 0;
				if (isIntentSafe)
					fa.startActivity(intent);
		}

		this.onCancelled();

		super.onPostExecute(result);
	}

	@Override
	protected void onCancelled(String result) {

		this.onCancelled();
	}

	@Override
	protected void onCancelled() {
		FragmentManager fm = fa.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();

		Fragment prev = fm.findFragmentByTag(dialogTag);
		if (prev != null) {
			ft.remove(prev);

		}
		try {
			ft.commit();
		} catch (Exception e) {

		}
	}

	@Override
	protected String doInBackground(String... params) {
		final String uri = params[0];
		final String htmlStringstep1 = HttpUtil.iosGetHtml(uri, null);
		String scriptdata = StringUtil.getStringBetween(htmlStringstep1, 0,
				"<script>var Vedio=", ";</script>").result;
		if (StringUtil.isEmpty(scriptdata)) {
			return null;
		}
		String iid = StringUtil.getStringBetween(scriptdata, 0,
				"\"vid\":\"", "\"").result;
		if(StringUtil.isEmpty(iid)){
			return null;
		}
		if(scriptdata.indexOf("\"type\":\"tudou\"")>=0){
			String newurl="http://so.v.ali213.net/plus/tudou.php?id="+iid;
			final String htmlStringstep2 = HttpUtil.iosGetHtml(newurl, null);
			String add = StringUtil.getStringBetween(htmlStringstep2, 0,
					"\"url\":\"", "\"").result;
			add=add.replaceAll("\\/", "/");
			add=add.replaceAll("\\\\/", "/");
			add=add.replaceAll("&amp;", "&");
			if(StringUtil.isEmpty(add)){
				return null;
			}else{
				return add;
			}
		}else if(scriptdata.indexOf("\"type\":\"youku\"")>=0){
			String URL="http://v.youku.com/player/getPlaylist/VideoIDS/"+iid+"/Pf/4/ctype/12/ev/1";
			String HTML=HttpUtil.iosGetHtml(URL, null);
			return YouKuLoadFunction.Load(HTML);
		}else if(scriptdata.indexOf("\"type\":\"qq\"")>=0){
			String newurl="http://so.v.ali213.net/plus/qq.php?id="+iid;
			final String htmlStringstep2 = HttpUtil.iosGetHtml(newurl, null);
			String add = StringUtil.getStringBetween(htmlStringstep2, 0,
					"\"url\":\"", "\"").result;
			add=add.replaceAll("\\/", "/");
			add=add.replaceAll("\\\\/", "/");
			add=add.replaceAll("&amp;", "&");
			if(StringUtil.isEmpty(add)){
				return null;
			}else{
				return add;
			}
		}else{
			return null;
		}
	}
	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}

	private int StrTotalCount(String str, String key) {
		int count = 0;
		int index = 0;
		while ((index = str.indexOf(key, index)) != -1) {
			index = index + key.length();
			count++;
		}
		return count;
	}
}
