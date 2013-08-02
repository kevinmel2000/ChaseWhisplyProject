package fr.tvbarthel.games.chasewhisply.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import fr.tvbarthel.games.chasewhisply.R;
import fr.tvbarthel.games.chasewhisply.mechanics.GameInformation;
import fr.tvbarthel.games.chasewhisply.model.DisplayableItem;
import fr.tvbarthel.games.chasewhisply.model.DisplayableItemFactory;
import fr.tvbarthel.games.chasewhisply.model.TargetableItem;

public class GameView extends View {

	private final Bitmap mCrossHairs;
	private final Bitmap mGhostBitmap;
	private final Bitmap mGhostTargetedBitmap;
	private final Bitmap mAmmoBitmap;
	private final Bitmap mBulletHoleBitmap;
	private final String mComboString;
	private final String mScoreString;
	private final String mTimeString;
	private final Paint mPaint = new Paint();
	private final Rect mBounds = new Rect();
	private GameInformation mModel;
	//ratio for displaying items
	private float mWidthRatioDegreeToPx;
	private float mHeightRatioDegreeToPx;
	private int mFontSize;
	private int mScreenWidth;
	private int mScreenHeight;


	public GameView(Context context, GameInformation model) {
		super(context);
		mModel = model;

		final Resources res = getResources();

		//initialize bitmap drawn after
		mCrossHairs = BitmapFactory.decodeResource(res, R.drawable.crosshair_white);
		mGhostBitmap = BitmapFactory.decodeResource(res, R.drawable.ghost);
		mGhostTargetedBitmap = BitmapFactory.decodeResource(res, R.drawable.ghost_targeted);
		mAmmoBitmap = BitmapFactory.decodeResource(res, R.drawable.ammo);
		mBulletHoleBitmap = BitmapFactory.decodeResource(res, R.drawable.bullethole);

		mComboString = res.getString(R.string.in_game_combo_counter);
		mScoreString = res.getString(R.string.in_game_score);
		mTimeString = res.getString(R.string.in_game_time);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		mScreenWidth = getWidth();
		mScreenHeight = getHeight();

		//initialize ratio degree / screen px
		mWidthRatioDegreeToPx = mScreenWidth / mModel.getSceneWidth();
		mHeightRatioDegreeToPx = mScreenHeight / mModel.getSceneHeight();

		mFontSize = mScreenWidth / 30;

		drawDisplayableItems(canvas);
		drawCrossHair(canvas);
		drawAmmo(canvas);
		drawRemainingTime(canvas);
		drawCombo(canvas);
		drawScore(canvas);
	}

	private void drawCrossHair(Canvas canvas) {
		canvas.drawBitmap(mCrossHairs, (float) (mScreenWidth - mCrossHairs.getWidth()) / 2,
				(float) (mScreenHeight - mCrossHairs.getHeight()) / 2, mPaint);
	}

	/**
	 * draw ammo on player screen
	 *
	 * @param canvas canvas from View.onDraw method
	 */
	private void drawAmmo(Canvas canvas) {
		final int currentAmmunition = mModel.getWeapon().getCurrentAmmunition();
		canvas.drawBitmap(mAmmoBitmap, (float) (mScreenWidth - mAmmoBitmap.getWidth() - 10),
				(float) (getHeight() - mAmmoBitmap.getHeight()), mPaint);
		mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		mPaint.setColor(Color.WHITE);
		mPaint.setStrokeWidth(4);
		mPaint.setTextSize(mAmmoBitmap.getHeight() / 2);
		canvas.drawText(String.valueOf(currentAmmunition)
				, mScreenWidth - mAmmoBitmap.getWidth() - mPaint.getTextSize()
				, mScreenHeight - (mAmmoBitmap.getHeight() / 4)
				, mPaint);

		if (currentAmmunition == 0) {
			final String noAmmoMessage = getResources().getString(R.string.in_game_no_ammo_message);
			mPaint.setTextSize(mFontSize);
			mPaint.getTextBounds(noAmmoMessage, 0, noAmmoMessage.length(), mBounds);
			mPaint.setColor(Color.RED);
			mPaint.setStrokeWidth(2);
			canvas.drawText(noAmmoMessage,
					(mScreenWidth - mBounds.width()) / 2,
					(mScreenHeight - mCrossHairs.getHeight()) / 2 - 10,
					mPaint);
		}
	}

	/**
	 * draw remaining time
	 *
	 * @param canvas canvas from View.onDraw method
	 */
	private void drawRemainingTime(Canvas canvas) {
		final long millis = mModel.getRemainingTime();
		final int ss = (int) (millis / 1000);
		mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		if (ss > 10) {
			mPaint.setColor(Color.WHITE);
		} else {
			mPaint.setColor(Color.RED);
		}
		mPaint.setStrokeWidth(4);
		mPaint.setTextSize(mFontSize);
		canvas.drawText(String.format(mTimeString, ss)
				, 10
				, 10 + mScreenWidth / 20
				, mPaint);
	}

