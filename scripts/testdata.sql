delete from shopping_items;
delete from categories;

insert into categories (id, name, color) VALUES
    (1, 'Beverages', 0xFFDC0D0D),
    (2, 'Bread & Bakery', 0xFFDC6318),
    (3, 'Canned Goods & Soups', 0xFFDCD61A),
    (4, 'Condiments, Spices & Bake', 0xFF7BDC1A),
    (5, 'Dairy, Eggs & Cheese', 0xFF1ADC61),
    (6, 'Frozen Foods', 0xFF18DCD2),
    (7, 'Fruits & Vegetables', 0xFF188BDC),
    (8, 'Grains, Pasta & Sides', 0xFF1644DC),
    (9, 'Meat & Seafood', 0xFF7718DC),
    (10, 'Personal Care & Health', 0xFFD618DC);

insert into shopping_items (name, bought, category_id) VALUES
    ('Diet Pepsi', 0, 1),
    ('Water', 0, 1),
    ('Bagels', 0, 2),
    ('Sandwich', 0, 2),
    ('Canned Peaches', 0, 3),
    ('Milk', 0, 5),
    ('Apples', 0, 7),
    ('Onions', 0, 7),
    ('Oranges', 0, 7),
    ('Salad', 0, 7),
    ('Tomates', 0, 7),
    ('Salmon', 0, 9),
    ('Thin-sliced pork', 0, 9),
    ('Shave gel', 0, 10),
    ('Toothbrush', 0, 10),
    ('Toothpaste', 0, 10);

