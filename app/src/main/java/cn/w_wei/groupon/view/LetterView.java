package cn.w_wei.groupon.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import cn.w_wei.groupon.R;

/**
 * Created by w_wei on 2019.04.23
 * 触摸时，这个事件最初传递给CityActivity，
 * 但它不处理，又继续传递给所含布局的根节点
 * 再由根节点传递给该View所属的FrameLayout，
 * 再看该View是否处理，如果不处理则传给RecyclerView
 * 进行城市分组的快速导航
 */
public class LetterView extends View {
    private int color;
    private Context context;
    private OnTouchLetterListener listener;

    public void setOnTouchLetterListener(OnTouchLetterListener listener){
        this.listener = listener;
    }

    public LetterView(Context context) {
        super(context);
    }

    //一下三个构造器，在有布局文件时，安卓系统会调用
    public LetterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
//        color = attrs.getAttributeIntValue("http://schemas.android.com/apk/res-auto","lv_color",Color.GRAY);
        //专门读写在布局文件中的自定义属性的类TypedArray，attrs存的内容包含该控件所有属性，
        //所以第二个参数为限定参数，限定为该控件自定义属性
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.LetterView);
        color = array.getColor(R.styleable.LetterView_lv_color, Color.GRAY);
        Log.i("TAG","R.styleable.LetterView_lv_color-->"+ color + ";"+Color.GRAY);
        array.recycle();
    }

    /**
     * 完成画笔初始化,画笔中的参数的作用是抗锯齿
     * 计算机图形绘制的三种基本元素：点，线，三角形
     */
    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //用来设置使用这个画笔绘制出来的文字的大小，
        //里面的参数如果是数字，则单位是px
        //第一个参数为需要的结果的单位
        float size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,14,getResources().getDisplayMetrics());
        paint.setTextSize(size);
    }

    /**
     *
     * @param context
     * @param attrs
     * @param defStyleAttr 默认的样式属性
     */
    public LetterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 先调用这个方法，用来设定LetterView尺寸（宽高）
     * 不一定需要重写，View的onMeasure已经有了很多的设定尺寸的代码，可以使用
     * 当View的onMeasure设定尺寸的代码逻辑不能满足实际要求时，才有必要进行改写
     * 默认情况下，自写View无法区别wrap_content,所以要想识别wrap_content属性，就要
     * 改写方法以识别wrap_content，如果不该写，出来的是match_parent效果
     * @param widthMeasureSpec 控件的宽度，它包含了参数的类型是wrap_content，match_parent还是具体的数值
     *      int是32未，它用后两位来区分是wrap_content还是match_parent，前30为存储了具体的数值
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //改写时一定要保留View的onMeasure方法调用
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //进行针对wrap_content的改写
        int mode = MeasureSpec.getMode(widthMeasureSpec);//得到所含数据类型
        if(mode == MeasureSpec.AT_MOST){//用于描述wrap_content
            int lPadding = getPaddingLeft();
            int rPadding = getPaddingRight();
            int contentWidth = 0;
            for(int i = 0;i < letters.length;i++){
                String letter = letters[i];
                Rect bounds = new Rect();
                paint.getTextBounds(letter,0,letter.length(),bounds);
                if(bounds.width() > contentWidth){
                    contentWidth = bounds.width();
                }
            }
            //左边距+内容最大宽度+右边距=控件应有的宽度
            int size = lPadding + contentWidth + rPadding;
            //MeasureSpec.getSize(heightMeasureSpec)得到所含数据大小
            setMeasuredDimension(size,MeasureSpec.getSize(heightMeasureSpec));
        }
    }

    /**
     * 其次调用这个方法，用来控制其它子控件在本View中的摆放顺序，本次不用重写该方法
     * @param changed
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    private String[] letters = {"热门","A","B","C","D","E","F","G",
            "H","I","J","K","L","M","N",
            "O","P","Q","R","S","T",
            "U","V","W","X","Y","Z"};
    private Paint paint;
    private RecyclerView rvPosition;
    private int position = 0;

    /**
     * 最后调用这个方法，这个自定义控件如何出现在布局中，一定要重写
     * x,y是接近文字左下角的坐标（不是真正坐标）
     * @param canvas 相当于画布，可以把其上内容显示在屏幕上
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();//获取视图LetterView整体的宽度
        int height = getHeight();//获取视图LetterView整体的高度
        if(position > 0 && position < letters.length){
            int m = width/2;
            int n = height/letters.length/2 + position * height/letters.length;
            int p = height/letters.length/2;
            int radius = (m+p)/2;
//            paint.setColor(Color.parseColor("#E6E3E3"));
            paint.setColor(color);
            canvas.drawCircle(m,n,radius,paint);
        }
        paint.setColor(Color.BLACK);
        for(int i = 0;i < letters.length;i++) {
            String letter = letters[i];

            Rect bounds = new Rect();//长方形缩写，新建边界信息
            paint.getTextBounds(letter,0,letter.length(),bounds);//这行语句可以拿到字母所占空间的边界信息
//            bounds.width()/bounds.height()

            float x = width/2 - bounds.width()/2;//分配给letter小空间的宽度的一半减去letter宽度的一半
            float y = height/letters.length/2 + bounds.height()/2 + i*height/letters.length;//分配给letter小空间的高度的一半加上letter高度的一半
            canvas.drawText(letter, x, y, paint);
        }
    }

    /**
     * 是否处理触摸事件
     * @param event 触摸事件会被封装成MotionEvent对象
     * @return true表示处理
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //按下，移动，还是抬起
        int action = event.getAction();
        switch (action){
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                //改变背景
//                setBackgroundColor(color);
//                setBackgroundColor(Color.TRANSPARENT);//设置背景是透明的
                //1.发广播，只能在安卓中用 2.通过setXXX()方法把RecyclerView对象穿递过来，简单但是扩展性差 3.定义接口
                if(listener != null) {
                    //手指按下或移动时距离LetterView顶部的距离
                    float y = event.getY();
                    //根据距离换算文字的下标值
                    int index = (int) ((y * letters.length)/getHeight());
                    position = index;
                    invalidate();
                    if(index >= 0 && index < letters.length){
                        String letter = letters[index];
                        listener.onTouchLetter(this, letter);
                        Intent intent = new Intent("letter-view");
                        intent.putExtra("letter","触摸到的字母-->"+letter);
                        context.sendBroadcast(intent);
                    }
                }
                break;
            default:
                position = 0;
                invalidate();
//                setBackgroundColor(Color.TRANSPARENT);//设置背景是透明的
                break;
        }
        return true;
    }

    public interface OnTouchLetterListener{
        void onTouchLetter(LetterView view,String letter);
    }
}
