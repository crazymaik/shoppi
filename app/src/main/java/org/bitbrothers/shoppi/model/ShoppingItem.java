package org.bitbrothers.shoppi.model;

/**
 * One item/entry of a shopping list.
 */
public final class ShoppingItem {

    private final Long id;
    private final String name;
    private final boolean bought;
    private final Long categoryId;
    private final Integer color;

    public ShoppingItem(String name) {
        this.id = null;
        this.name = name;
        this.bought = false;
        this.categoryId = null;
        this.color = 0;
    }

    public ShoppingItem(String name, Category category) {
        this.id = null;
        this.name = name;
        this.bought = false;
        this.categoryId = category.getId();
        this.color = category.getColor();
    }

    public ShoppingItem(long id, String name, boolean bought, Long categoryId, Integer color) {
        this.id = id;
        this.name = name;
        this.bought = bought;
        this.categoryId = categoryId;
        this.color = color != null ? color | 0xff000000 : null;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getColor() {
        return color;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public boolean isBought() {
        return bought;
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
}
