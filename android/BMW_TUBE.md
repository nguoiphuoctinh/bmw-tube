# BMW Tube

Fork of [Fermata](https://github.com/AndreyPavlenko/Fermata) (commit `8a1a5f4`,
GPL-3.0, by Andrey Pavlenko) for watching YouTube on a BMW G30 iDrive 7 screen through
wireless Android Auto. This fork stays GPL-3.0 (see `LICENSE`).

## How it works

An Android phone (target: Galaxy S20 FE or Pixel 4a, both stuck on Android 13) stays in the
car, runs wireless Android Auto, and runs this app. iDrive 7 only displays what Android Auto
sends. YouTube is the regular website inside Fermata's WebView (`modules/web`).

## Changes from upstream

1. **Parked-only video lock** (new):
   - `fermata/src/main/java/me/aap/fermata/parked/ParkedGuard.java`: reads GPS speed.
     Shows video only after the car is below 4 km/h for 3 s; hides immediately at 8 km/h
     or more; anything unknown (no permission, no GPS fix, GPS off) hides video.
   - `fermata/src/auto/java/me/aap/fermata/auto/ParkedOverlay.java`: opaque layer over the
     whole car UI while not parked. Hides the picture only; audio keeps playing. Blocks touch
     and controller input to the hidden UI.
   - `MainCarActivity.setContentView(...)`: wraps the car UI with the overlay.
   - `MainActivity.onCreate`: asks for location permission on the phone.
   - Auto manifest: adds `ACCESS_FINE_LOCATION` / `ACCESS_COARSE_LOCATION`.
2. **Mirroring removed**: `MirrorService`, `MirrorServiceFS`, `ProjectionActivity`,
   `ProjectionService` and `LauncherActivity` are taken out of the auto manifest, so no
   other phone app can be projected to the car around the lock. Their classes are still in
   the source but cannot run.
3. **Addon modules pruned**: only `modules/web` (YouTube) is kept.

## Build (needs Android SDK; allowed domains: dl.google.com, maven.google.com,
services.gradle.org, downloads.gradle.org, plugins.gradle.org, repo.maven.apache.org,
repo1.maven.org)

```sh
./gradlew :fermata:assembleAutoRelease -PABI=arm64-v8a \
    -PAPP_ID_SFX=.bmwtube -PAPP_NAME="BMW Tube"
```

## Install on the phone (Android 13)

1. Install the APK (allow install from unknown sources).
2. Open BMW Tube on the phone, grant Location (precise) and media permissions.
3. Android Auto settings → tap "Version" 10 times → Developer settings → enable
   "Unknown sources".
4. Connect to the car; BMW Tube appears in the Android Auto app list.

## Status

Not yet built or tested. Real-car test needed for: overlay covering the screen, GPS
behaviour in garages (indoors the video stays hidden until a GPS fix confirms the stop),
and wireless Android Auto video on the chosen phone.
