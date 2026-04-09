✅ Kiến trúc bạn đang dùng
Controller
@GetMapping
public ResponseEntity<Page<CheckingUserResponse>> getUsers(
Pageable pageable,
@RequestParam(required = false) String login,
@RequestParam(required = false) String role,
@RequestParam(required = false, defaultValue = "false") boolean activeStatus
)

✔ Nhận trực tiếp Pageable → chuẩn Spring
✔ Filter tách riêng (login, role, activeStatus) → rõ ràng
✔ Không cần DTO request → gọn nhẹ cho REST API

Service
public Page<CheckingUserResponse> getUsers(String login, String role, boolean activeStatus, Pageable pageable)

✔ Service nhận:

filter params
pageable

👉 Đây chính là pattern chuẩn:

Filter + Pageable → Service → Repository

Repository (ngầm hiểu)
Page<CheckingUserResponse> searchUsers(String login, String role, boolean activeStatus, Pageable pageable);

✔ Đẩy pagination xuống DB → đúng
✔ Không load full → đúng

🔥 Đây chính là pattern “chuẩn xịn” ngoài thực tế

Flow của bạn:

Controller
↓
Service (business logic)
↓
Repository (query + pageable)
↓
DB (LIMIT/OFFSET)

👉 Đây là cách:

Spring khuyến nghị
Microservice thường dùng
Clean & scalable
🧠 So với cách cũ bạn thấy
Cách	Đánh giá
DTO request + tự build Pageable	😐 dư thừa
Controller tự normalize	😐 verbose
Bạn đang làm (Pageable trực tiếp)	🔥 BEST
⚠️ Nhưng có 2 điểm bạn nên cải thiện (rất quan trọng)
1. ❗ activeStatus default = false là nguy hiểm
   @RequestParam(required = false, defaultValue = "false") boolean activeStatus

👉 Vấn đề:

Không truyền → luôn = false
Không thể phân biệt:
user không filter
user muốn false thật
✅ Cách chuẩn hơn
@RequestParam(required = false) Boolean activeStatus

👉 Sau đó xử lý trong repo:

(:activeStatus IS NULL OR u.active = :activeStatus)

✔ Cho phép:

không filter
filter true
filter false
2. 🔒 Chưa giới hạn size

Bạn đang bỏ normalizeSize() → OK
👉 nhưng phải có config:

spring:
data:
web:
pageable:
max-page-size: 100
default-page-size: 20

✔ Tránh:

?size=999999
💡 Optional (pro hơn nữa)
Validate sort field
if (pageable.getSort().isSorted()) {
pageable.getSort().forEach(order -> {
if (!List.of("login", "role", "createdDate").contains(order.getProperty())) {
throw new RuntimeException("Invalid sort field");
}
});
}
🎯 Kết luận

👉 Cách bạn đang làm:

✔ Đúng kiến trúc
✔ Clean
✔ Chuẩn REST
✔ Chuẩn Spring Boot

🚀 Final verdict

👉 Bạn đã đi đúng hướng “senior-level pattern” rồi, chỉ cần fix nhẹ:

đổi boolean → Boolean
thêm max-page-size config
(optional) validate sort