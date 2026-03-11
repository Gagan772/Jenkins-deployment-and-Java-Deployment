INSERT INTO customer (id, name, email, phone) VALUES (1, 'John Doe', 'john@example.com', '555-1234');
INSERT INTO customer (id, name, email, phone) VALUES (2, 'Jane Smith', 'jane@example.com', '555-5678');
INSERT INTO customer (id, name, email, phone) VALUES (3, 'Bob Johnson', 'bob@example.com', '555-9012');

INSERT INTO employee (id, name, department, salary) VALUES (1, 'Alice Brown', 'Engineering', 85000.00);
INSERT INTO employee (id, name, department, salary) VALUES (2, 'Charlie Wilson', 'Sales', 70000.00);
INSERT INTO employee (id, name, department, salary) VALUES (3, 'Diana Davis', 'HR', 65000.00);

INSERT INTO product (id, name, description, price, stock) VALUES (1, 'Laptop', 'High-performance laptop', 1200.00, 50);
INSERT INTO product (id, name, description, price, stock) VALUES (2, 'Mouse', 'Wireless mouse', 25.00, 200);
INSERT INTO product (id, name, description, price, stock) VALUES (3, 'Keyboard', 'Mechanical keyboard', 80.00, 150);
INSERT INTO product (id, name, description, price, stock) VALUES (4, 'Monitor', '27-inch 4K monitor', 450.00, 75);

INSERT INTO order_entity (id, customer_id, product_id, quantity, total_price, order_date) VALUES (1, 1, 1, 1, 1200.00, '2024-01-15');
INSERT INTO order_entity (id, customer_id, product_id, quantity, total_price, order_date) VALUES (2, 2, 2, 2, 50.00, '2024-01-16');
INSERT INTO order_entity (id, customer_id, product_id, quantity, total_price, order_date) VALUES (3, 1, 3, 1, 80.00, '2024-01-17');
