package org.wangliang.androidnote;

import java.util.HashMap;
import java.util.Map;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
//http://www.cnblogs.com/web8cn/archive/2011/12/20/2294727.html
//http://www.cnblogs.com/hanyonglu/archive/2012/03/26/2418178.html
//http://blog.csdn.net/Android_Tutor/article/details/6165470
//http://blog.csdn.net/ipromiseu/article/details/7420846
//http://www.android-study.com/pingtaikaifa/242.html


//http://www.eoeandroid.com/thread-171633-1-1.html
//http://www.eoeandroid.com/thread-2191-1-1.html
public class Main extends ListActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
        init();
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	super.onListItemClick(l, v, position, id);
    	startActivity(maps.get(position));
    }
    
    static String[] labels = new String[]{"Calender01","Calender2","Calender3"};//{"Calender01"};
    
    static Map<Integer, Intent> maps = new HashMap<Integer, Intent>();
    private void init(){
    	maps.put(0,new Intent(this, MainActivity.class ));
    	maps.put(1,new Intent(this, org.wangliang.androidnote.calender2.Main.class ));
    	maps.put(2,new Intent(this, org.wangliang.androidnote.calender3.TestActivity.class ));
    	setListAdapter(new MyListAdapter(this,labels));
    	
    }
    
    class MyListAdapter extends ArrayAdapter<String>{

		public MyListAdapter(Context context,String[] data) {
			super(context, android.R.layout.simple_list_item_1,android.R.id.text1,data);
		}
    	
    }
}