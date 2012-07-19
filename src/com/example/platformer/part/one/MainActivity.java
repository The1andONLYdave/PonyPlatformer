/*
 	Copyright (C) 2012  Jake Drahos <drahos.jake@gmail.com>
*/

package com.example.platformer.part.one;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Spinner;

public class MainActivity extends Activity implements OnClickListener  {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		
		((Button) findViewById(R.id.button1)).setOnClickListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()){
		case R.id.button1:
			startActivity(new Intent(this, GameActivity.class).putExtra("level", R.raw.level_01 + ((Spinner) findViewById(R.id.spinner1)).getSelectedItemPosition()));
		}
	}
}