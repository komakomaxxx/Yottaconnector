package com.example.sample.Radar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.example.yottaconnecter.Node;
import com.example.yottaconnecter.NodeList;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.widget.TextView;
import android.widget.Toast;

public class RadarView extends View{
	private boolean view_flag = false;
	private int centerX=0;
	private int centerY=0;
	private double direction;
	private Canvas canv;
	private float state = 1f;
	private float rs = 1f;
	private float sx = 2f,sy = 2f;
	private int currentX;
	private int currentY;
	
	
	private GestureDetector gesDetect;
	private RadarTouch RT;
	
	private List<drawNode> dn = new ArrayList<RadarView.drawNode>();

	
	public RadarView(Context context,AttributeSet attr) {
		super(context,attr);
		// TODO 自動生成されたコンストラクター・スタブ
		//_gestureDetector = new ScaleGestureDetector(context, _simpleListener);
	}
	
	protected void onSizeChanged(int w,int h,int oldw,int oldh){
		super.onSizeChanged(w,h,oldw,oldh);
		//画面中央座標算出
		centerX=w/2;
		centerY=h/2;
	}
	
	//ピンチ操作関係
/*	private SimpleOnScaleGestureListener _simpleListener
		= new ScaleGestureDetector.SimpleOnScaleGestureListener(){
		public boolean onScaleBigin(ScaleGestureDetector detector){
			
			return super.onScaleBegin(detector);
		}
		public void onScaleEnd(ScaleGestureDetector detector) {
			super.onScaleEnd(detector);
		}

		public boolean onScale(ScaleGestureDetector detector) {
			state = detector.getScaleFactor();
			sx = detector.getFocusX();
			sy = detector.getFocusY();
			if(state > 1){
				rs += 0.03f;
			}else{
				if(rs > 1f){
					rs -= 0.03f;
				}
			}
			return true;
		};
	};*/
	/*
	@Override
    public boolean onTouchEvent(MotionEvent ev) {
		int x = (int) ev.getRawX();
		int y = (int) ev.getRawY();
		
		if (ev.getAction() == MotionEvent.ACTION_MOVE) {
			int diffX = x - offsetX;
			int diffY = y - offsetY;
			
			currentX = currentX + diffX;
			currentY = currentY + diffY;
			
			offsetX = x;
			offsetY = y;
		}else if(ev.getAction() == MotionEvent.ACTION_DOWN){
			offsetX = x;
			offsetY = y;
		}
		String str = new String();
		str = "currentX = " + currentX + ", currentY = " + currentY;
		text.setText(str);
		

    	/*
		// 触る
	    if (ev.getAction() == MotionEvent.ACTION_DOWN) {
	    	if(tx == 0f && ty == 0f){
	    		tx = ev.getX();
		    	ty = ev.getY();
	    	}else{
	    		tx = ttx;
	    		ty = tty;
	    	}
	    }
	    // 触ったままスライド
	    else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
	    	ttx = tx - ev.getX();
			tty = ty - ev.getY();
			ttx = ttx / 10;
			tty = tty / 10;
	    }
	    // 離す
	    else if (ev.getAction() == MotionEvent.ACTION_UP) {
	    }
	    String str = new String();
	  	str = "[ttx = " + ttx + "],[tty = " + tty + "][tx = " + tx + "],[ty = " + ty + "]";
	  	text.setText(str);
		_gestureDetector.onTouchEvent(ev);
		
        return true;
    }
    */


	@Override
	protected void onDraw(Canvas canvas){
		super.onDraw(canvas);
		canv = canvas;
		
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		
		canv.scale(rs, rs, sx, sy);
		canv.translate(currentX/2, currentY/2);
		
		
		//中心点
		paint.setAntiAlias(true);
        paint.setColor(Color.BLUE);
        canv.drawCircle(centerX, centerY, 10, paint);
        
        //グリッドを描画
        paint.setStyle(Style.STROKE);
        paint.setColor(Color.GREEN);
        for(int i = 80;i/80 < 10;i+=80){
        	canv.drawCircle(centerX, centerY, i, paint);
        }
        
        //描画許可がある場合のみ描画
        if(view_flag == true){
			//各ノードの打点
        	try{
        		for (Node data : NodeList.nodelist) {
    				drawCoordinate(canv, paint, data);
    			}
        	}catch (java.util.ConcurrentModificationException e){
        		
        	}
		}
		
	}
	
	private void drawCoordinate(Canvas canvas,Paint paint,Node data){
		int x;
		int y;
		double rad = 0;
		
		
		rad = (data.getAzim() - direction) / 180 * Math.PI;
			
		x = (int)(Math.cos(rad) * data.getDist());
		y = (int)(Math.sin(rad) * data.getDist());
		paint.setTextSize(20);
		paint.setColor(Color.RED);
		canvas.drawText(data.getName(),x + centerX , y + centerY, paint);
		Bitmap bitmap = data.getRadarIcon();
		if(bitmap != null){
			canvas.drawBitmap(bitmap, x + centerX, y + centerY, paint);
		}
		dn.add(new drawNode(data, x+ centerX, y + centerY));
		
	}

	public void drawScreen(float[] sensorVal,boolean flag,RadarTouch rt){
		//this.text = text;
		view_flag = flag;
		direction = (sensorVal[0] + 0) % 360;
		this.RT = rt;
		dn.clear();
		invalidate();
		
	}
	
	public class drawNode{
		public Node node;
		public int x;
		public int y;
		
		public drawNode(Node n,int x,int y) {
			// TODO 自動生成されたコンストラクター・スタブ
			this.node = n;
			this.x = x;
			this.y = y;
			
		}
	}
	
	//ここから下タッチ操作系
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int range = 80;
		int x = (int)event.getX();
		int y = (int)event.getY();

		Log.d("Radar","x=" + x + " y=" + y + " dn size =" + dn.size());
		
		if(event.getAction() == MotionEvent.ACTION_UP){
			for (drawNode tdn : dn) {
				Log.d("Radar",tdn.node.getName() + ":" + tdn.x + ":" + tdn.y);
				if(	tdn.x > x-range &&
					tdn.x < x+10 &&
					tdn.y > y-range &&
					tdn.y < y+10){
					Log.d("RadarTouch",tdn.node.getName() + ": SUCCESS" + event.getAction());
					//RT.onRadarTouchNodeEvent(tdn.node.getName() + ": SUCCESS");
					RT.onRadarTouchNodeEvent(tdn.node);
					break;
				}
			}
		}	
		return true;
	}
	
}
