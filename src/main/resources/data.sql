-- Отключаем проверку внешних ключей, чтобы вставлять данные в любом порядке
SET CONSTRAINTS ALL DEFERRED;

-- Очищаем таблицы перед заполнением (опционально, но полезно для перезапуска)
DELETE FROM order_items;
DELETE FROM payments;
DELETE FROM orders;
DELETE FROM products;
DELETE FROM users;

-- Сбрасываем счетчики автоинкремента для чистоты
ALTER SEQUENCE users_id_seq RESTART WITH 1;
ALTER SEQUENCE products_id_seq RESTART WITH 1;
ALTER SEQUENCE orders_id_seq RESTART WITH 1;
ALTER SEQUENCE order_items_id_seq RESTART WITH 1;
ALTER SEQUENCE payments_id_seq RESTART WITH 1;


-- 1. Заполняем таблицу пользователей (users)
INSERT INTO users (id, username, first_name, last_name, email, phone_number, password_hash, role, active) VALUES
(1, 'john.doe', 'John', 'Doe', 'john.doe@example.com', '+79991234567', '$2a$10$abcdefghijklmnopqrstuv.w', 'USER', true),
(2, 'jane.smith', 'Jane', 'Smith', 'jane.smith@example.com', '+79997654321', '$2a$10$abcdefghijklmnopqrstuv.x', 'USER', true),
(3, 'admin.user', 'Admin', 'User', 'admin@example.com', '+79000000000', '$2a$10$abcdefghijklmnopqrstuv.y', 'ADMIN', true);

-- 2. Заполняем таблицу продуктов (products)
INSERT INTO products (id, name, price, version, description, stock_quantity, is_available, image_url, created_at, updated_at) VALUES
(101, 'Умная шторка Basic', 4990.00, 'BASIC', 'Базовая модель с управлением со смартфона.', 50, true, '/images/curtain_basic.jpg', NOW(), NOW()),
(102, 'Умная шторка Pro', 7990.00, 'PRO', 'Про-модель с голосовым управлением и датчиком света.', 30, true, '/images/curtain_pro.jpg', NOW(), NOW()),
(103, 'Умная шторка Premium', 11990.00, 'PREMIUM', 'Премиум-модель из эксклюзивных материалов с расширенной гарантией.', 15, true, '/images/curtain_premium.jpg', NOW(), NOW()),
(104, 'Пульт управления', 990.00, 'CONTROLLER', 'Дополнительный пульт для управления шторками.', 100, true, '/images/controller.jpg', NOW(), NOW());

-- 3. Заполняем заказы (orders)
-- Заказ 1: Оплачен и выполнен
INSERT INTO orders (id, user_id, total_price, status, shipping_address, phone_number, created_at, updated_at) VALUES
(1, 1, 13970.00, 'DELIVERED', 'г. Москва, ул. Тверская, д. 5, кв. 12', '+79991234567', NOW() - INTERVAL '3 day', NOW() - INTERVAL '1 day');

-- Заказ 2: В ожидании оплаты
INSERT INTO orders (id, user_id, total_price, status, shipping_address, phone_number, created_at, updated_at) VALUES
(2, 2, 7990.00, 'PENDING', 'г. Санкт-Петербург, Невский пр-т, д. 28, кв. 101', '+79997654321', NOW(), NOW());


-- 4. Заполняем позиции заказов (order_items)
-- Позиции для Заказа 1
INSERT INTO order_items (id, order_id, product_id, product_name, quantity, price) VALUES
(1, 1, 101, 'Умная шторка Basic', 1, 4990.00),
(2, 1, 102, 'Умная шторка Pro', 1, 7990.00),
(3, 1, 104, 'Пульт управления', 1, 990.00);

-- Позиции для Заказа 2
INSERT INTO order_items (id, order_id, product_id, product_name, quantity, price) VALUES
(4, 2, 102, 'Умная шторка Pro', 1, 7990.00);

-- 5. Заполняем платежи (payments) - (Опционально, т.к. создаются при оплате, но для тестов полезно)
-- Платеж для Заказа 1
INSERT INTO payments (id, order_id, amount, payment_date, payment_method, status, transaction_id) VALUES
(1, 1, 13970.00, NOW() - INTERVAL '3 day', 'YOOKASSA', 'SUCCEEDED', '2d84c67d-000f-5000-8000-11e9389e46f6');
-- Платеж для Заказа 2 еще не создан, так как статус PENDING

SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
SELECT setval('products_id_seq', (SELECT MAX(id) FROM products));
SELECT setval('orders_id_seq', (SELECT MAX(id) FROM orders));
SELECT setval('order_items_id_seq', (SELECT MAX(id) FROM order_items));
SELECT setval('payments_id_seq', (SELECT MAX(id) FROM payments));

-- Включаем проверку внешних ключей обратно
SET CONSTRAINTS ALL IMMEDIATE;
