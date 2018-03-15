delete from shopping_items;
delete from categories;

insert into categories (id, name, color) VALUES
    (1, 'Beverages', 0xffcc5151),
    (2, 'Bread & Bakery', 0xffe57e16),
    (3, 'Canned Goods & Soups', 0xff8ecc51),
    (4, 'Condiments, Spices & Bake', 0xff16e516),
    (5, 'Dairy, Eggs & Cheese', 0xff16e5e5),
    (6, 'Frozen Foods', 0xff5151cc),
    (7, 'Fruits & Vegetables', 0xff1616e5),
    (8, 'Grains, Pasta & Sides', 0xff7e16e5),
    (9, 'Meat & Seafood', 0xffe516e5),
    (10, 'Personal Care & Health', 0xffe5167e);

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

