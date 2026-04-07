-- =========================
-- 1. DROP TABLE
-- =========================
DROP TABLE IF EXISTS BirthdayLog CASCADE;
DROP TABLE IF EXISTS Employee CASCADE;
DROP TABLE IF EXISTS Department CASCADE;

-- =========================
-- 2. Department
-- =========================
CREATE TABLE Department (
    DepartmentID SERIAL PRIMARY KEY,
    DepartmentCode VARCHAR(50) NOT NULL UNIQUE,
    DepartmentName VARCHAR(255) NOT NULL
);

-- =========================
-- 3. Employee
-- =========================
CREATE TABLE Employee (
    EmployeeID SERIAL PRIMARY KEY,
    EmployeeCode VARCHAR(50) NOT NULL UNIQUE,
    FullName VARCHAR(255) NOT NULL,
    JobTitle VARCHAR(255),

    PhoneNumber VARCHAR(20),
    Email VARCHAR(255) NOT NULL UNIQUE,

    DateOfBirth DATE NOT NULL,
    BirthDay INT NOT NULL,
    BirthMonth INT NOT NULL,

    DepartmentID INT NOT NULL,

    IsActive BOOLEAN DEFAULT TRUE,

    CONSTRAINT fk_employee_department
        FOREIGN KEY (DepartmentID)
        REFERENCES Department(DepartmentID),

    -- 🔥 đảm bảo ngày/tháng hợp lệ
    CONSTRAINT chk_birth_day CHECK (BirthDay BETWEEN 1 AND 31),
    CONSTRAINT chk_birth_month CHECK (BirthMonth BETWEEN 1 AND 12),

    -- 🔥 đảm bảo BirthDay/BirthMonth khớp với DateOfBirth
    CONSTRAINT chk_birth_match
	CHECK (
    BirthDay = EXTRACT(DAY FROM DateOfBirth)::INT
    AND BirthMonth = EXTRACT(MONTH FROM DateOfBirth)::INT
	)
);

-- =========================
-- 4. BirthdayLog
-- =========================
CREATE TABLE BirthdayLog (
    LogID BIGSERIAL PRIMARY KEY,
    EmployeeID INT NOT NULL,
    SentDate DATE NOT NULL,
    Status VARCHAR(20) NOT NULL,
    ErrorMessage TEXT,
    CreatedAt TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_log_employee
        FOREIGN KEY (EmployeeID)
        REFERENCES Employee(EmployeeID)
        ON DELETE CASCADE,

    CONSTRAINT uq_employee_date
        UNIQUE (EmployeeID, SentDate),

    -- 🔥 control status
    CONSTRAINT chk_status
        CHECK (Status IN ('SUCCESS', 'FAILED'))
);

-- =========================
-- 5. INDEX
-- =========================

CREATE INDEX idx_employee_birthday 
ON Employee (BirthMonth, BirthDay);

CREATE INDEX idx_log_employee_date 
ON BirthdayLog (EmployeeID, SentDate);

-- =========================
-- 6. INSERT DATA
-- =========================

-- Department
INSERT INTO Department (DepartmentCode, DepartmentName)
VALUES 
('P.PTUD', 'Phòng Phát triển ứng dụng'),
('P.KT', 'Phòng Kỹ thuật'),
('BGD.CTY', 'Ban Giám đốc Công ty'),
('BGD.TT', 'Ban Giám đốc trung tâm');

-- Employee (phải truyền đủ 3 field)
INSERT INTO Employee (
    EmployeeCode, FullName, JobTitle, PhoneNumber, Email,
    DateOfBirth, BirthDay, BirthMonth, DepartmentID
)
VALUES
('000001', 'Nguyễn Văn A', 'DEV', '0901111111', 'test@company.com',
 '2003-10-21', 21, 10, 1);