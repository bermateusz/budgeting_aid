CREATE TABLE budget_category
(
id SERIAL PRIMARY KEY,
name VARCHAR(75) NOT NULL,
amount DECIMAL(16,2) NOT NULL
);