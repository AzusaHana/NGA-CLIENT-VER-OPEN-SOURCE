package sp.phone.task;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

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

public class YoukuVideoLoadTask extends AsyncTask<String, Integer, String> {

	final FragmentActivity fa;
	final String origurl;
	static final String dialogTag = "load_youku";
	public YoukuVideoLoadTask(FragmentActivity fa, String origurl) {
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
				R.string.load_youku_video);
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
		        intent.putExtra("title", "�ſ���Ƶ");
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
		final String vid = params[0];
		String URL="http://v.youku.com/player/getPlaylist/VideoIDS/"+vid+"/Pf/4/ctype/12/ev/1";
		String HTML=HttpUtil.iosGetHtml(URL, null);
		return YouKuLoadFunction.Load(HTML);
	}
}
