package org.bitbrothers.shoppi.model;

/**
 * One item/entry of a shopping list.
 */
public class ShoppingItem {

    private final Long id;
    private final String name;
    private final boolean bought;

    public ShoppingItem(String name) {
        this.id = null;
        this.name = name;
        this.bought = false;
    }

    public ShoppingItem(long id, String name, boolean bought) {
        this.id = id;
        this.name = name;
        this.bought = bought;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isBought() {
        return bought;
    }

    public ShoppingItem markBought() {
        return new ShoppingItem(id, name, true);
    }

    public ShoppingItem markUnbought() {
        return new ShoppingItem(id, name, false);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ShoppingItem that = (ShoppingItem) o;

        if (bought != that.bought) {
            return false;
        }
        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        return name != null ? name.equals(that.name) : that.name == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (bought ? 1 : 0);
        return result;
    }
}
