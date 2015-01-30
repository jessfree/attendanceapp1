

package com.example.freeneyj.attendance2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CampusActivity extends ListActivity {
	private static final int DIALOG_ID = 0,BACKUP_ERROR=1,BACKUP = 2, RESTORE_ALERT=3, BACKUP_EXISTS=4, RESTORE =5, RESTORE_ERROR=6, PASSWORD = 7,DATABASE_ERROR=8;
	Calendar myCal;
	public int Year,month,day,hour,Minute;
	static boolean suc_flag;
	EditText pwd,uname;
	Handler gtahandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_campus);
		Animation fade = AnimationUtils.loadAnimation(this, R.anim.fadein);
		findViewById(R.id.mainlayout).startAnimation(fade);
		LinearLayout sidebar = (LinearLayout)findViewById(R.id.mainsidebar);
		if(getWindowManager().getDefaultDisplay().getWidth()<200)
			sidebar.setVisibility(View.GONE);
		if(isUpgraded()||!(isdbok()))
		{	Intent i = new Intent(CampusActivity.this, com.example.freeneyj.attendance2.Initialize.class);
			startActivityForResult(i, 0);		}
		
		initalertnotif();
		ListView list = (ListView)findViewById(android.R.id.list);
		list.setFocusable(false);
		list.setScrollbarFadingEnabled(true);
		String[] functions = new String[]{"Alerts","TimeTable","Courses","Notes","Absences"};
		ArrayAdapter<String> listadapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,functions);
		this.setListAdapter(listadapter);
		this.setTitle("Welcome to Checkin!");
		}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	initalertnotif();	
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		Intent i;
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode== Activity.RESULT_OK){
		switch(requestCode){
		case 0:
			showDialog(PASSWORD);
			break;
			
		case 1:		
				backup();
			break;
			
		case 2:
				restore();
			break;
			
		case 3:
				i = new Intent(CampusActivity.this, Settings.class);
				startActivity(i);
				finish();
				break;
		case 4:
			i= new Intent(CampusActivity.this, com.example.freeneyj.attendance2.Inbox.class);
			startActivity(i);
			break;
		default:
			break;
		}}
	}
	
	public void initalertnotif(){
		SharedPreferences alarmdet = getSharedPreferences("checkin_alarmdet", MODE_PRIVATE);
		String details = alarmdet.getString("alarmdetails", "none");
		TextView alarmdet1 = (TextView) findViewById(R.id.alarm_title1);
		TextView alarmdet2 = (TextView) findViewById(R.id.alarm_title2);
		TextView alarmdet3 = (TextView) findViewById(R.id.alarm_title3);
		View div = (View) findViewById(R.id.alertdivider);
		if(!details.equals("none"))
		{	
			alarmdet1.setVisibility(View.VISIBLE);
			alarmdet2.setVisibility(View.VISIBLE);
			alarmdet3.setVisibility(View.VISIBLE);
			alarmdet = getSharedPreferences("checkin", MODE_PRIVATE);
			alarmdet2.setText(shorten(alarmdet.getString("alarmtitle", "Custom Alert")));
			alarmdet3.setText(details);
			div.setVisibility(View.VISIBLE);	}
		else{
			alarmdet1.setVisibility(View.INVISIBLE);
			alarmdet2.setVisibility(View.INVISIBLE);
			alarmdet3.setVisibility(View.INVISIBLE);
			div.setVisibility(View.INVISIBLE);
		}
	}
		
	public String shorten(String string)
	{
		int start=0;
		StringBuilder str = new StringBuilder();
		if(string.length()>20)
		{
			str.append(string.charAt(0));
			str.append(' ');
			for(start=1;start<string.length();start++)
			{
			if(string.charAt(start-1)==' '){
			str.append(string.charAt(start));
			str.append(' ');}
			}
			return str.toString().toUpperCase(Locale.UK);
		}
		else
		return string;
	}
	
	public void exit(View v)
	{		this.finish();	}
	
	public void about(View v)
	{		showDialog(DIALOG_ID);	}
	

	

	
	public void slotdisp(View v)
	{	if(isdbok()){
		Intent slots = new Intent(CampusActivity.this, com.example.freeneyj.attendance2.Slot.class);
		startActivity(slots);
	}else
		showDialog(DATABASE_ERROR);
	}
	
	public void coursefn(View v)
	{	Intent courseintent=new Intent(CampusActivity.this, com.example.freeneyj.attendance2.courses.class);
		startActivity(courseintent);	}
	
	public void coursecheck(View v)
	{	Intent check=new Intent(CampusActivity.this, com.example.freeneyj.attendance2.CheckData.class);
		startActivity(check);	}
	
	public void notes(View v)
	{	Intent noteint=new Intent(CampusActivity.this, com.example.freeneyj.attendance2.notedata.class);
		startActivity(noteint);	}
	
	public void bunk(View v)
	{	Intent bunkmeter=new Intent(CampusActivity.this, com.example.freeneyj.attendance2.bunkom.class);
		startActivity(bunkmeter);	}
	

	
	public void alarm(View v)
	{	Intent alarmintent = new Intent(CampusActivity.this, com.example.freeneyj.attendance2.Alarmsetter.class);
		startActivity(alarmintent);			}

	public boolean isdbok(){
		com.example.freeneyj.attendance2.DataManipulator db = new com.example.freeneyj.attendance2.DataManipulator(this);
		try{
			db.gettable(com.example.freeneyj.attendance2.DataManipulator.TABLE_NAME4);
		}
		catch(RuntimeException e){
			db.close();
			return false;
		}
		db.close();
		return true;
	}
	
	public boolean isUpgraded(){
		try{
			SharedPreferences ver = getSharedPreferences("AutoAppLauncherPrefs", Context.MODE_PRIVATE);
			int lastVer = ver.getInt("last_version", 0);
			int curVer = getVer();
			if(lastVer!=curVer){
				SharedPreferences.Editor verEdit = ver.edit();
				verEdit.putInt("last_version", curVer);
				verEdit.commit();
				return true;
			}	}
		catch(Exception e){e.printStackTrace();	}
		return false;
	}
	
	public int getVer(){	int version = 0;
		try{
			version=getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
			}
		catch(NameNotFoundException e){}
		return version;	}
	
	private void restore(){
		gtahandler = new Handler();
		showDialog(RESTORE_ALERT);
	}
	
	private void backup(){
		gtahandler = new Handler();
		Thread bckup = new Thread(backup_runnable);
		bckup.setDaemon(true);
		bckup.start();
	}
	
	private final Runnable restore_runnable = new Runnable() {
		
		public void run() {
			// TODO Auto-generated method stub
			File restorefile;
			String line;
			ContentValues values;
			BufferedReader restorereader;
			suc_flag = true;
			restorefile = new File(Environment.getExternalStorageDirectory()+"/freeneyj.attendance2/Notes");
			com.example.freeneyj.attendance2.DataManipulator db = new com.example.freeneyj.attendance2.DataManipulator(CampusActivity.this);
		try {
			if(restorefile.exists()){
			File[] notes = restorefile.listFiles();
			String title,content;
			for(File note : notes){
					restorereader = new BufferedReader(new FileReader(note));
					title = restorereader.readLine().trim();
					content = "";
					line = "";
					do{
						content = content.concat(line+"\n");
						line = restorereader.readLine().trim();
					}while(!line.equals("||.endoffile().||"));
					db.insertnote(title, content);}}
				
			
			restorefile = new File(Environment.getExternalStorageDirectory()+"/freeneyj.attendance2/campus.dat");
			if(restorefile.exists()){
			restorereader = new BufferedReader(new FileReader(restorefile));
			db.deleteAll(com.example.freeneyj.attendance2.DataManipulator.TABLE_NAME1);
			line = restorereader.readLine().trim();
			values = new ContentValues();
			while(!line.equals("||.endoffile().||")){
				values.put("teacher", line);
				values.put("course", restorereader.readLine().trim());
				values.put("code", restorereader.readLine().trim());
				values.put("bunk", Integer.parseInt(restorereader.readLine().trim()));
				restorereader.readLine();
				db.insertdata(com.example.freeneyj.attendance2.DataManipulator.TABLE_NAME1, values);
				line = restorereader.readLine().trim();
			}
			}
			else suc_flag=false;
			
			restorefile = new File(Environment.getExternalStorageDirectory()+"/freeneyj.attendance2/alarms.dat");
			if(restorefile.exists()){
			restorereader = new BufferedReader(new FileReader(restorefile));
			db.deleteAll(com.example.freeneyj.attendance2.DataManipulator.TABLE_NAME3);
			line = restorereader.readLine().trim();
			values = new ContentValues();
			while(!line.equals("||.endoffile().||")){
				values.put("id", Integer.parseInt(line));
				values.put("year", Integer.parseInt(restorereader.readLine().trim()));
				values.put("month", Integer.parseInt(restorereader.readLine().trim()));
				values.put("day",  Integer.parseInt(restorereader.readLine().trim()));
				values.put("hour", Integer.parseInt(restorereader.readLine().trim()));
				values.put("minute", Integer.parseInt(restorereader.readLine().trim()));
				values.put("title", restorereader.readLine().trim());
				values.put("type", restorereader.readLine().trim());
				values.put("status",  Integer.parseInt(restorereader.readLine().trim()));
				values.put("snooze",  Integer.parseInt(restorereader.readLine().trim()));
				values.put("shakemode",  Integer.parseInt(restorereader.readLine().trim()));
				values.put("mathsolver",  Integer.parseInt(restorereader.readLine().trim()));
				values.put("sun",  Integer.parseInt(restorereader.readLine().trim()));
				values.put("mon",  Integer.parseInt(restorereader.readLine().trim()));
				values.put("tue",  Integer.parseInt(restorereader.readLine().trim()));
				values.put("wed",  Integer.parseInt(restorereader.readLine().trim()));
				values.put("thu",  Integer.parseInt(restorereader.readLine().trim()));
				values.put("fri",  Integer.parseInt(restorereader.readLine().trim()));
				values.put("sat",  Integer.parseInt(restorereader.readLine().trim()));
				restorereader.readLine();
				db.insertdata(com.example.freeneyj.attendance2.DataManipulator.TABLE_NAME3, values);
				line = restorereader.readLine().trim();
			}
			}else suc_flag=false;
			
			restorefile = new File(Environment.getExternalStorageDirectory()+"/freeneyj.attendance2/bunks.dat");
			if(restorefile.exists()){
			restorereader = new BufferedReader(new FileReader(restorefile));
			db.deleteAll(com.example.freeneyj.attendance2.DataManipulator.TABLE_NAME5);
			line = restorereader.readLine().trim();
			values = new ContentValues();
			while(!line.equals("||.endoffile().||")){
				values.put("course", line);
				values.put("bunkdate", Long.parseLong(restorereader.readLine().trim()));
				restorereader.readLine();
				db.insertdata(com.example.freeneyj.attendance2.DataManipulator.TABLE_NAME5, values);
				line = restorereader.readLine().trim();
			}
			}else suc_flag=false;
			
			
			restorefile = new File(Environment.getExternalStorageDirectory()+"/freeneyj.attendance2/courses.dat");
			if(restorefile.exists()){
			restorereader = new BufferedReader(new FileReader(restorefile));
			db.deleteAll(com.example.freeneyj.attendance2.DataManipulator.TABLE_NAME4);
			int k;
			line = restorereader.readLine().trim();
			values = new ContentValues();
			while(!line.equals("||.endoffile().||")){
				values.put("DAY_ID", Integer.parseInt(line));
				line = restorereader.readLine().trim();
				k=1;
				while(!line.equals("")){
					values.put("HOUR"+k, line);
					k++;
					line = restorereader.readLine().trim();
				}
				if(values.getAsInteger("DAY_ID")==1) //initialising tables at first loop only
				db.coursetableinit(k-1);
				db.insertdata(com.example.freeneyj.attendance2.DataManipulator.TABLE_NAME4, values);
				line = restorereader.readLine().trim();
			}
			}
			else{suc_flag=false;}
			
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				suc_flag= false;
				e.printStackTrace();	}
			catch(IOException e){suc_flag= false;
			e.printStackTrace();	}
			db.close();
			gtahandler.post(new Runnable() {
				
				public void run() {
					// TODO Auto-generated method stub
					if(suc_flag){
					Intent i = new Intent(getBaseContext(), com.example.freeneyj.attendance2.MyAlarm.class);
					i.setAction("setalarm");
					startService(i);
					initalertnotif();
					showDialog(RESTORE);
					}
					else
						showDialog(RESTORE_ERROR);
				}
			});
		}
	};
	
	private final Runnable backup_runnable = new Runnable() {
		
		public void run() {
			// TODO Auto-generated method stub
			File backupfile;
			com.example.freeneyj.attendance2.DataManipulator db = new com.example.freeneyj.attendance2.DataManipulator(CampusActivity.this);
			List<String[]> data = db.selectAllnotes();
			suc_flag = true;
			try{
		for(String[] note : data){
			backupfile = new File(Environment.getExternalStorageDirectory() + "/freeneyj.attendance2/Notes/"+note[1] +".txt");
			backupfile.mkdirs();
			if(backupfile.exists())
				backupfile.delete();
				backupfile.createNewFile();
				BufferedWriter backupwriter = new BufferedWriter(new FileWriter(backupfile));
				backupwriter.write(note[1]+"\n");
				backupwriter.write(note[2]+"\n||.endoffile().||");
				backupwriter.flush();
				backupwriter.close();
			}

			backupfile = new File(Environment.getExternalStorageDirectory() + "/freeneyj.attendance2/campus.dat");
			if(backupfile.exists())
				backupfile.delete();
				backupfile.createNewFile();
				data = db.selectAll();
				BufferedWriter backupwriter = new BufferedWriter(new FileWriter(backupfile));
				for(String[] coursedet : data)
				backupwriter.write(coursedet[3]+"\n"+coursedet[0]+"\n"+coursedet[1]+"\n"+coursedet[2]+"\n\n");	
				backupwriter.write("||.endoffile().||");
				backupwriter.flush();
				backupwriter.close();
			}
			catch(IOException e){e.printStackTrace();
					suc_flag=false;
			}
			catch(Exception e){e.printStackTrace();
			suc_flag=false;}
			
			backupfile = new File(Environment.getExternalStorageDirectory() + "/freeneyj.attendance2/alarms.dat");
			writetosd(backupfile, db.fetchalarms());
			
			backupfile = new File(Environment.getExternalStorageDirectory() + "/freeneyj.attendance2/bunks.dat");
			writetosd(backupfile, db.gettable(com.example.freeneyj.attendance2.DataManipulator.TABLE_NAME5));
			
			
			backupfile = new File(Environment.getExternalStorageDirectory() + "/freeneyj.attendance2/courses.dat");
			writetosd(backupfile, db.gettable(com.example.freeneyj.attendance2.DataManipulator.TABLE_NAME4));
			
			db.close();
			

				gtahandler.post(new Runnable() {
					
					public void run() {
						// TODO Auto-generated method stub
						if(suc_flag)
						showDialog(BACKUP);		
						else
							showDialog(BACKUP_ERROR);
					}
				}); 
		}
	};
	
	private void writetosd(File backupfile,Cursor datacursor){
		if(backupfile.exists())
			backupfile.delete();
		try{
			backupfile.createNewFile();
			datacursor.moveToFirst();
			BufferedWriter backupwriter = new BufferedWriter(new FileWriter(backupfile));
			while(!datacursor.isAfterLast())
			{
			for(int j=0;j<datacursor.getColumnCount();j++)
				backupwriter.write(datacursor.getString(j)+"\n");
			backupwriter.write("\n");
			datacursor.moveToNext();}
			backupwriter.write("||.endoffile().||");
			backupwriter.flush();
			backupwriter.close();
			datacursor.close();
		}
		catch(IOException e){e.printStackTrace();gtahandler.post(new Runnable() {
			
			public void run() {
				// TODO Auto-generated method stub
				suc_flag=false;
				showDialog(BACKUP_ERROR);
			}
		});}
		catch(Exception e){e.printStackTrace();gtahandler.post(new Runnable() {
			
			public void run() {
				// TODO Auto-generated method stub
				suc_flag=false;
				showDialog(BACKUP_ERROR);
			}
		});}
	}
	
	public void getsettings(View v){
		startActivityForResult(new Intent(CampusActivity.this, com.example.freeneyj.attendance2.Password.class), 3);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, Menu.FIRST+1, 0, "Exit").setIcon(R.drawable.ic_close);
		menu.add(0, Menu.FIRST+2, 1, "About App").setIcon(R.drawable.about);
		menu.add(0, Menu.FIRST+3, 2, "Backup").setIcon(android.R.drawable.ic_partial_secure);
		menu.add(0, Menu.FIRST+4, 3, "Restore").setIcon(android.R.drawable.stat_notify_sdcard);
		menu.add(0, Menu.FIRST+5, 4, "Settings").setIcon(android.R.drawable.ic_lock_lock);
		return super.onCreateOptionsMenu(menu);	}
	
	
	
	public boolean onOptionsItemSelected(MenuItem menu)
	{ File bfile;
		switch(menu.getItemId())
		{
		case Menu.FIRST+1:
			this.finish();
			break;
		
		case Menu.FIRST+2:
			showDialog(DIALOG_ID);
			break;
			
		case Menu.FIRST+3:
			bfile = new File(Environment.getExternalStorageDirectory()+"/freeneyj.attendance2/courses.dat");
		if(bfile.exists())
			showDialog(BACKUP_EXISTS);
		else
			startActivityForResult(new Intent(this, com.example.freeneyj.attendance2.Password.class), 1);
			break;
			
		case Menu.FIRST+4:
			bfile = new File(Environment.getExternalStorageDirectory()+"/freeneyj.attendance2/courses.dat");
		if(bfile.exists())
			startActivityForResult(new Intent(this, com.example.freeneyj.attendance2.Password.class), 2);
		else
			showDialog(RESTORE_ERROR);
		break;
		
		case Menu.FIRST+5:
			startActivityForResult(new Intent(CampusActivity.this, com.example.freeneyj.attendance2.Password.class), 3);
		break;
		
		default:			break;
			}		
		return true;	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		switch(position)
		{
		case 0:alarm(v);
				break;
		case 1:slotdisp(v);
				break;
		case 2:coursecheck(v);
				break;
		case 3:notes(v);
				break;
		case 4:bunk(v);
				break;

		default : break;
		}
	}
	
	
	protected final Dialog onCreateDialog(final int id){
		Dialog dialog=null;
		AlertDialog.Builder builder = new AlertDialog.Builder(CampusActivity.this);
		
		switch(id){
		case DIALOG_ID:
		builder.setMessage(" test test test test test")
		.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dismissDialog(DIALOG_ID);
			}
		})
		.setNeutralButton("Help", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Intent read = new Intent(CampusActivity.this, com.example.freeneyj.attendance2.Textviewer.class);
				read.putExtra("text", getString(R.string.help));
				read.putExtra("title", "checkin Help");
				startActivity(read);
				dismissDialog(DIALOG_ID);
			}
		});
		break;
		
		case BACKUP : 
			builder.setMessage("Backup done succesfully!")
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dismissDialog(BACKUP);
				}
			});
			break;
		
		case BACKUP_ERROR:
			builder.setMessage("An error happened during backup process. Check your SD card")
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dismissDialog(BACKUP_ERROR);
				}
			});
			break;
			
		case RESTORE_ALERT:
			builder.setMessage("Restoring database will clear all your current data! ")
			.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Thread restore = new Thread(restore_runnable);
					restore.setDaemon(true);
					restore.start();
				}
			})
			.setNegativeButton("Abort", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dismissDialog(RESTORE_ALERT);
				}
			});
			break;
			
		case BACKUP_EXISTS:
			builder.setMessage("A backup already exists!")
			.setPositiveButton("Overwrite", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					startActivityForResult(new Intent(CampusActivity.this, com.example.freeneyj.attendance2.Password.class), 1);
					dismissDialog(BACKUP_EXISTS);
				}
			})
			.setNegativeButton("Abort Operation", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dismissDialog(BACKUP_EXISTS);
				}
			});
			
			break;
			
		case RESTORE:
			builder.setMessage("Database restored successfully")
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dismissDialog(RESTORE);
				}
			});
			break;
	
		case RESTORE_ERROR:
			builder.setMessage("Your backup folder is corrupted / missing!")
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dismissDialog(RESTORE_ERROR);
				}
			});
			break;
			
		case PASSWORD:
			LinearLayout lll = new LinearLayout(this);
			lll.setOrientation(LinearLayout.VERTICAL);
			LinearLayout ll = new LinearLayout(this);
			ll.setOrientation(LinearLayout.HORIZONTAL);
			TextView utext = new TextView(this);
			utext.setText("Set a User name\t\t\t\t");
			utext.setPadding(2, 2, 2, 2);
			utext.setClickable(false);
			utext.setTextColor(getResources().getColor(android.R.color.black));
			uname = new EditText(this);
			uname.setInputType(InputType.TYPE_CLASS_TEXT);
			uname.setEms(15);
			uname.setText("freeneyj.attendance2 User");
			uname.setFocusable(true);
			uname.setTextColor(getResources().getColor(android.R.color.black));
			ll.addView(utext);
			ll.addView(uname);
			lll.addView(ll);
			ll= new LinearLayout(this);
			ll.setOrientation(LinearLayout.HORIZONTAL);
			TextView text = new TextView(this);
			text.setText("Set a master password\t");
			text.setPadding(2, 2, 2, 2);
			text.setClickable(false);
			text.setTextColor(getResources().getColor(android.R.color.black));
			pwd = new EditText(this);
			pwd.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
			pwd.setEms(15);
			pwd.setFocusable(true);
			pwd.setTextColor(getResources().getColor(android.R.color.darker_gray));
			ll.addView(text);
			ll.addView(pwd);
			lll.addView(ll);
			builder.setView(lll)
			.setCancelable(false)
			.setPositiveButton("Set", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					SharedPreferences settings = getSharedPreferences("freeneyj.attendance2ettings", MODE_PRIVATE);
					SharedPreferences.Editor settingseditor = settings.edit();
					settingseditor.putInt("Password", pwd.getText().toString().hashCode());
					settingseditor.putString("Username", uname.getEditableText().toString());
					settingseditor.putString("server", "NULL");
					settingseditor.putBoolean("coursealerts", true);
					settingseditor.putBoolean("notifications", true);
					settingseditor.putInt("alerttime", 10);
					settingseditor.putInt("snoozetime", 3);
					settingseditor.putInt("interval", 5);
					settingseditor.putLong("boot", System.currentTimeMillis());
					settingseditor.commit();
					removeDialog(PASSWORD);
				}
			});
			break;
			
		case DATABASE_ERROR:
			builder.setMessage("Your database is not set properly. Initialize the application")
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dismissDialog(DATABASE_ERROR);
				}
			});
			break;
			
			default: break;
				}
		AlertDialog alert=builder.create();
		 dialog=alert;
			return dialog;
}
}