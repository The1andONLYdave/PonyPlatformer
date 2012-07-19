/*
 	Copyright (C) 2012  Jake Drahos <drahos.jake@gmail.com>
 	
 	This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along
    with this program; if not, write to the Free Software Foundation, Inc.,
    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
    
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