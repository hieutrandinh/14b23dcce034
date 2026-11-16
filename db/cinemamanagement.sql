CREATE DATABASE IF NOT EXISTS cinemamanagement CHARACTER SET utf8mb4;
USE cinemamanagement;

CREATE TABLE tblUser4 (
  id INT AUTO_INCREMENT PRIMARY KEY,
  fullName VARCHAR(120) NOT NULL,
  phoneNumber VARCHAR(15) NOT NULL UNIQUE,
  email VARCHAR(150),
  username VARCHAR(20) NOT NULL UNIQUE,
  password VARCHAR(100) NOT NULL,
  userType ENUM('CUSTOMER','STAFF') NOT NULL
);

CREATE TABLE tblStaff4 (
  id INT PRIMARY KEY,
  staffCode VARCHAR(20) NOT NULL UNIQUE,
  salary DECIMAL(12,2),
  hireDate DATE,
  CONSTRAINT fk_staff_user FOREIGN KEY (id) REFERENCES tblUser4(id)
);

CREATE TABLE tblManagementStaff4 (
  id INT PRIMARY KEY,
  department VARCHAR(60),
  CONSTRAINT fk_mgmt_staff FOREIGN KEY (id) REFERENCES tblStaff4(id)
);

CREATE TABLE tblSeller4 (
  id INT PRIMARY KEY,
  counterPosition VARCHAR(20),
  CONSTRAINT fk_seller_staff FOREIGN KEY (id) REFERENCES tblStaff4(id)
);

CREATE TABLE tblCustomer4 (
  id INT PRIMARY KEY,
  dateOfBirth DATE,
  address VARCHAR(200),
  CONSTRAINT fk_customer_user FOREIGN KEY (id) REFERENCES tblUser4(id)
);

CREATE TABLE tblMemberCard4 (
  id INT AUTO_INCREMENT PRIMARY KEY,
  cardCode VARCHAR(20) NOT NULL UNIQUE,
  customerId INT NOT NULL UNIQUE,
  registrationDate DATE NOT NULL,
  loyaltyPoint INT NOT NULL DEFAULT 0,
  pointsUsed INT NOT NULL DEFAULT 0,
  pointsBalance INT NOT NULL DEFAULT 0,
  CONSTRAINT fk_card_customer FOREIGN KEY (customerId) REFERENCES tblCustomer4(id)
);

CREATE TABLE tblMovie4 (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(150) NOT NULL,
  genre VARCHAR(60),
  director VARCHAR(100),
  basePrice DECIMAL(10,2) NOT NULL,
  duration INT NOT NULL
);

CREATE TABLE tblScreeningRoom4 (
  id INT AUTO_INCREMENT PRIMARY KEY,
  roomName VARCHAR(50) NOT NULL,
  totalSeats INT NOT NULL,
  seatMap VARCHAR(500),
  priceMultiplier DECIMAL(4,2) NOT NULL DEFAULT 1.00
);

CREATE TABLE tblSeat4 (
  id INT AUTO_INCREMENT PRIMARY KEY,
  roomId INT NOT NULL,
  seatRow VARCHAR(2) NOT NULL,
  seatNumber INT NOT NULL,
  CONSTRAINT fk_seat_room FOREIGN KEY (roomId) REFERENCES tblScreeningRoom4(id),
  UNIQUE KEY uq_room_seat (roomId, seatRow, seatNumber)
);

CREATE TABLE tblShowTime4 (
  id INT AUTO_INCREMENT PRIMARY KEY,
  movieId INT NOT NULL,
  showDate DATE NOT NULL,
  startTime TIME NOT NULL,
  priceMultiplier DECIMAL(4,2) NOT NULL DEFAULT 1.00,
  CONSTRAINT fk_show_movie FOREIGN KEY (movieId) REFERENCES tblMovie4(id)
);

CREATE TABLE tblShowTimeRoom4 (
  showTimeId INT NOT NULL,
  roomId INT NOT NULL,
  PRIMARY KEY (showTimeId, roomId),
  CONSTRAINT fk_showroom_show FOREIGN KEY (showTimeId) REFERENCES tblShowTime4(id),
  CONSTRAINT fk_showroom_room FOREIGN KEY (roomId) REFERENCES tblScreeningRoom4(id)
);

