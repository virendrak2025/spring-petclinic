-- Create table: types
CREATE TABLE types (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL
);

-- Create table: pet_type_details
CREATE TABLE pet_type_details (
    id INT PRIMARY KEY AUTO_INCREMENT,
    pet_type_id INT NOT NULL UNIQUE,
    temperament VARCHAR(100),
    length DOUBLE,
    weight DOUBLE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pet_type
        FOREIGN KEY (pet_type_id)
        REFERENCES types(id)
        ON DELETE CASCADE
);

-- Insert sample pet types
INSERT INTO types (name) VALUES ('Dog'), ('Cat'), ('Bird');

-- Insert sample pet type details (matching pet_type_id from types)
INSERT INTO pet_type_details (pet_type_id, temperament, length, weight)
VALUES
    (1, 'Friendly', 30.5, 15.2),
    (2, 'Calm', 20.0, 6.0),
    (3, 'Alert', 10.0, 1.5);
