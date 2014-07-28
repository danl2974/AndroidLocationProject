package com.dl2974.whatsaround;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ShapeDrawable;
import android.view.View;

public class MapInfoWindow extends View  {
	
	
    public MapInfoWindow(Context context)
    {
        super(context);
    }
	
	@Override
    public void onDraw(Canvas canvas)
    {
		/*
        RectF space = new RectF(this.getLeft(), this.getTop(), this.getRight(), this.getBottom());

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        paint.setShader(new LinearGradient(0, getWidth(), 0, 0, Color.BLACK, Color.WHITE, Shader.TileMode.MIRROR));

        canvas.drawArc(space, 180, 360, true, paint);

        Rect rect = new Rect(this.getLeft(),this.getTop() + (this.getHeight() / 2),this.getRight(),this.getBottom());
        canvas.drawRect(rect, paint);
        */
    }


}
