# VF7 Smart Launcher — bản thử nghiệm 1.0

Ứng dụng Android cài trên Samsung Galaxy S22 Ultra (Android 14), **không root**. Có các nút mở Google Maps, YouTube, YouTube Music; lưu lựa chọn bố cục 50:50/70:30/30:70; thử yêu cầu mở Maps và YouTube liền kề trên **điện thoại** (nếu One UI cho phép).

## Giới hạn cần hiểu rõ
- Đây **không phải** ứng dụng phát YouTube video trên màn hình Android Auto. Android Auto nguyên bản VF7 không cho APK thông thường vẽ giao diện YouTube + Google Maps tùy chọn trên màn hình xe.
- Tỷ lệ 50:50/70:30/30:70 được lưu vào ứng dụng để phục vụ cấu hình; không điều khiển tỷ lệ Android Auto và cũng không ép được chia màn hình trên Android 14.
- Khi di chuyển, dùng Google Maps + YouTube Music (âm thanh) trong Android Auto theo bố cục hệ thống hỗ trợ. Không xem video khi lái xe.

## Build APK không cần Android SDK trên máy cá nhân
1. Tạo kho GitHub mới, ví dụ `vf7-smart-launcher`.
2. Giải nén tệp ZIP này; upload **nội dung bên trong thư mục VF7SmartLauncher** lên gốc kho GitHub (gồm `.github`). Khi upload qua trình duyệt, kiểm tra thư mục `.github/workflows` vẫn giữ đúng.
3. Vào tab `Actions` → workflow `Build Android APK` → `Run workflow` (hoặc push vào nhánh main).
4. Chờ tác vụ hoàn tất. Trong trang run, chọn artifact `VF7-Smart-Launcher-APK` để tải xuống.
5. Giải nén artifact để nhận `app-debug.apk`. Chép vào điện thoại, cho phép "Cài ứng dụng không rõ nguồn gốc" với app quản lý file khi được hỏi rồi cài.

Bản debug APK được ký tự động với khóa debug cho mục đích thử nghiệm, không dùng phát hành trên Play Store.
