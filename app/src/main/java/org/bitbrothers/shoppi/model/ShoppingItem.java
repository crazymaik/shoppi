package org.bitbrothers.shoppi.model;

/**
 * One item/entry of a shopping list.
 */
public final class ShoppingItem {

    private final Long id;
    private final String name;
    private final boolean bought;
    private final Long categoryId;
    private final int color;

    public ShoppingItem(String name) {
        this.id = null;
        this.name = name;
        this.bought = false;
        this.categoryId = null;
        this.color = 0;
    }

    public ShoppingItem(long id, String name, boolean bought, Long categoryId, Integer color) {
        this.id = id;
        this.name = name;
        this.bought = bought;
        this.categoryId = categoryId;
        this.color = color != null ? color : 0;
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
        return new ShoppingItem(id, name, true, categoryId, color);
    }

    public ShoppingItem markUnbought() {
        return new ShoppingItem(id, name, false, categoryId, color);
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
        if (color != that.color) {
            return false;
        }
        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (!name.equals(that.name)) {
            return false;
        }
        return categoryId != null ? categoryId.equals(that.categoryId) : that.categoryId == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + name.hashCode();
        result = 31 * result + (bought ? 1 : 0);
        result = 31 * result + (categoryId != null ? categoryId.hashCode() : 0);
        result = 31 * result + color;
        return result;
    }

    public int getColor() {
        return color;
    }
}
