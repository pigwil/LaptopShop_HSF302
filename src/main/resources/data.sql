INSERT INTO users ( user_code, full_name, address, phone, username, password, role, created_at, updated_at, email)
VALUES
    ( 'U001', N'Nguyễn Văn A', N'Hà Nội', '0912345678', 'admin', '123456', 'ADMIN', GETDATE(), GETDATE(), 'admin@gmail.com'),
    ( 'U002', N'Trần Thị B', N'TP.HCM', '0987654321', 'user1', '123456', 'USER', GETDATE(), GETDATE(), 'user1@gmail.com'),
    ( 'U003', N'Lê Minh Cường', N'Đà Nẵng', '0905123456', 'user2', '123456', 'USER', GETDATE(), GETDATE(), 'user2@gmail.com'),
    ( 'U004', N'Phạm Thu Dung', N'Hải Phòng', '0934123456', 'user3', '123456', 'USER', GETDATE(), GETDATE(), 'user3@gmail.com'),
    ( 'U005', N'Đặng Hữu Nam', N'Cần Thơ', '0945123456', 'user4', '123456', 'USER', GETDATE(), GETDATE(), 'user4@gmail.com'),
    ( 'U006', N'Ngô Hồng Anh', N'Bình Dương', '0976123456', 'user5', '123456', 'USER', GETDATE(), GETDATE(), 'user5@gmail.com');

-- ===========================================================

INSERT INTO laptops ( laptop_code, laptop_name, laptop_status, brand, cpu_info, ram_info, price, quantity_in_stock, user_id, img_path, is_deleted)
VALUES
    ( 'L001', 'Acer Aspire Go', 'Available', 'Acer', 'Intel i3 12th Gen', '8GB', 12000000, 15, 1, 'asus1.jpg', 0),
    ('L002', 'Acer Aspire Lite', 'Available', 'Acer', 'Intel i5 13th Gen', '16GB', 15500000, 10, 1, 'acer2.jpg', 0),
    ( 'L003', 'Acer Nitro V15', 'Available', 'Acer', 'Intel i7 13th Gen', '16GB', 26500000, 8, 1, 'asus-gaming.jpg', 0),
    ( 'L004', 'Asus Gaming V16', 'Available', 'Asus', 'Intel Core i7', '16GB', 24500000, 7, 1, 'asus0.jpg', 0),
    ('L005', 'Asus TUF Gaming F16', 'Available', 'Asus', 'Intel i7 13620H', '16GB', 28000000, 5, 1, 'hp-15.jpg', 0),
    ( 'L006', 'Asus VivoBook 15', 'Available', 'Asus', 'Intel i5 1240P', '8GB', 15500000, 9, 2, 'hpElite.jpg', 0),
    ( 'L007', 'Asus Zenbook Duo', 'Available', 'Asus', 'Intel i7 1360P', '16GB', 32000000, 4, 2, 'hp-gaming.jpg', 0),
    ( 'L008', 'Dell Inspiron 15', 'Available', 'Dell', 'Intel i5 1335U', '16GB', 17500000, 6, 2, 'hp-omni.jpg', 0),
    ( 'L009', 'Dell G15 Gaming', 'Available', 'Dell', 'Intel i9 13900HX', '32GB', 39000000, 5, 1, 'lenovo-.jpg', 0),
    ( 'L010', 'HP 240 G10', 'Available', 'HP', 'Intel i3 1215U', '8GB', 12000000, 12, 2, 'asus-zenbook.jpg', 0);


INSERT INTO orders (user_id, created_date, order_status, payed, is_archived, shipping_address, shipping_Phone, shipping_Name)
VALUES
    (2, GETDATE(), 'Confirmed', 1, 0, N'123 Lê Lợi, TP.HCM', '0987654321', N'Trần Thị B'),
    (4, GETDATE(), 'Confirmed', 0, 0, N'25 Ngô Gia Tự, Hải Phòng', '0934123456', N'Phạm Thu Dung'),
    (5, GETDATE(), 'Delivered', 1, 0, N'55 Nguyễn Trãi, Cần Thơ', '0945123456', N'Đặng Hữu Nam');

-- ===========================================================
-- 4️⃣ SEED ORDER DETAILS (KHÔNG GHI id)
-- ===========================================================
-- ⚠️ Lưu ý: order_id và laptop_id phải khớp với dữ liệu hiện có.
-- Ví dụ: user_id=2 có laptop_id=1 và 2, user_id=4 có laptop_id=5 và 6, user_id=5 có laptop_id=7 hoặc 8.

INSERT INTO order_details (order_id, laptop_id, quantity, unit_price, total_price)
VALUES
    (1, 9, 1, 39000000, 39000000),   -- đơn hàng 1 mua Dell G15 Gaming
    (1, 6, 1, 15500000, 15500000),   -- đơn hàng 1 mua Asus VivoBook 15
    (2, 2, 2, 15500000, 31000000),   -- đơn hàng 2 mua Acer Aspire Lite
    (3, 7, 1, 32000000, 32000000);   -- đơn hàng 3 mua Asus Zenbook Duo
-- ===========================================================