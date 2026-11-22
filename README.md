# BMW Tube (private)

YouTube on the BMW G30 iDrive 7 screen, parked-only video lock. Personal use, GPL-3.0 forks.

- `android/` - Fermata fork, runs on an Android phone (Android 11-13) via wireless Android Auto.
  See `android/BMW_TUBE.md`. CI: `.github/workflows/android.yml` -> APK artifact.
- `ios-legacy/` - CarTube fork, runs on an iPhone on iOS <= 17.0 via TrollStore + wireless CarPlay.
  See `ios-legacy/BMW_TUBE.md`. CI: `.github/workflows/ios-legacy.yml` -> IPA artifact.

Both hide the picture while the car moves (GPS >= 8 km/h) and keep the audio; the picture
returns after 3 s below 4 km/h.
