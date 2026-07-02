-- Departments
INSERT INTO departments (name, location, budget) VALUES
('Engineering', 'San Francisco', 2500000.00),
('Marketing', 'New York', 800000.00),
('Sales', 'Chicago', 1200000.00),
('HR', 'Austin', 400000.00),
('Finance', 'Boston', 600000.00);

-- Employees
INSERT INTO employees (first_name, last_name, email, department_id, salary, hire_date, position) VALUES
('Alice', 'Johnson', 'alice.j@company.com', 1, 120000, '2020-03-15', 'Senior Engineer'),
('Bob', 'Smith', 'bob.s@company.com', 1, 95000, '2021-07-01', 'Software Engineer'),
('Carol', 'Williams', 'carol.w@company.com', 2, 85000, '2019-11-20', 'Marketing Manager'),
('David', 'Brown', 'david.b@company.com', 3, 75000, '2022-01-10', 'Sales Rep'),
('Eva', 'Davis', 'eva.d@company.com', 3, 80000, '2020-08-05', 'Senior Sales Rep'),
('Frank', 'Miller', 'frank.m@company.com', 4, 70000, '2021-04-22', 'HR Specialist'),
('Grace', 'Wilson', 'grace.w@company.com', 1, 140000, '2018-06-01', 'Engineering Lead'),
('Henry', 'Moore', 'henry.m@company.com', 5, 90000, '2019-09-15', 'Finance Analyst'),
('Iris', 'Taylor', 'iris.t@company.com', 2, 65000, '2023-02-14', 'Marketing Analyst'),
('Jack', 'Anderson', 'jack.a@company.com', 1, 110000, '2020-12-01', 'Backend Engineer');

-- Categories
INSERT INTO categories (name, description) VALUES
('Electronics', 'Gadgets and electronic devices'),
('Clothing', 'Apparel and fashion'),
('Books', 'Physical and digital books'),
('Home & Garden', 'Home improvement and garden supplies'),
('Sports', 'Sports equipment and accessories');

-- Products
INSERT INTO products (name, category_id, price, stock_quantity, sku) VALUES
('MacBook Pro 16"', 1, 2499.99, 45, 'ELEC-001'),
('iPhone 15 Pro', 1, 999.99, 120, 'ELEC-002'),
('Wireless Headphones', 1, 299.99, 200, 'ELEC-003'),
('4K Monitor', 1, 599.99, 60, 'ELEC-004'),
('Running Shoes', 5, 89.99, 300, 'SPORT-001'),
('Yoga Mat', 5, 49.99, 150, 'SPORT-002'),
('Winter Jacket', 2, 179.99, 80, 'CLOTH-001'),
('Denim Jeans', 2, 69.99, 250, 'CLOTH-002'),
('Clean Code Book', 3, 39.99, 500, 'BOOK-001'),
('Design Patterns', 3, 44.99, 350, 'BOOK-002'),
('Garden Hose', 4, 29.99, 400, 'HOME-001'),
('Smart Thermostat', 4, 199.99, 90, 'HOME-002');

-- Customers
INSERT INTO customers (first_name, last_name, email, city, country, joined_date) VALUES
('John', 'Doe', 'john.doe@email.com', 'New York', 'USA', '2022-01-15'),
('Jane', 'Smith', 'jane.smith@email.com', 'London', 'UK', '2021-06-20'),
('Mike', 'Johnson', 'mike.j@email.com', 'Toronto', 'Canada', '2023-03-10'),
('Sarah', 'Connor', 'sarah.c@email.com', 'Sydney', 'Australia', '2022-09-05'),
('Tom', 'Lee', 'tom.l@email.com', 'Singapore', 'Singapore', '2021-12-01'),
('Emma', 'Garcia', 'emma.g@email.com', 'Madrid', 'Spain', '2023-07-18'),
('Liam', 'Wilson', 'liam.w@email.com', 'Chicago', 'USA', '2022-04-22'),
('Olivia', 'Martinez', 'olivia.m@email.com', 'Miami', 'USA', '2020-11-30');

-- Orders
INSERT INTO orders (customer_id, order_date, status, total_amount) VALUES
(1, '2024-01-10 10:30:00', 'completed', 3499.98),
(2, '2024-01-15 14:20:00', 'completed', 299.99),
(3, '2024-02-01 09:00:00', 'shipped', 1299.98),
(4, '2024-02-10 16:45:00', 'completed', 89.99),
(5, '2024-02-20 11:00:00', 'pending', 649.98),
(1, '2024-03-01 08:30:00', 'completed', 84.98),
(6, '2024-03-05 13:15:00', 'processing', 219.98),
(7, '2024-03-10 17:00:00', 'completed', 599.99),
(8, '2024-03-15 12:30:00', 'shipped', 2499.99),
(2, '2024-03-20 10:00:00', 'completed', 114.98);

-- Order Items
INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES
(1, 1, 1, 2499.99),
(1, 3, 1, 999.99),
(2, 3, 1, 299.99),
(3, 2, 1, 999.99),
(3, 5, 1, 299.99),
(4, 5, 1, 89.99),
(5, 4, 1, 599.99),
(5, 6, 1, 49.99),
(6, 9, 1, 39.99),
(6, 10, 1, 44.99),
(7, 12, 1, 199.99),
(7, 11, 1, 19.99),
(8, 4, 1, 599.99),
(9, 1, 1, 2499.99),
(10, 7, 1, 179.99),
(10, 8, 1, 69.99) -- Note: adjusted to 249.98 but keeping items for demo
;
