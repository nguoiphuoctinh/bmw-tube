# BMW Tube for iPhone (old iOS, TrollStore)

Personal fork of [CarTube](https://github.com/Avangelista/CarTube) (commit `2abc94d`,
GPL-3.0, by Rory Madden / Avangelista) to watch YouTube on the BMW G30 iDrive 7 screen
through wireless CarPlay, using an iPhone only. For the owner's own car; not distributed.
This fork stays GPL-3.0 (see `license`).

## Who this is for

An iPhone that is **still on iOS 14.0 - 16.6.1, 16.7 RC (20H18) or 17.0**. Those are the only
versions where TrollStore installs (it relies on a CoreTrust bug Apple fixed in 16.7.x and
17.0.1+). TrollStore is what lets this app carry the private CarPlay entitlements
(`SBStarkCapable`, `platform-application`, ...) that Apple does not give third-party apps.
Upstream CarTube states 14.0 - 15.4.1; 16.x / 17.0 under TrollStore 2 is not yet tested here.

Trade-off: that iPhone can never be updated again, so it stays without security fixes. Use a
dedicated in-car iPhone, not the daily phone.

## Changes from upstream

1. **Parked-only video lock** (`CarTube/CarPlay/CarPlayViewController.swift`, bottom):
   `ParkedGuard` reads iPhone GPS speed (Core Location). Video is shown only after the car
   is below 4 km/h for 3 s; it hides at once at 8 km/h or more; no permission / no GPS speed
   yet / GPS lost all hide the video. `ParkedOverlayView` covers the whole CarPlay screen and
   blocks touches; the YouTube audio keeps playing underneath.
2. `CarTubeApp.swift`: asks for location permission at launch; the upstream update check is
   disabled (upstream releases do not have the lock).
3. `Info.plist`: location usage text; display name "BMW Tube".

## Build (macOS + Xcode + ldid required)

```sh
brew install ldid      # once
./ipabuild.sh          # -> build/CarTube.ipa (unsigned + entitlements via ldid)
```

## Install

1. Install TrollStore on the in-car iPhone (guide: ios.cfw.guide, pick your exact iOS).
2. Open `CarTube.ipa` with TrollStore -> Install.
3. Open BMW Tube on the phone once and allow Location "While Using the App".
4. Connect the iPhone to the car's wireless CarPlay; BMW Tube appears on the CarPlay screen.

## Status

Not yet built or tested on a car.