CREATE TABLE tblInvoice4 (
  id INT AUTO_INCREMENT PRIMARY KEY,
  invoiceCode VARCHAR(20) NOT NULL UNIQUE,
  customerId INT NULL,
  sellerId INT NOT NULL,
  issuedDate DATETIME NOT NULL,
  totalAmount DECIMAL(12,2) NOT NULL,
  discountAmount DECIMAL(12,2) NOT NULL DEFAULT 0,
  finalAmount DECIMAL(12,2) NOT NULL,
  CONSTRAINT fk_invoice_customer FOREIGN KEY (customerId) REFERENCES tblCustomer4(id),
  CONSTRAINT fk_invoice_seller FOREIGN KEY (sellerId) REFERENCES tblSeller4(id)
);

CREATE TABLE tblTicket4 (
  id INT AUTO_INCREMENT PRIMARY KEY,
  invoiceId INT NOT NULL,
  showTimeId INT NOT NULL,
  seatId INT NOT NULL,
  price DECIMAL(10,2) NOT NULL,
  CONSTRAINT fk_ticket_invoice FOREIGN KEY (invoiceId) REFERENCES tblInvoice4(id),
  CONSTRAINT fk_ticket_show FOREIGN KEY (showTimeId) REFERENCES tblShowTime4(id),
  CONSTRAINT fk_ticket_seat FOREIGN KEY (seatId) REFERENCES tblSeat4(id),
  UNIQUE KEY uq_show_seat (showTimeId, seatId)
);

CREATE TABLE tblPayment4 (
  id INT AUTO_INCREMENT PRIMARY KEY,
  paymentCode VARCHAR(20) NOT NULL UNIQUE,
  invoiceId INT NOT NULL UNIQUE,
  amount DECIMAL(12,2) NOT NULL,
  payAt DATETIME,
  status VARCHAR(20) NOT NULL,
  CONSTRAINT fk_payment_invoice FOREIGN KEY (invoiceId) REFERENCES tblInvoice4(id)
);

CREATE TABLE tblCounterPayment4 (
  id INT PRIMARY KEY,
  paymentMethod VARCHAR(20) NOT NULL,
  CONSTRAINT fk_counterpay_payment FOREIGN KEY (id) REFERENCES tblPayment4(id)
);

CREATE TABLE tblOnlinePayment4 (
  id INT PRIMARY KEY,
  paymentGateway VARCHAR(50) NOT NULL,
  transactionCode VARCHAR(50),
  CONSTRAINT fk_onlinepay_payment FOREIGN KEY (id) REFERENCES tblPayment4(id)
);

CREATE TABLE tblMovieStat4 (
  id INT PRIMARY KEY,
  totalTicketsSold INT NOT NULL DEFAULT 0,
  totalRevenue DECIMAL(14,2) NOT NULL DEFAULT 0,
  viewerCount INT NOT NULL DEFAULT 0,
  showCount INT NOT NULL DEFAULT 0,
  occupancyRate DECIMAL(5,2) NOT NULL DEFAULT 0,
  CONSTRAINT fk_moviestat_movie FOREIGN KEY (id) REFERENCES tblMovie4(id)
);

CREATE TABLE tblCustomerStat4 (
  id INT PRIMARY KEY,
  totalSpent DECIMAL(14,2) NOT NULL DEFAULT 0,
  totalTicketsPurchased INT NOT NULL DEFAULT 0,
  numberOfVisits INT NOT NULL DEFAULT 0,
  lastVisitDate DATE,
  averageSpendPerVisit DECIMAL(12,2) NOT NULL DEFAULT 0,
  CONSTRAINT fk_customerstat_customer FOREIGN KEY (id) REFERENCES tblCustomer4(id)
);

INSERT INTO tblUser4 (fullName, phoneNumber, email, username, password, userType) VALUES
  ('Nguyen Van B', '0900000001', 'nguyenvanb@cinema.example', 'nguyenvanb', 'seller123', 'STAFF');
SET @seller1 = LAST_INSERT_ID();
INSERT INTO tblStaff4 (id, staffCode, salary, hireDate) VALUES (@seller1, 'ST001', 8000000, '2024-01-10');
INSERT INTO tblSeller4 (id, counterPosition) VALUES (@seller1, 'Counter 01');

INSERT INTO tblUser4 (fullName, phoneNumber, email, username, password, userType) VALUES
  ('Tran Thi C', '0900000002', 'tranthic@cinema.example', 'tranthic', 'seller123', 'STAFF');
SET @seller2 = LAST_INSERT_ID();
INSERT INTO tblStaff4 (id, staffCode, salary, hireDate) VALUES (@seller2, 'ST002', 7500000, '2024-06-01');
INSERT INTO tblSeller4 (id, counterPosition) VALUES (@seller2, 'Counter 02');

