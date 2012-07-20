/*
 	Copyright (C) 2012  Jake Drahos <drahos.jake@gmail.com>
*/

package info.qcops.twilight.sparkle.platformer;

import info.qcops.twilight.sparkle.platformer.R;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;

public class EventHandler implements OnClickListener, OnTouchListener {
	public GameActivity activity;
	
	public EventHandler(GameActivity activity){
		this.activity = activity;
	}
	public void onClick(View v) {
		switch (v.getId()){
		case R.id.button2:
			activity.entities.get(0).enabled = true;
			v.setVisibility(android.view.View.INVISIBLE);
			((Button) v).setText(R.string.resume);
			activity.findViewById(R.id.button3).setVisibility(android.view.View.VISIBLE);
			activity.findViewById(R.id.button5).setVisibility(android.view.View.INVISIBLE);
			
			if (!(activity.graphicsHandler.threadRunning))
				activity.graphicsHandler.startThread();
			
			if (!(activity.physicsHandler.threadRunning))
					activity.physicsHandler.startThread();
			break;
		case R.id.button3:
			v.setVisibility(android.view.View.INVISIBLE);
			((Button) activity.findViewById(R.id.button2)).setText(R.string.resume);
			activity.findViewById(R.id.button2).setVisibility(android.view.View.VISIBLE);
			
			activity.findViewById(R.id.button5).setVisibility(android.view.View.VISIBLE);
			
			if (activity.graphicsHandler.threadRunning)
				activity.graphicsHandler.stopThread();
			
			if (activity.physicsHandler.threadRunning)
					activity.physicsHandler.stopThread();
			break;
		case R.id.button4:
			v.setVisibility(android.view.View.INVISIBLE);
			activity.findViewById(R.id.button2).setVisibility(android.view.View.VISIBLE);
			activity.findViewById(R.id.button5).setVisibility(android.view.View.INVISIBLE);
			((Button) activity.findViewById(R.id.button2)).setText(R.string.start);
			activity.nextLevel();
			break;
		case R.id.button5:
			v.setVisibility(android.view.View.INVISIBLE);
			activity.finish();
			break;
		}
	}
	@SuppressWarnings("deprecation")
	public boolean onTouch(View v, MotionEvent event) {
		
		PlayerEntity player = ((PlayerEntity) activity.entities.get(0));
		
		switch (v.getId()){
		case R.id.button1:
			if (player.action == PlayerEntity.ACTION_ARRIVE)
				break;
			switch (event.getAction()){
			case MotionEvent.ACTION_DOWN:
				if (event.getX() < 200)
					player.commandLeft = true;
				if (event.getX() > activity.findViewById(R.id.button1).getWidth() - 200)
					player.commandRight = true;
				if ((event.getX() < activity.findViewById(R.id.button1).getWidth() - 225) && (event.getX() > 225))
					player.commandJump = true;
				break;
				
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				if ((event.getY() > 0) && (event.getX() < 200))
					player.commandLeft = false;
				if ((event.getY() > 0) && (event.getX() > activity.findViewById(R.id.button1).getWidth() - 200))
					player.commandRight = false;
				if ((event.getY() > 0) && (event.getX() < activity.findViewById(R.id.button1).getWidth() - 225) && (event.getX() > 225))
					player.commandJump = false;
				break;
				
			case MotionEvent.ACTION_POINTER_1_UP:
				if (event.getX(0) < 200)
					player.commandLeft = false;
				if (event.getX(0) > activity.findViewById(R.id.button1).getWidth() - 200)
					player.commandRight = false;
				if ((event.getX(0) < activity.findViewById(R.id.button1).getWidth() - 225) && (event.getX(0) > 225))
					player.commandJump = false;
				break;
			
			case MotionEvent.ACTION_POINTER_2_UP:
					if (event.getX(1) < 200)
						player.commandLeft = false;
					if (event.getX(1) > activity.findViewById(R.id.button1).getWidth() - 200)
						player.commandRight = false;
					if ((event.getX(1) < activity.findViewById(R.id.button1).getWidth() - 225) && (event.getX(1) > 225))
						player.commandJump = false;
					break;
				
			case MotionEvent.ACTION_MOVE:
				player.commandLeft = false;
				player.commandRight = false;
				player.commandJump = false;
				
				for (int i = 0; i < event.getPointerCount(); i++){
					if (event.getY(i) > 0){
						if (event.getX(i) < 200)
							player.commandLeft = true;
						if (event.getX(i) > activity.findViewById(R.id.button1).getWidth() - 200)
							player.commandRight = true;
						if ((event.getX(i) < activity.findViewById(R.id.button1).getWidth() - 225) && (event.getX(i) > 225))
							player.commandJump = true;
					}
				}
			}
			break;
		}
		return false;
	}
}
