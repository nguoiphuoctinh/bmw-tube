package me.aap.fermata.parked;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * BMW Tube: decides whether video may be shown on the car screen.
 *
 * Video is shown only while the car is confirmed to be standing still. Speed comes from the
 * phone's GPS. Audio is never touched here; the car UI only hides the picture.
 *
 * Rules (fail-safe: anything unknown hides the video):
 *  - no location permission            -> NO_PERMISSION (hidden)
 *  - no GPS speed reading yet           -> WAITING_GPS   (hidden)
 *  - speed >= MOVING_KMH                -> MOVING        (hidden, immediately)
 *  - speed <= STOPPED_KMH for STOP_CONFIRM_MS -> PARKED (shown)
 *  - speed in between                   -> keep the current state (hysteresis against GPS jitter)
 *  - GPS goes quiet                     -> keep the last state (quiet after standing = parked,
 *                                          quiet after driving = moving)
 */
public final class ParkedGuard implements LocationListener {
	public enum State {PARKED, MOVING, WAITING_GPS, NO_PERMISSION}

	public interface Listener {
		void onParkedStateChanged(@NonNull State state);
	}

	static final float MOVING_KMH = 8f;
	static final float STOPPED_KMH = 4f;
	static final long STOP_CONFIRM_MS = 3000;
	static final long PERMISSION_RETRY_MS = 5000;

	private static ParkedGuard instance;

	private final Context ctx;
	private final LocationManager lm;
	private final Handler handler = new Handler(Looper.getMainLooper());
	private final List<Listener> listeners = new CopyOnWriteArrayList<>();
	private State state = State.WAITING_GPS;
	private boolean started;
	private long belowSince;

	private ParkedGuard(Context ctx) {
		this.ctx = ctx.getApplicationContext();
		lm = (LocationManager) this.ctx.getSystemService(Context.LOCATION_SERVICE);
	}

	public static synchronized ParkedGuard get(Context ctx) {
		if (instance == null) instance = new ParkedGuard(ctx);
		return instance;
	}

	public static boolean hasPermission(Context ctx) {
		return ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION)
				== PackageManager.PERMISSION_GRANTED;
	}

	@NonNull
	public State getState() {
		return state;
	}

	public boolean isVideoAllowed() {
		return state == State.PARKED;
	}

	/** Must be called on the main thread. */
	public void addListener(Listener l) {
		listeners.add(l);
		start();
		l.onParkedStateChanged(state);
	}

	/** Must be called on the main thread. */
	public void removeListener(Listener l) {
		listeners.remove(l);
		if (listeners.isEmpty()) stop();
	}

	private void start() {
		if (started) return;

		if ((lm == null) || !hasPermission(ctx)) {
			set(State.NO_PERMISSION);
			handler.removeCallbacks(retry);
			handler.postDelayed(retry, PERMISSION_RETRY_MS);
			return;
		}

		try {
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 0f, this,
					Looper.getMainLooper());
			started = true;
			if (state == State.NO_PERMISSION) set(State.WAITING_GPS);
		} catch (SecurityException | IllegalArgumentException ex) {
			set(State.NO_PERMISSION);
			handler.removeCallbacks(retry);
			handler.postDelayed(retry, PERMISSION_RETRY_MS);
		}
	}

	private final Runnable retry = () -> {
		if (!started && !listeners.isEmpty()) start();
	};

	private void stop() {
		handler.removeCallbacks(retry);
		if (!started) return;
		try {
			lm.removeUpdates(this);
		} catch (SecurityException ignore) {
		}
		started = false;
	}

	@Override
	public void onLocationChanged(@NonNull Location loc) {
		if (!loc.hasSpeed()) return;
		float kmh = loc.getSpeed() * 3.6f;
		long now = SystemClock.elapsedRealtime();

		if (kmh >= MOVING_KMH) {
			belowSince = 0;
			set(State.MOVING);
		} else if (kmh <= STOPPED_KMH) {
			if (belowSince == 0) belowSince = now;
			if (now - belowSince >= STOP_CONFIRM_MS) set(State.PARKED);
		} else {
			belowSince = 0;
			if (state == State.WAITING_GPS) set(State.MOVING);
		}
	}

	// Needed on older API levels where these are abstract.
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public void onProviderEnabled(@NonNull String provider) {
	}

	@Override
	public void onProviderDisabled(@NonNull String provider) {
		// GPS switched off: we can no longer confirm the car is stopped.
		belowSince = 0;
		if (state == State.PARKED) set(State.WAITING_GPS);
	}

	private void set(State s) {
		if (state == s) return;
		state = s;
		for (Listener l : listeners) l.onParkedStateChanged(s);
	}
}