INSERT INTO tblUser4 (fullName, phoneNumber, email, username, password, userType) VALUES
  ('Le Van D', '0900000003', 'levand@cinema.example', 'levand', 'seller123', 'STAFF');
SET @manager1 = LAST_INSERT_ID();
INSERT INTO tblStaff4 (id, staffCode, salary, hireDate) VALUES (@manager1, 'ST003', 15000000, '2023-03-15');
INSERT INTO tblManagementStaff4 (id, department) VALUES (@manager1, 'Operations');

INSERT INTO tblUser4 (fullName, phoneNumber, email, username, password, userType) VALUES
  ('Nguyen Van A', '0912345678', 'nguyenvana@gmail.com', 'nguyenvana', 'member123', 'CUSTOMER');
SET @cus1 = LAST_INSERT_ID();
INSERT INTO tblCustomer4 (id, dateOfBirth, address) VALUES (@cus1, '2000-05-01', 'Ha Noi');
INSERT INTO tblMemberCard4 (cardCode, customerId, registrationDate, loyaltyPoint, pointsUsed, pointsBalance) VALUES
  ('MC0001', @cus1, '2026-10-19', 12, 0, 12);
SET @card1 = LAST_INSERT_ID();

INSERT INTO tblUser4 (fullName, phoneNumber, email, username, password, userType) VALUES
  ('Pham Thi E', '0912345679', 'phamthie@gmail.com', 'phamthie', 'member123', 'CUSTOMER');
SET @cus2 = LAST_INSERT_ID();
INSERT INTO tblCustomer4 (id, dateOfBirth, address) VALUES (@cus2, '1998-11-20', 'Hai Phong');
INSERT INTO tblMemberCard4 (cardCode, customerId, registrationDate, loyaltyPoint, pointsUsed, pointsBalance) VALUES
  ('MC0002', @cus2, '2026-10-20', 19, 0, 19);
SET @card2 = LAST_INSERT_ID();

INSERT INTO tblUser4 (fullName, phoneNumber, email, username, password, userType) VALUES
  ('Hoang Van F', '0912345680', 'hoangvanf@gmail.com', 'hoangvanf', 'member123', 'CUSTOMER');
SET @cus3 = LAST_INSERT_ID();
INSERT INTO tblCustomer4 (id, dateOfBirth, address) VALUES (@cus3, '2003-02-14', 'Da Nang');
INSERT INTO tblMemberCard4 (cardCode, customerId, registrationDate, loyaltyPoint, pointsUsed, pointsBalance) VALUES
  ('MC0003', @cus3, '2026-10-25', 0, 0, 0);

INSERT INTO tblUser4 (fullName, phoneNumber, email, username, password, userType) VALUES
  ('Vu Thi G', '0912345681', 'vuthig@gmail.com', 'vuthig', 'member123', 'CUSTOMER');
SET @cus4 = LAST_INSERT_ID();
INSERT INTO tblCustomer4 (id, dateOfBirth, address) VALUES (@cus4, '1995-07-30', 'Ha Noi');

INSERT INTO tblMovie4 (name, genre, director, basePrice, duration) VALUES
  ('Avengers: Endgame',      'Action',    'Anthony Russo',    70000, 180),
  ('Avengers: Infinity War', 'Action',    'Anthony Russo',    60000, 160),
  ('Inside Out 2',           'Animation', 'Kelsey Mann',      55000, 96),
  ('Dune: Part Two',         'Sci-Fi',    'Denis Villeneuve', 75000, 166),
  ('The Godfather',          'Drama',     'Francis Coppola',  50000, 175);

INSERT INTO tblScreeningRoom4 (roomName, totalSeats, seatMap, priceMultiplier) VALUES
  ('Room 1', 24, 'A-C/1-8', 1.00),
  ('Room 2', 24, 'A-C/1-8', 1.20),
  ('Room 3', 24, 'A-C/1-8', 1.50);

INSERT INTO tblSeat4 (roomId, seatRow, seatNumber)
SELECT r.id, row_src.row_label, nums.n
FROM tblScreeningRoom4 r
JOIN (SELECT 'A' AS row_label UNION SELECT 'B' UNION SELECT 'C') row_src
JOIN (SELECT 1 AS n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8) nums;