	/**
	 * draw combo counter
	 *
	 * @param canvas canvas from View.onDraw method
	 */
	private void drawCombo(Canvas canvas) {
		final int comboNumber = mModel.getCurrentCombo();
		if (comboNumber > 1) {
			mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			mPaint.setColor(Color.WHITE);
			mPaint.setStrokeWidth(4);
			mPaint.setTextSize(mFontSize);
			canvas.drawText(String.format(mComboString, mModel.getCurrentCombo())
					, mScreenWidth / 2 + mCrossHairs.getWidth() / 2
					, mScreenHeight / 2 + mCrossHairs.getHeight() / 2
					, mPaint);
		}
	}

	/**
	 * draw score
	 *
	 * @param canvas canvas from View.onDraw method
	 */
	private void drawScore(Canvas canvas) {
		mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		mPaint.setColor(Color.WHITE);
		mPaint.setStrokeWidth(4);
		mPaint.setTextSize(mFontSize);
		canvas.drawText(String.format(mScoreString, mModel.getCurrentScore())
				, 10
				, mScreenHeight - mPaint.getTextSize()
				, mPaint);

	}

	/**
	 * draw active items on the screen
	 *
	 * @param canvas canvas from View.onDraw method
	 */
	private void drawDisplayableItems(Canvas canvas) {
		final float[] currentPos = mModel.getCurrentPosition();
		currentPos[0] *= mWidthRatioDegreeToPx;
		currentPos[1] *= mHeightRatioDegreeToPx;
		for (DisplayableItem i : mModel.getItemsForDisplay()) {
			switch (i.getType()) {
				case DisplayableItemFactory.TYPE_EASY_GHOST:
					if (!((TargetableItem) i).isAlive()) {
						//Ghost dead
					} else {
						//Ghost alive
						if (isTargeted(currentPos, i, mGhostBitmap)) {
							//Ghost alive and targeted
							renderItem(canvas, mGhostTargetedBitmap, i, mGhostTargetedBitmap.getWidth(), mGhostTargetedBitmap.getHeight());
							mModel.setCurrentTarget((TargetableItem) i);
						} else {
							//Ghost alive and not targeted
							renderItem(canvas, mGhostBitmap, i, mGhostBitmap.getWidth(), mGhostBitmap.getHeight());
							if (i == mModel.getCurrentTarget()) {
								mModel.removeTarget();
							}
						}
					}
					break;
				case DisplayableItemFactory.TYPE_BULLET_HOLE:
					renderItem(canvas, mBulletHoleBitmap, i, mBulletHoleBitmap.getWidth(), mBulletHoleBitmap.getHeight());
					break;
			}
		}
	}

	/**
	 * used to know if a targetable item is targeted
	 *
	 * @param crosshairPosition current crosshair x and y
	 * @param t                 targetable item
	 * @param targetableRes     bitmap used to draw this targetable
	 * @return true if targeted
	 */
	private boolean isTargeted(float[] crosshairPosition, DisplayableItem t, Bitmap targetableRes) {
		final int xInPx = (int) (t.getX() * mWidthRatioDegreeToPx);
		final int yInPx = (int) (t.getY() * mHeightRatioDegreeToPx);
		if (crosshairPosition[0] > xInPx - targetableRes.getWidth() / 2 & crosshairPosition[0] < xInPx + targetableRes.getWidth() / 2
				& crosshairPosition[1] > yInPx - targetableRes.getHeight() / 2 & crosshairPosition[1] < yInPx + targetableRes.getHeight() / 2) {
			return true;
		} else {
			return false;
		}
	}

	public void renderItem(final Canvas canvas, final Bitmap bitmap, final DisplayableItem item, final int mWidth, final int mHeight) {
		final float[] currentPosInDegree = mModel.getCurrentPosition();
		//normalization, avoid negative value.
		currentPosInDegree[0] += 180;
		currentPosInDegree[1] += 180;

		final float[] itemPosInDegree = new float[]{item.getX() + 180, item.getY() + 180};

		final float windowXInPx = currentPosInDegree[0] * mWidthRatioDegreeToPx - mScreenWidth / 2;
		final float windowYInPx = currentPosInDegree[1] * mHeightRatioDegreeToPx - mScreenHeight / 2;

		float itemXInPx = itemPosInDegree[0];
		float itemYInPx = itemPosInDegree[1];

		float diffX = currentPosInDegree[0] - itemPosInDegree[0];
		float diffY = currentPosInDegree[1] - itemPosInDegree[1];

		float distX = Math.abs(diffX);
		float distY = Math.abs(diffY);

		if (distX > 360 - distX) {
			itemXInPx = currentPosInDegree[0] - diffX + Math.signum(diffX) * 360;
		}

		if (distY > 360 - distY) {
			itemYInPx = currentPosInDegree[1] - diffY + Math.signum(diffY) * 360;
		}

		itemXInPx = itemXInPx * mWidthRatioDegreeToPx - mWidth / 2;
		itemYInPx = itemYInPx * mHeightRatioDegreeToPx - mHeight / 2;

		final float borderLeft = windowXInPx - mWidth;
		final float borderTop = windowYInPx - mHeight;
		final float borderRight = borderLeft + mScreenWidth + mWidth;
		final float borderBottom = borderTop + mScreenHeight + mHeight;

		if (itemXInPx > borderLeft && itemXInPx < borderRight && itemYInPx < borderBottom && itemYInPx > borderTop) {
			canvas.drawBitmap(bitmap, itemXInPx - windowXInPx, itemYInPx - windowYInPx, mPaint);
		}
	}
}
