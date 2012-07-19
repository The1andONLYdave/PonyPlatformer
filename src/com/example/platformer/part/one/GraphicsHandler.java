/*
 	Copyright (C) 2012  Jake Drahos <drahos.jake@gmail.com>
*/

package com.example.platformer.part.one;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GraphicsHandler implements SurfaceHolder.Callback, Runnable{
	static final int ARRIVE_LENGTH = 14;
	static final int GALLOP_LENGTH = 12;
	static final int IDLE_LENGTH = 16;
	static final int READ_LENGTH = 60;
	
	static final int ARRIVE_DELAY = 75;
	static final int GALLOP_DELAY = 50;
	static final int IDLE_DELAY = 150;
	static final int READ_DELAY = 60;
	
	static final int IDLE_OFFSET = 0;
	static final int GALLOP_OFFSET = -4;
	static final int ARRIVE_OFFSET = 0;
	static final int SLIDE_OFFSET = 3;
	
	public GameActivity activity;
	int canvasWidth, canvasHeight;
	int yOffset = 0;
	Rect backgroundRect;
	Paint backgroundPaint;
	Thread graphicsThread;
	
	boolean threadRunning = false;
	
	public GraphicsHandler(GameActivity activity){
		this.activity = activity;
		
		backgroundRect = new Rect(0,0,1,1);
		backgroundPaint = new Paint();
		backgroundPaint.setColor(Color.BLUE);
	}
	
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		this.canvasWidth = width;
		this.canvasHeight = height;
		
		backgroundRect.set(new Rect( 0, 0, width, height));
		
		refreshEntities();
	}

	public void stopThread(){
		threadRunning = false;
		graphicsThread.interrupt();
	}
	
	public void startThread(){
		threadRunning = true;
		graphicsThread = new Thread(this);
		graphicsThread.start();
	}
	
	public void surfaceCreated(SurfaceHolder holder) {
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
	}

	public void refreshEntities(){
		SurfaceHolder holder = ((SurfaceView) activity.findViewById(R.id.surfaceView1)).getHolder();
        Canvas canvas = holder.lockCanvas();
        
       	canvas.drawRect(backgroundRect, backgroundPaint);
        
        for (int i = 1; i < activity.entities.size(); i++)
        	if (activity.entities.get(i).enabled)
        		drawEntityByType(activity.entities.get(i), canvas);
        
        if(activity.entities.get(0).enabled)
        	drawEntityByType(activity.entities.get(0), canvas);
        
        holder.unlockCanvasAndPost(canvas);
	}
	
	private void drawEntityByType(GameEntity entity, Canvas canvas){
		BitmapDrawable sprite = null;
		Rect spriteRect = null;
		switch (entity.type){
		case PLAYER:
			sprite = getPlayerSprite();
			spriteRect = new Rect(
					(int) entity.posX - sprite.getIntrinsicWidth()/2, 
					(int) entity.posY - sprite.getIntrinsicHeight()/2 - yOffset, 
					(int) entity.posX + sprite.getIntrinsicWidth()/2, 
					(int) entity.posY + sprite.getIntrinsicHeight()/2 - yOffset);
			break;
		case BLOCK_GRASS_64_64:
			sprite = ((BitmapDrawable) activity.getResources().getDrawable(R.drawable.b_g_64_64));
			break;
		case BLOCK_GRASS_64_128:
			sprite = ((BitmapDrawable) activity.getResources().getDrawable(R.drawable.b_g_64_128));
			break;
		case BLOCK_GRASS_128_64:
			sprite = ((BitmapDrawable) activity.getResources().getDrawable(R.drawable.b_g_128_64));
			break;
		case BLOCK_GRASS_128_128:
			sprite = ((BitmapDrawable) activity.getResources().getDrawable(R.drawable.b_g_128_128));
			break;
		case BOOKS:
			sprite = ((BitmapDrawable) activity.getResources().getDrawable(R.drawable.books));
			break;
		}
		
		if (entity.type != EntityType.PLAYER)
			spriteRect = new Rect(
					(int) entity.posX - entity.width/2, 
					(int) entity.posY - entity.height/2, 
					(int) entity.posX + entity.width/2, 
					(int) entity.posY + entity.height/2);
		
		canvas.drawBitmap(sprite.getBitmap(), null, spriteRect, null);
	}
	
	private BitmapDrawable getPlayerSprite(){
		PlayerEntity player = (PlayerEntity) activity.entities.get(0);
		BitmapDrawable sprite = null;
		switch (player.lockedAction){
		case PlayerEntity.ACTION_ARRIVE:
			sprite =  ((BitmapDrawable) activity.getResources().getDrawable(R.drawable.a_r_01 + player.currentFrame));
			
			yOffset = ARRIVE_OFFSET;
			break;
		case PlayerEntity.ACTION_IDLE:
			if (player.direction == PlayerEntity.DIRECTION_LEFT)
				sprite =  ((BitmapDrawable) activity.getResources().getDrawable(R.drawable.s_l_01 + player.currentFrame));
			
			if (player.direction == PlayerEntity.DIRECTION_RIGHT)
				sprite =  ((BitmapDrawable) activity.getResources().getDrawable(R.drawable.s_r_01 + player.currentFrame));
			
			yOffset = IDLE_OFFSET;
			break;
		case PlayerEntity.ACTION_GALLOP:
			if (player.direction == PlayerEntity.DIRECTION_LEFT)
				sprite =  ((BitmapDrawable) activity.getResources().getDrawable(R.drawable.g_l_01 + player.currentFrame));
			
			if (player.direction == PlayerEntity.DIRECTION_RIGHT)
				sprite =  ((BitmapDrawable) activity.getResources().getDrawable(R.drawable.g_r_01 + player.currentFrame));
			
			yOffset = GALLOP_OFFSET;
			break;
		case PlayerEntity.ACTION_SLIDE:
			if (player.direction == PlayerEntity.DIRECTION_LEFT)
				sprite =  ((BitmapDrawable) activity.getResources().getDrawable(R.drawable.slide_l));
			
			if (player.direction == PlayerEntity.DIRECTION_RIGHT)
				sprite =  ((BitmapDrawable) activity.getResources().getDrawable(R.drawable.slide_r));
			
			yOffset = SLIDE_OFFSET;
			break;
		case PlayerEntity.ACTION_READ:
			sprite =  ((BitmapDrawable) activity.getResources().getDrawable(R.drawable.read_01 + player.currentFrame));
			break;
		}
		return sprite;
	}
	
	public void run() {
		PlayerEntity player = (PlayerEntity) activity.entities.get(0);
		
		long lastTimeChanged = System.currentTimeMillis();
		
		while (true) {
			if (Thread.interrupted())
				return;
			
			if (System.currentTimeMillis() >= (lastTimeChanged + player.frameDelay)){
				player.lockedAction = player.action;
				player.lockedWidth = player.width;
				player.lockedHeight = player.height;
				player.lockedPosX = player.posX;
				player.lockedPosY = player.posY;
				
				if (player.enabled)
					player.currentFrame++;
				
				if (player.lockedAction != player.oldAction)
					player.currentFrame = 0;
				
				if ((player.lockedAction == PlayerEntity.ACTION_IDLE) && (player.currentFrame >= IDLE_LENGTH))
					player.currentFrame = 0;
				
				if ((player.lockedAction == PlayerEntity.ACTION_GALLOP) && (player.currentFrame >= GALLOP_LENGTH))
					player.currentFrame = 0;
				
				if ((player.lockedAction == PlayerEntity.ACTION_READ) && (player.currentFrame >= READ_LENGTH))
					player.currentFrame = 0;
				
				if ((player.lockedAction == PlayerEntity.ACTION_ARRIVE) && (player.currentFrame >= ARRIVE_LENGTH)){
					player.lockedAction = PlayerEntity.ACTION_IDLE;
					player.action = player.lockedAction;
				}
				
				player.oldAction = player.action;
				
				switch (player.lockedAction){
				case PlayerEntity.ACTION_GALLOP: 
					player.frameDelay = GALLOP_DELAY;
					break;
				case PlayerEntity.ACTION_IDLE: 
					player.frameDelay = IDLE_DELAY;
					break;
				case PlayerEntity.ACTION_ARRIVE: 
					player.frameDelay = ARRIVE_DELAY;
					break;
				case PlayerEntity.ACTION_READ: 
					player.frameDelay = READ_DELAY;
					break;
				}
					
				lastTimeChanged = System.currentTimeMillis();
			}
			
			refreshEntities();
		}
	}
}
