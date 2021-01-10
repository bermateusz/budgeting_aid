INSERT INTO budget_category(name, amount) SELECT * FROM (
SELECT 'Wallet', 1000 UNION
SELECT 'Savings', 5000 UNION
SELECT 'Insurance policy', 0 UNION
SELECT 'Food expenses', 0
) x WHERE NOT EXISTS(SELECT * FROM budget_category);