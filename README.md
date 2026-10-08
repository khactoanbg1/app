# VF7 Smart View 2.0 — OpenStreetMap / MapLibre

Ứng dụng **trên điện thoại** hiển thị YouTube trong WebView và bản đồ OpenStreetMap bằng MapLibre GL JS trong WebView song song; tỷ lệ 50:50 / 70:30 / 30:70. Nút Vị trí sử dụng vị trí gần nhất mà Android đã ghi nhận (không phải dẫn đường liên tục). Không cần Google Maps API key.

**GIỚI HẠN QUAN TRỌNG:** APK này **không phải ứng dụng Android Auto** và **không thể hiện lên màn hình Android Auto VF7 chỉ bằng cài APK**. Không có dẫn đường turn-by-turn, tìm địa điểm hay chế độ offline. Chỉ thao tác video khi xe đỗ; không được dùng video khi đang lái. Nhúng YouTube có thể bị từ chối tùy nội dung.

**Kết nối:** cần Internet cho CDN MapLibre, ô bản đồ OSM và YouTube. OSM public tile server có [usage policy](https://operations.osmfoundation.org/policies/tiles/), không phù hợp để khai thác nặng hoặc phân phối quy mô lớn; nếu triển khai công khai cần nhà cung cấp tiles tuân thủ chính sách. Tín dụng © OpenStreetMap contributors được hiển thị trên bản đồ.

**GitHub:** Tải toàn bộ nội dung trong ZIP (bao gồm thư mục `.github`) lên **gốc** repository, ghi đè file cũ; không thêm thư mục bọc. Vào Actions > Build Android APK > Run workflow. Tải artifact nếu job hoàn tất xanh. Build chưa được kiểm tra thực tế tại đây.
