-- Insert default admin user (password: admin123)
INSERT INTO users (email, password, first_name, last_name, role, active)
VALUES ('admin@example.com', '$2a$10$YourBCryptHashHere', 'Admin', 'User', 'ADMIN', true)
    ON CONFLICT (email) DO NOTHING;

-- Insert sample categories
INSERT INTO categories (name, description, image_url) VALUES
                                                          ('Clothing', 'Fashion and clothing items', 'https://example.com/clothing.jpg'),
                                                          ('Books', 'Books and stationery', 'https://example.com/books.jpg'),
                                                          ('Home & Kitchen', 'Home appliances and kitchen items', 'https://example.com/home.jpg')
    ON CONFLICT (name) DO NOTHING;

-- Insert sample products
INSERT INTO products (name, description, price, discounted_price, quantity, brand, category_id, image_url) VALUES
                                                                                                               ('Samsung Galaxy S23', 'Flagship Samsung phone', 89999.00, 84999.00, 30, 'Samsung', 1, 'https://example.com/s23.jpg'),
                                                                                                               ('Men''s Casual Shirt', 'Cotton casual shirt for men', 1999.00, 1499.00, 100, 'Peter England', 2, 'https://example.com/shirt.jpg'),
                                                                                                               ('Women''s Dress', 'Elegant evening dress', 3999.00, 2999.00, 50, 'Zara', 2, 'https://example.com/dress.jpg'),
                                                                                                               ('Spring Boot in Action', 'Learn Spring Boot framework', 899.00, NULL, 200, 'Manning', 3, 'https://example.com/springboot.jpg'),
                                                                                                               ('Microwave Oven', '20L microwave oven', 8999.00, 7999.00, 25, 'LG', 4, 'https://example.com/microwave.jpg')
    ON CONFLICT DO NOTHING;