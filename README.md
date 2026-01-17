# HỆ THỐNG ĐIỂM DANH SINH VIÊN (JAVA FX)

Hệ thống điểm danh sinh viên là ứng dụng desktop được phát triển bằng JavaFX, nhằm hỗ trợ quản lý và ghi nhận việc điểm danh sinh viên trong môi trường lớp học. Hệ thống tích hợp nhiều hình thức điểm danh như điểm danh thủ công, điểm danh bằng mã QR và điểm danh thông qua camera, giúp tăng tính linh hoạt và giảm sai sót trong quá trình quản lý. Ứng dụng được xây dựng phục vụ mục đích học tập và nghiên cứu, phù hợp cho các học phần liên quan đến Lập trình Java, Cơ sở dữ liệu và Công nghệ phần mềm.

## CHỨC NĂNG HỆ THỐNG

Hệ thống hỗ trợ xác thực người dùng bao gồm đăng nhập, đăng ký tài khoản bằng email kết hợp mã OTP và khôi phục mật khẩu thông qua email. Sau khi đăng nhập thành công, người dùng có thể truy cập các chức năng điểm danh và quản lý dữ liệu.

Hệ thống hỗ trợ ba hình thức điểm danh. Thứ nhất là điểm danh thủ công, cho phép nhập thông tin sinh viên gồm MSSV, họ tên, lớp, thông tin học phần như môn học và phòng học, đồng thời lựa chọn trạng thái điểm danh gồm có mặt, vắng hoặc trễ. Thứ hai là điểm danh bằng mã QR, trong đó hệ thống tạo mã QR cho từng buổi học, sinh viên quét mã để thực hiện điểm danh và hệ thống ghi nhận thời gian tương ứng. Thứ ba là điểm danh bằng camera, cho phép kết nối webcam, chụp ảnh phục vụ điểm danh và lưu thông tin theo môn học và phòng học.

Ngoài chức năng điểm danh, hệ thống còn hỗ trợ quản lý sinh viên và giáo viên, bao gồm thêm, xóa và hiển thị danh sách. Chức năng thống kê cho phép tổng hợp số lượt điểm danh, số sinh viên, số môn học và hiển thị bảng chi tiết điểm danh gồm MSSV, họ tên, lớp, môn học, phòng, trạng thái và thời gian.

## CÔNG NGHỆ SỬ DỤNG

Hệ thống được xây dựng bằng ngôn ngữ lập trình Java. Giao diện người dùng được phát triển bằng JavaFX kết hợp FXML và CSS. Việc kết nối cơ sở dữ liệu được thực hiện thông qua JDBC, sử dụng hệ quản trị cơ sở dữ liệu Microsoft SQL Server. Chức năng camera được tích hợp thông qua thư viện OpenCV. Việc tạo và xử lý mã QR sử dụng thư viện ZXing.

## KIẾN TRÚC HỆ THỐNG

Hệ thống được thiết kế theo mô hình phân lớp gồm tầng giao diện (JavaFX, FXML), tầng xử lý nghiệp vụ (Controller), tầng truy cập dữ liệu (DAO, JDBC) và tầng cơ sở dữ liệu (SQL Server). Cách tổ chức này giúp hệ thống dễ bảo trì và mở rộng.

## CẤU TRÚC THƯ MỤC

- **src/**
  - **app/**: Khởi chạy chương trình
  - **controller/**: Xử lý nghiệp vụ và sự kiện
  - **dao/**: Truy cập và thao tác dữ liệu
  - **model/**: Các lớp đối tượng
  - **db/**: Kết nối cơ sở dữ liệu
  - **view/**: Các file FXML
  - **css/**: Định dạng giao diện
  - **assets/**: Hình ảnh và biểu tượng


## CÀI ĐẶT VÀ CHẠY CHƯƠNG TRÌNH

Môi trường yêu cầu gồm JDK 17 trở lên, Apache Maven, Microsoft SQL Server và webcam đối với chức năng camera. Người dùng cần cấu hình thông tin kết nối cơ sở dữ liệu trong file db/DataDBConnection.java. Sau đó chạy ứng dụng bằng lệnh mvn clean javafx:run.

## GIỚI HẠN HỆ THỐNG

Hệ thống chưa triển khai chức năng nhận diện khuôn mặt. Chức năng gửi OTP mang tính mô phỏng. Ứng dụng phục vụ mục đích học tập và chưa tối ưu cho triển khai thực tế.

## HƯỚNG PHÁT TRIỂN

Trong tương lai, hệ thống có thể được mở rộng bằng cách bổ sung phân quyền người dùng (Admin, Giáo viên), tích hợp nhận diện khuôn mặt, xuất báo cáo ra Excel và phát triển phiên bản web hoặc mobile.

## TÀI LIỆU THAM KHẢO

Oracle. JavaFX Documentation. https://openjfx.io/  
Oracle. JDBC – Java Database Connectivity. https://docs.oracle.com/javase/tutorial/jdbc/  
Microsoft. SQL Server Documentation. https://learn.microsoft.com/en-us/sql/sql-server/  
OpenCV. Open Source Computer Vision Library. https://opencv.org/  
ZXing Project. QR Code Processing Library. https://github.com/zxing/zxing  
Ian Sommerville. Software Engineering (10th Edition). Pearson Education.

## THÔNG TIN TÁC GIẢ

Sinh viên: Võ Mạnh Quân  
Trường: Đại học Đà Nẵng – VKU  
Ngành: Công nghệ thông tin  
Năm học: 2025 – 2026
