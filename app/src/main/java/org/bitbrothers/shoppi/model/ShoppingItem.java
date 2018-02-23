package org.bitbrothers.shoppi.model;

/**
 * One item/entry of a shopping list.
 */
public class ShoppingItem {

    private final Long id;
    private final String name;

    public ShoppingItem(String name) {
        this.id = null;
        this.name = name;
    }

    public ShoppingItem(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
