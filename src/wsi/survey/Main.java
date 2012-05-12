package wsi.survey;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class Main extends ListActivity {

	public static final String filename = "FILE_NAME";

	private SimpleAdapter mAdapter;
	private List<Map<String, String>> mData = new ArrayList<Map<String, String>>();
	private final String[] from = { "title" };
	private int[] to;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		to = new int[] { android.R.id.text1 };
		getQuestionNaireList();
		mAdapter = new SimpleAdapter(this, mData, android.R.layout.simple_list_item_1, from, to);
		setListAdapter(mAdapter);
	}

	@Override
	protected void onListItemClick(ListView list, View view, int position, long id) {
		super.onListItemClick(list,  view,  position,  id);
		
		Intent intent = new Intent(this, AnswerQuesionNaire.class);
		Bundle bundle = new Bundle();
		Map<String, String> map = mData.get(position);

		bundle.putString(filename, map.get(from[0]));
		intent.putExtras(bundle);
		startActivity(intent);
	}

	public void getQuestionNaireList() {
		String filePath = Environment.getExternalStorageDirectory() + "/CompterThink/";
		File file = new File(filePath);
		File files[] = file.listFiles();
		int len = files.length;

		for (int i = 0; i < len; i++) {
			Hashtable<String, String> table = new Hashtable<String, String>();
			table.put(from[0], files[i].getName());
			mData.add(table);
		}

	}

}