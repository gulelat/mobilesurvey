package wsi.survey;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class Welcome extends Activity {

	private static int duration = 2000;

	private String filename[] = { "Adult_mental_stress_scale.xml",
			"Social_adaptation_ability_scale.xml",
			"Holland_vocational_aptitude_test_scale.xml" };

	private Context mContext = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.welcom);

		Toast.makeText(this, "欢迎使用心理健康测试系统", duration).show();
		mkdir();
		Message msg = Message.obtain();
		mHander.sendMessageDelayed(msg, duration);
	}

	private Handler mHander = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			Intent intent = new Intent(Welcome.this, Main.class);
			startActivity(intent);
//			finish();
		};
	};

	private void mkdir() {
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			String filePath = Environment.getExternalStorageDirectory() .getAbsolutePath()+ "/CompterThink/";	 // 获得SD卡根目录 + "/CompterThink/"
			File xmlFolder = new File(filePath);
			if (!xmlFolder.exists()) {
				xmlFolder.mkdirs();
				
				File file = new File(filePath + filename[0]);
				inputxml(mContext, file, R.raw.adultmentalstressscale);
				file = new File(filePath + filename[1]);
				inputxml(mContext, file, R.raw.socialadaptationabilityscale);
				file = new File(filePath + filename[2]);
				inputxml(mContext, file, R.raw.hollandvocationalaptitudetestscale);
			}
			
		} else {
			Toast.makeText(this, "找不到sdcard", duration).show();
		}
	}

	// 规划了file参数、ID参数，方便多文件写入
	public void inputxml(Context context, File file, int raw) {
		InputStream in = null;
		OutputStream out = null;
		BufferedInputStream bin = null;
		BufferedOutputStream bout = null;
		
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			in = context.getResources().openRawResource(raw);
			int length = in.available();				// 获取文件的字节数
			byte[] buffer = new byte[length];			// 创建byte数组
			bin = new BufferedInputStream(in);			// 缓冲输出流
			
			out = new FileOutputStream(file);			// 字节输入流
			bout = new BufferedOutputStream(out);		// 缓存输入流
			int len = bin.read(buffer);
			while (len != -1) {
				bout.write(buffer, 0, len);
				len = bin.read(buffer);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			if (bin != null) {
				bin.close();
			}
			if (bout != null) {
				bout.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
