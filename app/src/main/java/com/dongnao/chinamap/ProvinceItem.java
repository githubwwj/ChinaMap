package com.dongnao.chinamap;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.support.v4.content.ContextCompat;

/**
 * 一条省份数据
 */
public class ProvinceItem {

    private final Context mContext;
    /**
     * 有路线在手,说走就走
     */
    private Path mPath;

    /**
     * 绘制颜色
     */
    private int mDrawColor;

    /**
     * 绘制的路径和颜色
     *
     * @param path
     * @param color
     */
    public ProvinceItem(Path path, int color, Context context) {
        this.mPath = path;
        this.mDrawColor = color;
        this.mContext = context;
    }

    /**
     * 绘制某一个路径
     *
     * @param canvas
     * @param paint
     * @param isSelect 是否选中
     */
    void drawItem(Canvas canvas, Paint paint, boolean isSelect) {
        if (isSelect) {
            //清除阴影层
            paint.clearShadowLayer();

            //设置边界
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2);
            paint.setColor(Color.MAGENTA);
            paint.setShadowLayer(6, 0, 0, Color.BLUE);
//            radius:模糊半径，radius越大越模糊，越小越清晰，但是如果radius设置为0，则阴影消失不见
//            dx:阴影的横向偏移距离，正值向右偏移，负值向左偏移
//            dy:阴影的纵向偏移距离，正值向下偏移，负值向上偏移
//            color: 绘制阴影的画笔颜色，即阴影的颜色（对图片阴影无效）

            canvas.drawPath(mPath, paint);

            //选中时，绘制描边效果
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(mDrawColor);
            paint.setStrokeWidth(1);
            canvas.drawPath(mPath, paint);
        } else {
            //设置边界
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(1);
            paint.setColor(Color.BLACK);
            paint.setShadowLayer(8, 0, 0, Color.WHITE);
            canvas.drawPath(mPath, paint);

            //后面是填充
            paint.clearShadowLayer();
            paint.setColor(mDrawColor);
            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeWidth(2);
            canvas.drawPath(mPath, paint);
        }
    }

    public void setDrawColor(int mDrawColor) {
        this.mDrawColor = mDrawColor;
    }

    /**
     * true 包含点击的x y 左边
     *
     * @param x 坐标
     * @param y 坐标
     * @return
     */
    public boolean isTouch(float x, float y) {
        RectF rectF = new RectF();
        /**
         * 获取路径的举行
         */
        mPath.computeBounds(rectF, true);
//        rectF   矩形  包含了Path
        Region region = new Region();

        //设置路径范围
        region.setPath(mPath, new Region((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));
        //x y 是否在这个坐标中
        return region.contains((int) x, (int) y);
    }
}
