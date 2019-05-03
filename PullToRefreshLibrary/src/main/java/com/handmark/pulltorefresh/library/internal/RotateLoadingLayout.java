/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.handmark.pulltorefresh.library.internal;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView.ScaleType;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Orientation;
import com.handmark.pulltorefresh.library.R;

public class RotateLoadingLayout extends LoadingLayout {

	int[] resIds = new int[]{
			R.drawable.dropdown_anim_00,
			R.drawable.dropdown_anim_01,
			R.drawable.dropdown_anim_02,
			R.drawable.dropdown_anim_03,
			R.drawable.dropdown_anim_04,
			R.drawable.dropdown_anim_05,
			R.drawable.dropdown_anim_06,
			R.drawable.dropdown_anim_07,
			R.drawable.dropdown_anim_08,
			R.drawable.dropdown_anim_09,
			R.drawable.dropdown_anim_10
	};

//	static final int ROTATION_ANIMATION_DURATION = 1200;
//
//	private final Animation mRotateAnimation;
//	private final Matrix mHeaderImageMatrix;
//	//旋转中心点
//	private float mRotationPivotX, mRotationPivotY;
//	//判断下拉时处于什么状态
//	private final boolean mRotateDrawableWhilePulling;

	/**
	 * 如果声明了自有属性，则在构造器中完成属性的初始化
	 * @param context
	 * @param mode
	 * @param scrollDirection
	 * @param attrs
	 */
	public RotateLoadingLayout(Context context, Mode mode, Orientation scrollDirection, TypedArray attrs) {
		super(context, mode, scrollDirection, attrs);

//		mRotateDrawableWhilePulling = attrs.getBoolean(R.styleable.PullToRefresh_ptrRotateDrawableWhilePulling, true);
//
//		mHeaderImage.setScaleType(ScaleType.MATRIX);
//		mHeaderImageMatrix = new Matrix();
//		mHeaderImage.setImageMatrix(mHeaderImageMatrix);
//
//		mRotateAnimation = new RotateAnimation(0, 720, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
//				0.5f);
//		mRotateAnimation.setInterpolator(ANIMATION_INTERPOLATOR);
//		mRotateAnimation.setDuration(ROTATION_ANIMATION_DURATION);
//		mRotateAnimation.setRepeatCount(Animation.INFINITE);
//		mRotateAnimation.setRepeatMode(Animation.RESTART);
	}

	/**
	 * 原生：对自由属性中旋转动画的中心点进行赋值
	 * 现在：NO-Op
	 * @param imageDrawable
	 */
	public void onLoadingDrawableSet(Drawable imageDrawable) {
//		if (null != imageDrawable) {
//			mRotationPivotX = Math.round(imageDrawable.getIntrinsicWidth() / 2f);
//			mRotationPivotY = Math.round(imageDrawable.getIntrinsicHeight() / 2f);
//		}
	}

	/**
	 * 回调方法，只要处于下拉动作，该方法就会被不断地触发
	 * 原生：随着下拉的距离，计算一个应该旋转的角度
	 * 利用Matrix计算旋转该角度时，ImageView每个角的坐标
	 * 现在：随着下拉的距离，不断地切换图片
	 * @param scaleOfLayout 下拉距离用该参数表示0~无限大的一个小数
	 */
	protected void onPullImpl(float scaleOfLayout) {
		//根据scaleOfLayout来切换图片
		int index = (int) (scaleOfLayout*10);
		if(index <= 10){
			//根据原始图片的大小创建一个缩小的图片,第二，三个参数为缩放至目的宽高，第四个参数为缩放时是否保持图片质量
			int id = resIds[index];
			Bitmap src = BitmapFactory.decodeResource(getResources(),id);
			//----------------方法一----------------
//			int width = (int) (src.getWidth() * (index + 1) / 11f);
//			int height = (int) (src.getHeight() * (index + 1) / 11f);
			//图像抖动是因为int类型损失了精度
//			src = Bitmap.createScaledBitmap(src,width,height,true);//可以对图片进行缩放
			mHeaderImage.setImageBitmap(src);
			//-------------------方法二-----------------
			float scale = (scaleOfLayout * 10 + 1) / 11f;
			mHeaderImage.setScaleX(scale);
			mHeaderImage.setScaleY(scale);
			//利用原始图片创建一个ScaleDrawble图片
			//---------------------方法三--------------------
//			Drawable drawable = getResources().getDrawable(id);
//			drawable.setLevel(100);//设置原长宽为100，放在这里在eclipse中也可以出效果
			//第二个参数为缩放中心点，后俩个个参数为缩放倍数
//			ScaleDrawable sd = new ScaleDrawable(drawable, Gravity.CENTER,0.5f,0.5f);
//			drawable.setLevel(100);//设置原长宽为100,放在这里可以出效果

//			mHeaderImage.setImageDrawable(sd);
		}else{
			int resId = resIds[10];
			mHeaderImage.setImageResource(resId);

		}
//		float angle;
//		if (mRotateDrawableWhilePulling) {
//			angle = scaleOfLayout * 90f;
//		} else {
//			angle = Math.max(0f, Math.min(180f, scaleOfLayout * 360f - 180f));
//		}
//
//		mHeaderImageMatrix.setRotate(angle, mRotationPivotX, mRotationPivotY);
//		mHeaderImage.setImageMatrix(mHeaderImageMatrix);
	}

	/**
	 * 回到方法
	 * 当下拉到一定位置松手后，进入刷新状态时被回调
	 * 原生：让ImageView播放一个无线旋转的补间动画
	 * 现在：让ImageView播放一个吃包子的帧动画
	 */
	@Override
	protected void refreshingImpl() {
		mHeaderImage.setImageResource(R.drawable.refreshing_anim);
		AnimationDrawable drawable = (AnimationDrawable) mHeaderImage.getDrawable();
		drawable.start();
//		mHeaderImage.startAnimation(mRotateAnimation);
	}

	/**
	 * 回调方法
	 * 当刷新完毕后，头部缩起时调用
	 * 当PullToRefreshListView的onRefreshComplete方法被调用时
	 * 原生：停止ImageView的旋转补间动画，重置Matrix
	 * 现在：没有必要让帧动画停下来
	 * NO-OP
	 */
	@Override
	protected void resetImpl() {
//		mHeaderImage.clearAnimation();
//		resetImageRotation();
	}

//	private void resetImageRotation() {
//		if (null != mHeaderImageMatrix) {
//			mHeaderImageMatrix.reset();
//			mHeaderImage.setImageMatrix(mHeaderImageMatrix);
//		}
//	}

	@Override
	protected void pullToRefreshImpl() {
		// NO-OP
	}

	@Override
	protected void releaseToRefreshImpl() {
		// NO-OP
	}

	/**
	 * 返回默认使用的图片
	 * 原生：双向箭头图片
	 * 现在：小孩吃包子的第一张图片
	 * @return
	 */
	@Override
	protected int getDefaultDrawableResId() {
//		return R.drawable.default_ptr_rotate;
		return R.drawable.dropdown_anim_00;
	}

}
