/*
 * freeneyj.attendance2 v1 Copyright (c) 2013 Godly T.Alias
 * 
   This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
 */

package com.example.freeneyj.attendance2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TimePicker;

import java.util.Calendar;

public class Alarmsetter extends ListActivity {
	
	Calendar myCal;
	int Year,month,day,hour,Minute,count=0,itemid=0, alarmid=-1;
	final int ALARM_DIALOG_TIME=0, ALARM_DIALOG_DATE=1,FINALIZE=2,DELETE_ALARM=3;
	int[] ids;
	Cursor alarms;
	String alarmtitle;
	Button newalarm, newtask;
	com.example.freeneyj.attendance2.DataManipulator db;
	ListView list;
	
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarmslayout);
		initlist();
		list = (ListView) findViewById(android.R.id.list);
		list.setOnItemLongClickListener(clicked);
		myCal = Calendar.getInstance();
		Year  = myCal.get(Calendar.YEAR);
	    month= myCal.get(Calendar.MONTH);
		day = myCal.get(Calendar.DAY_OF_MONTH);
		hour = myCal.get(Calendar.HOUR_OF_DAY);
		Minute = myCal.get(Calendar.MINUTE);
		newtask=(Button)findViewById(R.id.add_new);
		newtask.setOnClickListener(settask);
		newalarm = (Button)findViewById(R.id.add_alarm);
		newalarm.setOnClickListener(setalarm);
	};
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
			}

	
	private void initlist(){
		db = new com.example.freeneyj.attendance2.DataManipulator(this);
		alarms = db.fetchalarms();
		String[] alarmlist = new String[alarms.getCount()];
		if(alarms!=null)
		{
			alarms.moveToFirst();
			count=0;
			while(!alarms.isAfterLast())
			{
				alarmlist[count]=alarms.getString(6);
				count++;
				alarms.moveToNext();
			}
		ArrayAdapter<String> alarmadapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,alarmlist);
		this.setListAdapter(alarmadapter);
		}
		alarms.close();
		db.close();
	}
	
	private View.OnClickListener setalarm = new View.OnClickListener() {
		
		public void onClick(View v) {
			// TODO Auto-generated method stub
			itemid=0;
			showDialog(ALARM_DIALOG_TIME);
		}
	};
	private View.OnClickListener settask = new View.OnClickListener() {
		
		public void onClick(View v) {
			// TODO Auto-generated method stub
			itemid=1;
			showDialog(ALARM_DIALOG_TIME);
		}
	};
	private DatePickerDialog.OnDateSetListener dateselector = new DatePickerDialog.OnDateSetListener() {
		
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			// TODO Auto-generated method stub
			Year=year;
			month=monthOfYear;
			day=dayOfMonth;
			showDialog(FINALIZE);
		}
	};
	
	private TimePickerDialog.OnTimeSetListener timeselector = new TimePickerDialog.OnTimeSetListener() {
		
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// TODO Auto-generated method stub
			hour=hourOfDay;
			Minute=minute;
			if(itemid==1)
			showDialog(ALARM_DIALOG_DATE);
			else
			showDialog(FINALIZE);
		}
			
	};
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode==10){
			if(resultCode== Activity.RESULT_OK){
				db = new com.example.freeneyj.attendance2.DataManipulator(Alarmsetter.this);
				db.deletealarm(alarmid);
				db.close();
				initlist();
				Intent i = new Intent(getBaseContext(), com.example.freeneyj.attendance2.MyAlarm.class);
				i.setAction("setalarm");
				startService(i);
			}
		}
		else if(resultCode== Activity.RESULT_OK)
		{
			if(data.getStringExtra("operation").equals("delete"))
				 showDialog(DELETE_ALARM);
				
			else
			{
				db=new com.example.freeneyj.attendance2.DataManipulator(this);
			if(requestCode==2)
					db.alarmsave(data);
					
		if(requestCode==3)
				db.alarmupdate(data,alarmid);
		db.close();
		Intent i = new Intent(getBaseContext(), com.example.freeneyj.attendance2.MyAlarm.class);
		i.setAction("setalarm");
		startService(i);
		initlist();
			}
			
		}
			};
	
	protected final Dialog onCreateDialog(final int id)
	{
		switch(id)
		{
		case ALARM_DIALOG_TIME:
			return new TimePickerDialog(this, timeselector, hour, Minute, false);
	
		case ALARM_DIALOG_DATE:
			/*
			LayoutInflater inflate = this.getLayoutInflater();
			AlertDialog.Builder alarmDialog = new AlertDialog.Builder(this);
			alarmDialog.setTitle(R.string.alarmtitle)
			.setView(inflate.inflate(R.layout.alarm, null))
			.setPositiveButton("Set", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Intent alintent=new Intent(CampusActivity.this,MyAlarm.class);
					startActivity(alintent);});*/
			return new DatePickerDialog(this,dateselector,Year,month,day);
			
		case FINALIZE:
			AlertDialog.Builder confirm = new AlertDialog.Builder(this);
			confirm.setTitle("Setting new alert")
			
			.setCancelable(false)
			
			.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Intent alintent=new Intent(Alarmsetter.this, com.example.freeneyj.attendance2.AlarmOptions.class);
					alintent.putExtra("year", Year);
					alintent.putExtra("month", month);
					alintent.putExtra("day", day);
					alintent.putExtra("alarmid", -1);
					alintent.putExtra("hour", hour);
					alintent.putExtra("minute", Minute);
					alintent.putExtra("itemid", itemid);
					removeDialog(FINALIZE);
					startActivityForResult(alintent, 2);
				}
			})
			
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					removeDialog(FINALIZE);
				}
			});
			if(itemid==1)
				confirm.setMessage("\nTask Time           " + ((hour%12==0)?12:hour%12) + " : " + Minute + " " + ((hour/12 > 0)?"PM":"AM") + "\n\nAlarm Date          " + day + " / " +(month+1)+ " / " + Year + "\n\n");
			if(itemid==0)
				confirm.setMessage("\nAlarm Time          " + ((hour%12==0)?12:hour%12) + " : " + Minute + " " + ((hour/12 > 0)?"PM":"AM") + "\n\n");
			return confirm.create();
			
		case DELETE_ALARM :
			AlertDialog.Builder delalarm = new AlertDialog.Builder(this);
			delalarm.setTitle("Deleting alert")
			.setMessage("Are you sure to delete the alert, "+alarmtitle+"?!")
			.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					startActivityForResult(new Intent(Alarmsetter.this, com.example.freeneyj.attendance2.Password.class), 10);
					removeDialog(DELETE_ALARM);
				}
			})
			.setNegativeButton("No", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					removeDialog(DELETE_ALARM);
				}
			})
			.setCancelable(false);
			return delalarm.create();
		}
		return null;
	}
	
	AdapterView.OnItemLongClickListener clicked = new OnItemLongClickListener() {

		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			// TODO Auto-generated method stub
			db = new com.example.freeneyj.attendance2.DataManipulator(Alarmsetter.this);
			alarms = db.fetchalarms();
			alarms.moveToPosition(arg2);
			alarmid = alarms.getInt(0);
			alarmtitle = alarms.getString(6);
			showDialog(DELETE_ALARM);
			alarms.close();
			db.close();
			return false;
		}
	};
	

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		
		db = new com.example.freeneyj.attendance2.DataManipulator(Alarmsetter.this);
		alarms = db.fetchalarms();
		alarms.moveToPosition(position);
		Intent alintent=new Intent(Alarmsetter.this, com.example.freeneyj.attendance2.AlarmOptions.class);
		alarmid = alarms.getInt(0);
		alarmtitle = alarms.getString(6);
		alintent.putExtra("year", alarms.getInt(1));
		alintent.putExtra("month", alarms.getInt(2));
		alintent.putExtra("day", alarms.getInt(3));
		alintent.putExtra("alarmid", alarmid);
		alintent.putExtra("hour", alarms.getInt(4));
		alintent.putExtra("minute", alarms.getInt(5));
		alintent.putExtra("alarmtitle", alarmtitle);
		if(alarms.getString(7).equals("alarm"))
			itemid=0;
		else itemid=1;

		alintent.putExtra("itemid", itemid);
		alintent.putExtra("alarmstat", !(alarms.getInt(8)==0));
		alintent.putExtra("snoozetime", alarms.getInt(9));
		alintent.putExtra("shake_snooze", (alarms.getInt(10)!=0));
		alintent.putExtra("mathsolver", !(alarms.getInt(11)==0));
		alintent.putExtra("sun", !(alarms.getInt(12)==0));
		alintent.putExtra("mon", !(alarms.getInt(13)==0));
		alintent.putExtra("tue", !(alarms.getInt(14)==0));
		alintent.putExtra("wed", !(alarms.getInt(15)==0));
		alintent.putExtra("thu", !(alarms.getInt(16)==0));
		alintent.putExtra("fri", !(alarms.getInt(17)==0));
		alintent.putExtra("sat", !(alarms.getInt(18)==0));
		startActivityForResult(alintent, 3);
		
		alarms.close();
		db.close();
	}
}
