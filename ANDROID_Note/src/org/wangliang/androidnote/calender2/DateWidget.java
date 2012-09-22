package org.wangliang.androidnote.calender2;

import java.util.ArrayList;
import java.util.Calendar;

import org.wangliang.androidnote.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

/**
 * Android鏃ュ巻鎺т欢绀轰緥
 * @Description:Android鏃ュ巻鎺т欢绀轰緥 

 * @FileName: DateWidget.java 

 * @Package com.decarta.calendar 

 * @Author Hanyonglu

 * @Date 2012-3-26 涓婂崍11:46:14 

 * @Version V1.0
 */
public class DateWidget extends Activity {
	private ArrayList<DateWidgetDayCell> days = new ArrayList<DateWidgetDayCell>();
	private Calendar calStartDate = Calendar.getInstance();
	private Calendar calToday = Calendar.getInstance();
	private Calendar calCalendar = Calendar.getInstance();
	private Calendar calSelected = Calendar.getInstance();
	
	LinearLayout layContent = null;
	Button btnToday = null;
	
	private int iFirstDayOfWeek = Calendar.SUNDAY;
	private int iMonthViewCurrentMonth = 0;
	private int iMonthViewCurrentYear = 0;
	public static final int SELECT_DATE_REQUEST = 111;
	private static final int iDayCellSize = 38;
	private static final int iDayHeaderHeight = 24;
	private static final int iTotalWidth = (iDayCellSize * 7);
	private TextView tv,monthTextView,yearTextView;
	private int mYear;
	private int mMonth;
	private int mDay;
	private int type;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle bundle = this.getIntent().getExtras();
		type = bundle.getInt("type");
		
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); //澹版槑浣跨敤鑷畾涔夋爣棰�
		iFirstDayOfWeek = Calendar.SUNDAY;
		mYear = calSelected.get(Calendar.YEAR);
		mMonth = calSelected.get(Calendar.MONTH);
		mDay = calSelected.get(Calendar.DAY_OF_MONTH);
		setContentView(generateContentView());
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);//鑷畾涔夊竷灞�祴鍊�
		calStartDate = getCalendarStartDate();
		DateWidgetDayCell daySelected = updateCalendar();
		updateControlsState();
		if (daySelected != null)
			daySelected.requestFocus();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	private LinearLayout createLayout(int iOrientation) {
		LinearLayout lay = new LinearLayout(this);
		lay.setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		lay.setOrientation(iOrientation);
		return lay;
	}

	private Button createButton(String sText, int iWidth, int iHeight) {
		Button btn = new Button(this);
		btn.setText(sText);
		btn.setLayoutParams(new LayoutParams(iWidth, iHeight));
		return btn;
	}

	private void generateTopButtons(LinearLayout layTopControls) {

		final int iSmallButtonWidth = 40;
		btnToday = createButton("", iTotalWidth - iSmallButtonWidth*4,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

		monthTextView = new TextView(this);
		monthTextView.setPadding(8, 8, 8, 8);
		monthTextView.setText(mYear+"");
		monthTextView.setWidth(55);
		
		yearTextView = new TextView(this);
		yearTextView.setPadding(20, 8, 8, 8);
		yearTextView.setText(format(mMonth+1));
		yearTextView.setWidth(55);
		
		Button btnPrevMonth = new Button(this);
		btnPrevMonth.setLayoutParams(new LayoutParams(iSmallButtonWidth,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		btnPrevMonth.setBackgroundResource(R.drawable.prev_month);
		
		Button btnPrevYear = new Button(this);
		btnPrevYear.setLayoutParams(new LayoutParams(iSmallButtonWidth,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		btnPrevYear.setBackgroundResource(R.drawable.prev_year);
		
		Button btnNextMonth = new Button(this);
		btnNextMonth.setLayoutParams(new LayoutParams(iSmallButtonWidth,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		btnNextMonth.setBackgroundResource(R.drawable.next_month);
		
		Button btnNextYear = new Button(this);
		btnNextYear.setLayoutParams(new LayoutParams(iSmallButtonWidth,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		btnNextYear.setBackgroundResource(R.drawable.next_year);

		// set events
		btnPrevMonth.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View arg0) {
				setPrevMonthViewItem();
			}
		});

		btnNextMonth.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View arg0) {
				setNextMonthViewItem();
			}
		});
		
		btnPrevYear.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View arg0) {
				setPrevYearViewItem();
			}
		});
		
		btnNextYear.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View arg0) {
				setNextYearViewItem();
			}
		});

		layTopControls.setGravity(Gravity.CENTER_HORIZONTAL);
		layTopControls.addView(btnPrevYear);
		layTopControls.addView(btnPrevMonth);
		layTopControls.addView(monthTextView);
		layTopControls.addView(yearTextView);
		layTopControls.addView(btnNextMonth);
		layTopControls.addView(btnNextYear);
	}

	private View generateContentView() {
		LinearLayout layMain = createLayout(LinearLayout.VERTICAL);
		layMain.setPadding(8, 8, 8, 8);
		LinearLayout layTopControls = createLayout(LinearLayout.HORIZONTAL);

		layContent = createLayout(LinearLayout.VERTICAL);
		layContent.setPadding(20, 0, 20, 0);
		generateTopButtons(layTopControls);
		generateCalendar(layContent);
		layMain.addView(layTopControls);
		layMain.addView(layContent);

		tv = new TextView(this);
		tv.setPadding(20, 0, 20, 0);
		layMain.addView(tv);
		return layMain;
	}

	private View generateCalendarRow() {
		LinearLayout layRow = createLayout(LinearLayout.HORIZONTAL);
		for (int iDay = 0; iDay < 7; iDay++) {
			DateWidgetDayCell dayCell = new DateWidgetDayCell(this,
					iDayCellSize, iDayCellSize);
			dayCell.setItemClick(mOnDayCellClick);
			days.add(dayCell);
			layRow.addView(dayCell);
		}
		return layRow;
	}

	private View generateCalendarHeader() {
		LinearLayout layRow = createLayout(LinearLayout.HORIZONTAL);
		for (int iDay = 0; iDay < 7; iDay++) {
			DateWidgetDayHeader day = new DateWidgetDayHeader(this,
					iDayCellSize, iDayHeaderHeight);
			final int iWeekDay = DayStyle.getWeekDay(iDay, iFirstDayOfWeek);
			day.setData(iWeekDay);
			layRow.addView(day);
		}
		return layRow;
	}

	private void generateCalendar(LinearLayout layContent) {
		layContent.addView(generateCalendarHeader());
		days.clear();
		for (int iRow = 0; iRow < 6; iRow++) {
			layContent.addView(generateCalendarRow());
		}
	}

	private Calendar getCalendarStartDate() {
		calToday.setTimeInMillis(System.currentTimeMillis());
		calToday.setFirstDayOfWeek(iFirstDayOfWeek);

		if (calSelected.getTimeInMillis() == 0) {
			calStartDate.setTimeInMillis(System.currentTimeMillis());
			calStartDate.setFirstDayOfWeek(iFirstDayOfWeek);
		} else {
			calStartDate.setTimeInMillis(calSelected.getTimeInMillis());
			calStartDate.setFirstDayOfWeek(iFirstDayOfWeek);
		}
		updateStartDateForMonth();

		return calStartDate;
	}

	private DateWidgetDayCell updateCalendar() {
		DateWidgetDayCell daySelected = null;
		boolean bSelected = false;
		final boolean bIsSelection = (calSelected.getTimeInMillis() != 0);
		final int iSelectedYear = calSelected.get(Calendar.YEAR);
		final int iSelectedMonth = calSelected.get(Calendar.MONTH);
		final int iSelectedDay = calSelected.get(Calendar.DAY_OF_MONTH);
		calCalendar.setTimeInMillis(calStartDate.getTimeInMillis());
		for (int i = 0; i < days.size(); i++) {
			final int iYear = calCalendar.get(Calendar.YEAR);
			final int iMonth = calCalendar.get(Calendar.MONTH);
			final int iDay = calCalendar.get(Calendar.DAY_OF_MONTH);
			final int iDayOfWeek = calCalendar.get(Calendar.DAY_OF_WEEK);
			DateWidgetDayCell dayCell = days.get(i);
			// check today
			boolean bToday = false;
			if (calToday.get(Calendar.YEAR) == iYear)
				if (calToday.get(Calendar.MONTH) == iMonth)
					if (calToday.get(Calendar.DAY_OF_MONTH) == iDay)
						bToday = true;
			// check holiday
			boolean bHoliday = false;
			if ((iDayOfWeek == Calendar.SATURDAY)
					|| (iDayOfWeek == Calendar.SUNDAY))
				bHoliday = true;
			if ((iMonth == Calendar.JANUARY) && (iDay == 1))
				bHoliday = true;

			dayCell.setData(iYear, iMonth, iDay, bToday, bHoliday,
					iMonthViewCurrentMonth,iDayOfWeek);
			bSelected = false;
			if (bIsSelection)
				if ((iSelectedDay == iDay) && (iSelectedMonth == iMonth)
						&& (iSelectedYear == iYear)) {
					bSelected = true;
				}
			dayCell.setSelected(bSelected);
			if (bSelected)
				daySelected = dayCell;
			calCalendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		layContent.invalidate();
		return daySelected;
	}

	private void updateStartDateForMonth() {
		iMonthViewCurrentMonth = calStartDate.get(Calendar.MONTH);
		iMonthViewCurrentYear = calStartDate.get(Calendar.YEAR);
		calStartDate.set(Calendar.DAY_OF_MONTH, 1);
		UpdateCurrentMonthDisplay();
		// update days for week
		int iDay = 0;
		int iStartDay = iFirstDayOfWeek;
		if (iStartDay == Calendar.MONDAY) {
			iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY;
			if (iDay < 0)
				iDay = 6;
		}
		if (iStartDay == Calendar.SUNDAY) {
			iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;
			if (iDay < 0)
				iDay = 6;
		}
		calStartDate.add(Calendar.DAY_OF_WEEK, -iDay);
	}

	private void UpdateCurrentMonthDisplay() {
		String s = calCalendar.get(Calendar.YEAR) + "/"
				+ (calCalendar.get(Calendar.MONTH) + 1);// dateMonth.format(calCalendar.getTime());
		btnToday.setText(s);
		mYear = calCalendar.get(Calendar.YEAR);
	}

	private void setPrevMonthViewItem() {
		iMonthViewCurrentMonth--;
		if (iMonthViewCurrentMonth == -1) {
			iMonthViewCurrentMonth = 11;
			iMonthViewCurrentYear--;
		}
		calStartDate.set(Calendar.DAY_OF_MONTH, 1);
		calStartDate.set(Calendar.MONTH, iMonthViewCurrentMonth);
		calStartDate.set(Calendar.YEAR, iMonthViewCurrentYear);
		updateDate();
		updateCenterTextView(iMonthViewCurrentMonth,iMonthViewCurrentYear);
	}

	private void setNextMonthViewItem() {
		iMonthViewCurrentMonth++;
		if (iMonthViewCurrentMonth == 12) {
			iMonthViewCurrentMonth = 0;
			iMonthViewCurrentYear++;
		}
		calStartDate.set(Calendar.DAY_OF_MONTH, 1);
		calStartDate.set(Calendar.MONTH, iMonthViewCurrentMonth);
		calStartDate.set(Calendar.YEAR, iMonthViewCurrentYear);
		updateDate();
		updateCenterTextView(iMonthViewCurrentMonth,iMonthViewCurrentYear);
	}
	
	private void setPrevYearViewItem() {
		iMonthViewCurrentYear--;
		calStartDate.set(Calendar.DAY_OF_MONTH, 1);
		calStartDate.set(Calendar.MONTH, iMonthViewCurrentMonth);
		calStartDate.set(Calendar.YEAR, iMonthViewCurrentYear);
		updateDate();
		updateCenterTextView(iMonthViewCurrentMonth,iMonthViewCurrentYear);		
	}
	
	private void setNextYearViewItem() {
		iMonthViewCurrentYear++;
		calStartDate.set(Calendar.DAY_OF_MONTH, 1);
		calStartDate.set(Calendar.MONTH, iMonthViewCurrentMonth);
		calStartDate.set(Calendar.YEAR, iMonthViewCurrentYear);
		updateDate();
		updateCenterTextView(iMonthViewCurrentMonth,iMonthViewCurrentYear);
	}

	private DateWidgetDayCell.OnItemClick mOnDayCellClick = new DateWidgetDayCell.OnItemClick() {
		public void OnClick(DateWidgetDayCell item) {
			calSelected.setTimeInMillis(item.getDate().getTimeInMillis());
			item.setSelected(true);
			updateCalendar();
			updateControlsState();
			Intent i = new Intent(DateWidget.this,Main.class);
			startActivity(i);
		}
	};
	
	private void updateCenterTextView(int iMonthViewCurrentMonth,int iMonthViewCurrentYear){
        monthTextView.setText(iMonthViewCurrentYear+"");
        yearTextView.setText(format(iMonthViewCurrentMonth+1)+"");
	}
	
	private void updateDate() {
		updateStartDateForMonth();
		updateCalendar();
	}

	private void updateControlsState() {
		mYear = calSelected.get(Calendar.YEAR);
		mMonth = calSelected.get(Calendar.MONTH);
		mDay = calSelected.get(Calendar.DAY_OF_MONTH);
		tv.setText("鎮ㄩ�鎷╃殑鏃ユ湡鏄細"+new StringBuilder().append(mYear).append("/").append(format(mMonth + 1)).append("/").append(format(mDay)));
		tv.setHorizontallyScrolling(true);
		SharedPreferences mPerferences = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor mEditor = mPerferences.edit();
		if (type == 0)
			mEditor.putString("check_in", format(mMonth + 1)+"-"+format(mDay)+"-"+mYear);
		else if (type == 1)
			mEditor.putString("check_out", format(mMonth + 1)+"-"+format(mDay)+"-"+mYear);
        mEditor.commit();
	}

	private String format(int x) {
		String s = "" + x;
		if (s.length() == 1)
			s = "0" + s;
		return s;
	}
}
