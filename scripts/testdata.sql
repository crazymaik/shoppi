delete from shopping_items;
delete from categories;

insert into categories (id, name, color) VALUES
    (1, 'Beverages', 0xffe51616),
    (2, 'Bread & Bakery', 0xffe57e16),
    (3, 'Candy & Snacks', 0xffe5e516),
    (4, 'Canned Goods & Soups', 0xff7ee516),
    (5, 'Cleaning & Home', 0xff16e516),
    (6, 'Condiments, Spices & Bake', 0xff16e57e),
    (7, 'Dairy, Eggs & Cheese', 0xff16e5e5),
    (8, 'Frozen Foods', 0xff167ee5),
    (9, 'Fruits & Vegetables', 0xff1616e5),
    (10, 'Grains, Pasta & Sides', 0xff7e16e5),
    (11, 'Meat & Seafood', 0xffe516e5),
    (12, 'Personal Care & Health', 0xffe5167e);

insert into shopping_items (name, bought, category_id) VALUES
    ('Soda', 0, 1),
    ('Water', 0, 1),
    ('Bagels', 0, 2),
    ('Sandwich', 0, 2),
    ('Chips', 0, 3),
    ('Ice cream', 0, 3),
    ('Canned Peaches', 0, 4),
    ('Milk', 0, 7),
    ('Apples', 0, 9),
    ('Onions', 0, 9),
    ('Oranges', 0, 9),
    ('Tomatoes', 0, 9),
    ('Salmon', 0, 11),
    ('Toothbrush', 0, 12),
    ('Toothpaste', 0, 12);

