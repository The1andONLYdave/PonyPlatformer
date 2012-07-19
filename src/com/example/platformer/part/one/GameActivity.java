/*
 	Copyright (C) 2012  Jake Drahos <drahos.jake@gmail.com>
*/

package com.example.platformer.part.one;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class GameActivity extends Activity {
	static final int LEVEL_COUNT = 10;
	
	ArrayList<GameEntity> entities;
	GraphicsHandler graphicsHandler;
	TextView debugText;
	PhysicsHandler physicsHandler;
	EventHandler eventHandler;
	
	int level;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        
        debugText = (TextView) findViewById(R.id.textView1);
        
        entities = new ArrayList<GameEntity>();
        
        level = getIntent().getExtras().getInt("level");
        
        parseLevelFile(level);
        
		physicsHandler = new PhysicsHandler(this);
        graphicsHandler = new GraphicsHandler(this);
        eventHandler = new EventHandler(this);
        
		((SurfaceView) findViewById(R.id.surfaceView1)).getHolder().addCallback(graphicsHandler);
		((Button) findViewById(R.id.button2)).setOnClickListener(eventHandler);
		((ImageButton) findViewById(R.id.button3)).setOnClickListener(eventHandler);
		((ImageButton) findViewById(R.id.button4)).setOnClickListener(eventHandler);
		((ImageButton) findViewById(R.id.button5)).setOnClickListener(eventHandler);
		findViewById(R.id.button1).setOnTouchListener(eventHandler);
    }
    
    @Override
    public void onPause() {
    	super.onPause();

    	findViewById(R.id.button3).setVisibility(android.view.View.INVISIBLE);
		findViewById(R.id.button2).setVisibility(android.view.View.VISIBLE);
		
		if (graphicsHandler.threadRunning)
			graphicsHandler.stopThread();
		if (physicsHandler.threadRunning)
			physicsHandler.stopThread();
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public void nextLevel() {
		if (graphicsHandler.threadRunning)
			graphicsHandler.stopThread();
		if (physicsHandler.threadRunning)
			physicsHandler.stopThread();
		
		try {
			Thread.sleep(250); //Let other threads die
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		entities.clear();
        
		if (level == (LEVEL_COUNT + R.raw.level_01 - 1))
			finish();
		
		else {
	        parseLevelFile(++level);
	        
	        physicsHandler.spawnLockScreen = true;
	        graphicsHandler.refreshEntities();
		}
    }
    
    private void parseLevelFile(int id){
		String levelString = "";
		String tempString = "";
		
    	try {
			BufferedReader reader = new BufferedReader (new InputStreamReader(getResources().openRawResource(id)));
			
			while ((tempString = reader.readLine()) != null){
				if (tempString.startsWith("#"))
					continue;
				levelString += tempString + "\n";
			}
			
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	levelString = levelString.replace("\n", "");
    	levelString = levelString.replace("\r", "");
    	levelString = levelString.replace(" ", "");
    	levelString = levelString.replace("\t", "");
    	
    	String segment = levelString.substring(0, levelString.indexOf("~"));
    	
    	entities.add(new PlayerEntity(Integer.parseInt(segment.substring(0, segment.indexOf(","))), Integer.parseInt(segment.substring(segment.indexOf(",") + 1))));
    	
    	levelString = levelString.substring(levelString.indexOf("~") + 1);
    	
    	String value;
    	EntityType type = null;
    	int x, y, w, h;
    	
    	while(true) {
    		if (levelString.equals(new String("!")))
    			break;
    		segment = levelString.substring(0, levelString.indexOf(";"));
    		levelString = levelString.substring(levelString.indexOf(";") + 1);
    		
	    	value = segment.substring(0, segment.indexOf(","));
	    	segment = segment.substring(segment.indexOf(",") + 1);
	    	
	    	if (value.equals("b_g_64_64"))
	    			type = EntityType.BLOCK_GRASS_64_64;
	    	else if (value.equals("b_g_64_128"))
    			type = EntityType.BLOCK_GRASS_64_128;
	    	else if (value.equals("b_g_128_64"))
    			type = EntityType.BLOCK_GRASS_128_64;
	    	else if (value.equals("b_g_128_128"))
    			type = EntityType.BLOCK_GRASS_128_128;
	    	else if (value.equals("books"))
    			type = EntityType.BOOKS;

	    	value = segment.substring(0, segment.indexOf(","));
	    	segment = segment.substring(segment.indexOf(",") + 1);	    	
	    	x = Integer.parseInt(value);
	    	
	    	value = segment.substring(0, segment.indexOf(","));
	    	segment = segment.substring(segment.indexOf(",") + 1);	    	
	    	y = Integer.parseInt(value);
	    	
	    	value = segment.substring(0, segment.indexOf(","));
	    	segment = segment.substring(segment.indexOf(",") + 1);	    	
	    	w = Integer.parseInt(value);
	    	
	    	h = Integer.parseInt(segment);
	    	
	    	entities.add(new GameEntity(type, x, y, w, h));
	    	entities.get(entities.size() - 1).enabled = true;
	    	if (type == EntityType.BOOKS){
		    	entities.get(entities.size() - 1).collides = false;
	    	}
    	}
    }
}
