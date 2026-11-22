# BMW Tube

Xem YouTube trên màn hình iDrive của BMW (thử trên G30 iDrive 7), **chỉ hiện hình khi xe đã dừng**.
Khi xe chạy (GPS từ 8 km/h trở lên) hình bị ẩn, tiếng vẫn phát; dừng dưới 4 km/h trong 3 giây thì hình hiện lại.

Dự án cá nhân, không liên quan tới BMW, Google, Apple hay YouTube. Dùng với rủi ro của bạn.

## Tải về

Vào [Releases](../../releases/tag/latest):

| File | Dành cho | Kết nối với xe |
|---|---|---|
| `BMW-Tube-android-arm64.apk` | Điện thoại Android 11–13 (khuyên dùng Android 13, ví dụ Galaxy S20 FE, Pixel 4a) | Android Auto không dây |
| `BMW-Tube-ios-legacy.ipa` | iPhone còn ở iOS 14.0–16.6.1, 16.7 RC hoặc 17.0 (cài bằng TrollStore) | CarPlay không dây |

Xe cần có Android Auto / CarPlay không dây (iDrive 7 trở lên).

## Cài trên Android

1. Tải APK, mở ra, cho phép cài từ nguồn không xác định.
2. Mở **BMW Tube** trên điện thoại, cấp quyền **Vị trí chính xác**.
3. Mở Android Auto → bấm "Phiên bản" 10 lần → Developer settings → bật **Unknown sources**.
4. Kết nối với xe, chọn BMW Tube trên màn hình iDrive.

Android 14 trở lên có thể chỉ ra tiếng, không ra hình.

## Cài trên iPhone (iOS cũ)

1. Cài TrollStore đúng bản iOS của máy (xem ios.cfw.guide).
2. Mở file IPA bằng TrollStore → Install.
3. Mở BMW Tube một lần, cho phép Vị trí "Khi dùng app".
4. Kết nối CarPlay không dây với xe.

iPhone từ iOS 17.0.1 trở lên **không cài được** (TrollStore không hỗ trợ).

## Mã nguồn và giấy phép

- `android/`: fork của [Fermata](https://github.com/AndreyPavlenko/Fermata), GPL-3.0. Chi tiết thay đổi: `android/BMW_TUBE.md`.
- `ios-legacy/`: fork của [CarTube](https://github.com/Avangelista/CarTube), GPL-3.0. Chi tiết thay đổi: `ios-legacy/BMW_TUBE.md`.

Toàn bộ repo phát hành theo GPL-3.0.
