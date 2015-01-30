/*
 * GTAcampuS v1 Copyright (c) 2013 Godly T.Alias
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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Textviewer extends Activity {
	
	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.textviewer);
		LinearLayout textviewer = (LinearLayout) findViewById(R.id.textviewerpad);
		Animation animation = AnimationUtils.loadAnimation(Textviewer.this, R.anim.fadein);
		textviewer.startAnimation(animation);
		this.setTitle(getIntent().getStringExtra("title"));
		TextView text = (TextView) findViewById(R.id.textviewer);
		text.setText(getIntent().getStringExtra("text"));
		Button back = (Button)findViewById(R.id.goback);
		back.setOnClickListener(goback);
	};

	View.OnClickListener goback = new OnClickListener() {
		
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Textviewer.this.finish();
		}
	};
}