INSERT INTO tblShowTime4 (movieId, showDate, startTime, priceMultiplier) VALUES
  (1, '2026-11-02', '18:00:00', 1.00),
  (1, '2026-11-02', '20:30:00', 1.15),
  (1, '2026-11-02', '21:00:00', 1.10),
  (2, '2026-11-02', '19:00:00', 1.00),
  (2, '2026-11-03', '19:30:00', 1.10),
  (3, '2026-11-03', '09:00:00', 0.80),
  (3, '2026-11-03', '14:00:00', 1.00),
  (4, '2026-11-03', '20:00:00', 1.20),
  (4, '2026-11-04', '17:30:00', 1.00),
  (5, '2026-11-04', '15:00:00', 0.90);

INSERT INTO tblShowTimeRoom4 (showTimeId, roomId) VALUES
  (1, 1), (1, 2),
  (2, 1),
  (3, 2),
  (4, 2),
  (5, 3),
  (6, 1), (6, 3),
  (7, 2),
  (8, 3),
  (9, 1),
  (10, 2);

INSERT INTO tblInvoice4 (invoiceCode, customerId, sellerId, issuedDate, totalAmount, discountAmount, finalAmount)
VALUES ('HD0001', @cus1, @seller1, '2026-11-02 17:35:00', 140000, 14000, 126000);
SET @inv1 = LAST_INSERT_ID();
INSERT INTO tblTicket4 (invoiceId, showTimeId, seatId, price)
SELECT @inv1, 1, s.id, 70000
FROM tblSeat4 s WHERE s.roomId = 1 AND s.seatRow = 'A' AND s.seatNumber IN (1, 2);
INSERT INTO tblPayment4 (paymentCode, invoiceId, amount, payAt, status)
VALUES ('PM0001', @inv1, 126000, '2026-11-02 17:36:00', 'SUCCESS');
INSERT INTO tblCounterPayment4 (id, paymentMethod) VALUES (LAST_INSERT_ID(), 'Cash');

INSERT INTO tblInvoice4 (invoiceCode, customerId, sellerId, issuedDate, totalAmount, discountAmount, finalAmount)
VALUES ('HD0002', NULL, @seller2, '2026-11-02 20:05:00', 80500, 0, 80500);
SET @inv2 = LAST_INSERT_ID();
INSERT INTO tblTicket4 (invoiceId, showTimeId, seatId, price)
SELECT @inv2, 2, s.id, 80500
FROM tblSeat4 s WHERE s.roomId = 1 AND s.seatRow = 'B' AND s.seatNumber = 3;
INSERT INTO tblPayment4 (paymentCode, invoiceId, amount, payAt, status)
VALUES ('PM0002', @inv2, 80500, '2026-11-02 20:06:00', 'SUCCESS');
INSERT INTO tblCounterPayment4 (id, paymentMethod) VALUES (LAST_INSERT_ID(), 'Card');

INSERT INTO tblInvoice4 (invoiceCode, customerId, sellerId, issuedDate, totalAmount, discountAmount, finalAmount)
VALUES ('HD0003', @cus2, @seller1, '2026-11-02 18:40:00', 216000, 21600, 194400);
SET @inv3 = LAST_INSERT_ID();
INSERT INTO tblTicket4 (invoiceId, showTimeId, seatId, price)
SELECT @inv3, 4, s.id, 72000
FROM tblSeat4 s WHERE s.roomId = 2 AND s.seatRow = 'C' AND s.seatNumber IN (1, 2, 3);
INSERT INTO tblPayment4 (paymentCode, invoiceId, amount, payAt, status)
VALUES ('PM0003', @inv3, 194400, '2026-11-02 18:42:00', 'SUCCESS');
INSERT INTO tblCounterPayment4 (id, paymentMethod) VALUES (LAST_INSERT_ID(), 'Cash');

INSERT INTO tblMovieStat4 (id, totalTicketsSold, totalRevenue, viewerCount, showCount, occupancyRate) VALUES
  (1, 3, 206500, 3, 3, 4.17),
  (2, 3, 194400, 3, 2, 6.25),
  (3, 0, 0, 0, 2, 0.00),
  (4, 0, 0, 0, 2, 0.00),
  (5, 0, 0, 0, 1, 0.00);

INSERT INTO tblCustomerStat4 (id, totalSpent, totalTicketsPurchased, numberOfVisits, lastVisitDate, averageSpendPerVisit) VALUES
  (@cus1, 126000, 2, 1, '2026-11-02', 126000),
  (@cus2, 194400, 3, 1, '2026-11-02', 194400);
