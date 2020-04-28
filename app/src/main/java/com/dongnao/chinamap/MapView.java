package com.dongnao.chinamap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class MapView extends View {

    private Context mContext;
    /**
     * 地图的默认缩放比例
     */
    private float mScale = 1.0f;

    /**
     * 地图的大小
     */
    private RectF mMapRect = new RectF();

    /**
     * 省份集合
     */
    private List<ProvinceItem> mProvinceItemList;

    private Paint mPaint;

    /**
     * 手指点击后选中某个省份
     */
    private ProvinceItem mSelectProvinceItem;

    public MapView(Context context) {
        this(context, null);
    }

    public MapView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MapView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        loadPathData();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        handleTouch(event.getX(), event.getY());
        return super.onTouchEvent(event);
    }

    /**
     * 点击某个省份进行绘制
     *
     * @param x 点击x坐标
     * @param y 点击y坐标
     */
    private void handleTouch(float x, float y) {
        if (mProvinceItemList == null) {
            return;
        }
        for (ProvinceItem provinceItem : mProvinceItemList) {
            if (provinceItem.isTouch(x / mScale, y / mScale)) {
                mSelectProvinceItem = provinceItem;
                postInvalidate();
                break;
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        map 的宽度  和高度
        if (mMapRect != null) {
            double mapWidth = mMapRect.width();

            //控件宽度
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = MeasureSpec.getSize(heightMeasureSpec);

            int finalWidth = Math.min(width, height);

            //计算控件宽度和地图宽度的比例  比如
            //以屏幕宽度为主
            mScale = (float) (finalWidth / mapWidth);
            Log.d("tag", "-------scale=" + mScale);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mProvinceItemList != null) {
            canvas.save();
            canvas.scale(mScale, mScale);
            for (ProvinceItem provinceItem : mProvinceItemList) {
                if (provinceItem != this.mSelectProvinceItem) {
                    provinceItem.drawItem(canvas, mPaint, false);
                } else {
                    provinceItem.drawItem(canvas, mPaint, true);
                }
            }
        }
    }

    /**
     * 解析svg 矢量图数据
     */
    private void loadPathData() {
        //获取矢量图输入流
        InputStream inputStream = mContext.getResources().openRawResource(R.raw.china);
        List<ProvinceItem> list = new ArrayList<>();
        try {
            //文档施工队工厂
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            //文档施工队的某位师傅
            DocumentBuilder builder = factory.newDocumentBuilder();
            //w3c 的Document
            Document document = builder.parse(inputStream);

            //文档中的根元素
            Element rootElement = document.getDocumentElement();

            //获取名称为path 的节点列表
            NodeList items = rootElement.getElementsByTagName("path");
//                中国地图的  矩形
            float left = -1;
            float right = -1;
            float top = -1;
            float bottom = -1;
            RectF rect = new RectF();
            for (int i = 0; i < items.getLength(); i++) {
                //获取一个节点
                Element element = (Element) items.item(i);

                //通过节点中的值
                String pathData = element.getAttribute("android:pathData");

                //地图路径
                Path path = PathParser.createPathFromPathData(pathData);

                //要绘制的地图颜色
                int color;
                int flag = i % 4;
                switch (flag) {
                    case 1:
                        color = ContextCompat.getColor(mContext, R.color.deep_sky_blue);
                        break;
                    case 2:
                        color = ContextCompat.getColor(mContext, R.color.cerulean_blue);
                        break;
                    case 3:
                        color = ContextCompat.getColor(mContext, R.color.light_sky_blue);
                        break;
                    default:
                        color = ContextCompat.getColor(mContext, R.color.cyan);
                        break;
                }

                ProvinceItem provinceItem = new ProvinceItem(path, color,mContext);
                list.add(provinceItem);

                //获取宽高
                path.computeBounds(rect, true);

                //获取地图大小,也就是地图的边界
                left = left == -1 ? rect.left : Math.min(left, rect.left);
                right = right == -1 ? rect.right : Math.max(right, rect.right);
                top = top == -1 ? rect.top : Math.min(top, rect.top);
                bottom = bottom == -1 ? rect.bottom : Math.max(bottom, rect.bottom);
                mMapRect.set(left, top, right, bottom);
            }
            mProvinceItemList = list;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
