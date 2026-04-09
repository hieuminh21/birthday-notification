CREATE TABLE message_template (
id SERIAL PRIMARY KEY,
name VARCHAR(100) NOT NULL,
type VARCHAR(50) NOT NULL,
content TEXT NOT NULL,
is_active BOOLEAN DEFAULT true,
is_default BOOLEAN DEFAULT true,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO message_template (name, type, content)
VALUES (
'Birthday Template Default',
'BIRTHDAY',
'Chúc mừng sinh nhật {jobTitle} {fullName}! 🎉
Chúc bạn một ngày thật nhiều niềm vui, hạnh phúc và luôn tràn đầy năng lượng tích cực.
Hy vọng tuổi mới sẽ mang đến cho bạn thật nhiều thành công trong công việc cũng như cuộc sống. 🎂'
);

