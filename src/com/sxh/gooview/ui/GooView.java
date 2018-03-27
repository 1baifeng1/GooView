package com.sxh.gooview.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class GooView extends View {
	
	private final float maxDistanceOfd2s = 100;
	private static final int STATE_DRAG_POINT_CONNECT = 0;
	private static final int STATE_DRAG_POINT_DISCONNECT = 1;
	private int stateDragPoint = STATE_DRAG_POINT_CONNECT;

	private Paint paint;
	private PointF dragPointF;
	private int dragPointFRadius;
	private PointF staticPointF;
	private int staticPointFRadius;
	private final int minStaticPointFRadius = 5;
	private PointF s2;
	private PointF s1;
	private PointF d2;
	private PointF d1;
	private PointF gooControlPointf;
	private Path gooPath;

	public GooView(Context context) {
		super(context);
		init();
	}

	public GooView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public GooView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init() {
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.RED);

		dragPointF = new PointF();
		dragPointF.x = 500f;
		dragPointF.y = 500f;
		dragPointFRadius = 50;

		staticPointF = new PointF();
		staticPointF.x = 500f;
		staticPointF.y = 500f;
		staticPointFRadius = 50;

		d1 = new PointF();
		// d1.x = dragPointF.x;
		// d1.y = dragPointF.y + dragPointFRadius;
		d2 = new PointF();
		// d2.y = dragPointF.y - dragPointFRadius;
		// d2.x = dragPointF.x;
		s1 = new PointF();
		// s1.x = staticPointF.x;
		// s1.y = staticPointF.y + staticPointFRadius;
		s2 = new PointF();
		// s2.y = staticPointF.y - staticPointFRadius;
		// s2.x = staticPointF.x;

		gooControlPointf = new PointF();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// ���ǻ�ȡλ���Ǵ���Ļ���Ͻ�Ϊ����ԭ���ȡ����ͼ����canvas���Ի������Ͻ�Ϊԭ��
		// 1���ȼ�ȥ״̬���ĸ߶�
		canvas.translate(0, -getStatusBarHeight(getResources()));
		// 2����ȥ�������ĸ߶�
		// int viewTop = getWindow().

		// ����Բ�������Ѿ������£���Ҫ���¼���d1��d2��s1��s2
		// б��
		float offsetX = staticPointF.x - dragPointF.x;
		float offsetY = staticPointF.y - dragPointF.y;
		if (offsetX != 0) {
			float ratioOfd2s = offsetY / offsetX; // б��
			double angleOfd2s = Math.atan(ratioOfd2s); // б��
			d1.x = dragPointF.x - (float) Math.sin(angleOfd2s)
					* dragPointFRadius;
			d1.y = dragPointF.y + (float) Math.cos(angleOfd2s)
					* dragPointFRadius;
			d2.x = dragPointF.x + (float) Math.sin(angleOfd2s)
					* dragPointFRadius;
			d2.y = dragPointF.y - (float) Math.cos(angleOfd2s)
					* dragPointFRadius;

			s1.x = staticPointF.x - (float) Math.sin(angleOfd2s)
					* staticPointFRadius;
			s1.y = staticPointF.y + (float) Math.cos(angleOfd2s)
					* staticPointFRadius;
			s2.x = staticPointF.x + (float) Math.sin(angleOfd2s)
					* staticPointFRadius;
			s2.y = staticPointF.y - (float) Math.cos(angleOfd2s)
					* staticPointFRadius;
		} else {
			// �������Բ��ֱ���У�û��б�ʣ�����ֱ����
			d1.set(dragPointF.x - dragPointFRadius, dragPointF.y);
			d2.set(dragPointF.x + dragPointFRadius, dragPointF.y);

			s1.set(staticPointF.x - staticPointFRadius, staticPointF.y);
			s2.set(staticPointF.x + staticPointFRadius, staticPointF.y);
		}

		canvas.drawCircle(dragPointF.x, dragPointF.y, dragPointFRadius, paint);
		canvas.drawCircle(staticPointF.x, staticPointF.y, staticPointFRadius,
				paint);

		gooControlPointf = calcGooControlPoint(staticPointF, dragPointF);
		gooPath = new Path(); // ÿ����Ҫ��������һ���µ�
		gooPath.moveTo(d2.x, d2.y);
		gooPath.quadTo(gooControlPointf.x, gooControlPointf.y, s2.x, s2.y);
		gooPath.lineTo(s1.x, s1.y);
		gooPath.quadTo(gooControlPointf.x, gooControlPointf.y, d1.x, d1.y);
		gooPath.close(); // Ĭ�ϻ�պϣ�ʣ�µ���һ��d1��d2��ֱ�ߣ����������Զ�ȥ�պ�

		canvas.drawPath(gooPath, paint);
	}

	/**
	 * ���㱴���������еĿ��Ƶ�
	 * 
	 * @param p1
	 *            ��ʼ��
	 * @param p2
	 *            �յ�
	 * @return ���Ƶ�
	 */
	private PointF calcGooControlPoint(PointF p1, PointF p2) {
		PointF pointF = new PointF();

		pointF.x = (p1.x + p2.x) / 2.0f;
		pointF.y = (p1.y + p2.y) / 2.0f;

		return pointF;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getRawX();
		float y = event.getRawY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			dragPointF.set(x, y);

			break;
		case MotionEvent.ACTION_MOVE:
			dragPointF.set(x, y);

			// ���ž����������Ҫ��СstaticԲ�İ뾶
			if (5 >= staticPointFRadius) {
				staticPointFRadius = 5;
			} else {
				staticPointFRadius = calcStaticPointFRadius();
			}
			

			break;
		case MotionEvent.ACTION_UP:

			break;

		default:
			break;
		}

		invalidate();
		return true;
	}

	/**
	 * �򵥴���һ��
	 * 
	 * @return
	 */
	private int calcStaticPointFRadius() {
		double x = Math.pow(dragPointF.x - staticPointF.x, 2);
		double y = Math.pow(dragPointF.y - staticPointF.y, 2);
		float distance = (float) Math.sqrt(x + y);
		
		float fraction = distance / maxDistanceOfd2s;

		return (int)(50 + (5 - 50) * fraction * 0.1f);
	}

	public static float getStatusBarHeight(Resources resources) {
		int status_bar_height_id = resources.getIdentifier("status_bar_height",
				"dimen", "android");
		return resources.getDimension(status_bar_height_id);
	}
}
