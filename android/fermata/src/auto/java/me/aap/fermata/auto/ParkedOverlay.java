package me.aap.fermata.auto;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import me.aap.fermata.parked.ParkedGuard;

/**
 * BMW Tube: an opaque layer above the whole car UI. Visible unless ParkedGuard confirms the car
 * is standing still. It hides the picture only; playback and audio keep running underneath.
 */
public class ParkedOverlay extends FrameLayout implements ParkedGuard.Listener {
	private final TextView text;

	public ParkedOverlay(Context ctx) {
		super(ctx);
		setBackgroundColor(Color.BLACK);
		setClickable(true);
		setFocusable(true);

		text = new TextView(ctx);
		text.setTextColor(Color.WHITE);
		text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
		text.setGravity(Gravity.CENTER);
		text.setLineSpacing(0, 1.3f);
		int pad = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32,
				ctx.getResources().getDisplayMetrics());
		text.setPadding(pad, pad, pad, pad);
		addView(text, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
				Gravity.CENTER));
		apply(ParkedGuard.State.WAITING_GPS);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		ParkedGuard.get(getContext()).addListener(this);
	}

	@Override
	protected void onDetachedFromWindow() {
		ParkedGuard.get(getContext()).removeListener(this);
		super.onDetachedFromWindow();
	}

	@Override
	public void onParkedStateChanged(@NonNull ParkedGuard.State state) {
		apply(state);
	}

	private void apply(ParkedGuard.State state) {
		switch (state) {
			case PARKED -> {
				setVisibility(GONE);
				return;
			}
			case MOVING -> text.setText(
					"Video tạm ẩn khi xe đang chạy\nTiếng vẫn phát bình thường\n\nHình sẽ hiện lại khi xe dừng hẳn");
			case WAITING_GPS -> text.setText(
					"Đang chờ tín hiệu GPS…\nVideo sẽ hiện khi xác nhận xe đã dừng");
			case NO_PERMISSION -> text.setText(
					"Chưa có quyền Vị trí\nMở BMW Tube trên điện thoại và cấp quyền Vị trí chính xác");
		}
		setVisibility(VISIBLE);
		bringToFront();
	}

	// While visible, swallow touch and controller input so the hidden UI cannot be operated.
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		return getVisibility() == VISIBLE;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent ev) {
		return getVisibility() == VISIBLE || super.dispatchKeyEvent(ev);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return getVisibility() == VISIBLE;
	}
}
