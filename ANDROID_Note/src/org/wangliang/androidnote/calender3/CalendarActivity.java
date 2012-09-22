/*
 * Copyright (C) 2011 Chris Gao <chris@exina.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wangliang.androidnote.calender3;

import org.wangliang.androidnote.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Android���ڿؼ�������
 * @Description: ����ѡ���ϣ����£�������ʾѡ������ڡ�

 * @FileName: CalendarActivity.java 

 * @Package com.exina.android.calendar 

 * @Author Hanyonglu

 * @Date 2012-3-26 ����11:36:05 

 * @Version V1.0
 */
public class CalendarActivity extends Activity  implements CalendarView.OnCellTouchListener{
	public static final String MIME_TYPE = "vnd.android.cursor.dir/vnd.exina.android.calendar.date";
	private CalendarView mView = null;
	private TextView mHit;
	private Handler mHandler = new Handler();
	private  Button btCenter;
	private Rect ecBounds;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calender_main3);
        mView = (CalendarView)findViewById(R.id.calendar);
        mView.setOnCellTouchListener(this);
        
        if(getIntent().getAction().equals(Intent.ACTION_PICK))
        	findViewById(R.id.hint).setVisibility(View.INVISIBLE);
        
        btCenter = (Button) findViewById(R.id.btCenter);
        btCenter.setText(mView.getYear() + "-" + (mView.getMonth() + 1));
        Button btLeft = (Button) findViewById(R.id.btnLeft);
        btLeft.setText("����");
        Button btRight = (Button) findViewById(R.id.btRight);
        btRight.setText("����");
        btLeft.setOnClickListener(new OnClickListener() { 
			public void onClick(View v) {
				mView.previousMonth(); 
				btCenter.setText(mView.getYear() + "-" + (mView.getMonth() + 1));
			}
		});
        btRight.setOnClickListener(new OnClickListener() { 
			public void onClick(View v) {
				mView.nextMonth(); 
				btCenter.setText(mView.getYear() + "-" + (mView.getMonth() + 1));
			}
		});
        
    }

	public void onTouch(Cell cell) {
		
		Intent intent = getIntent();
		String action = intent.getAction();
		if(cell.mPaint.getColor() == Color.GRAY) {
			// �������µ�
			mView.previousMonth(); 
			btCenter.setText(mView.getYear() + "-" + (mView.getMonth() + 1));
		} else if(cell.mPaint.getColor() == Color.LTGRAY) {
			// ���µ�
			mView.nextMonth(); 
			btCenter.setText(mView.getYear() + "-" + (mView.getMonth() + 1));
		} else {  //  ���µ�
			if(action.equals(Intent.ACTION_PICK) || action.equals(Intent.ACTION_GET_CONTENT)) {
				Intent ret = new Intent();
				ret.putExtra("year", mView.getYear());
				ret.putExtra("month", mView.getMonth());
				ret.putExtra("day", cell.getDayOfMonth());
				 
				System.out.println("��ǰ���ڣ�" + mView.getYear() + "-" + mView.getMonth() + "-" + cell.getDayOfMonth());
				// �ڴ��õ�ǰ��View �ػ�һ��
				ecBounds = cell.getBound();
				mView.getDate();
//				MyCalendarView mCalendarView = new MyCalendarView(CalendarActivity.this);
//				mCalendarView  =  (MyCalendarView) mView;
				mView.mDecoraClick.setBounds(ecBounds);
				mView.invalidate();
				
//				this.setResult(RESULT_OK, ret);
//				finish();
				return;
			}
		}
//		int day = cell.getDayOfMonth();
//		if(mView.firstDay(day))
//			mView.previousMonth();
//		else if(mView.lastDay(day))
//			mView.nextMonth();
//		else
//			return;

		mHandler.post(new Runnable() {
			public void run() {
				btCenter.setText(mView.getYear() + "-" + (mView.getMonth() + 1));
				Toast.makeText(CalendarActivity.this, DateUtils.getMonthString(mView.getMonth(), DateUtils.LENGTH_LONG) + " "+mView.getYear(), Toast.LENGTH_SHORT).show();
			}
		});
	}
 
}