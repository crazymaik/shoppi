package org.bitbrothers.shoppi.model;

public final class Category {

    private final Long id;
    private final String name;
    private final int color;

    public Category(String name, int color) {
        this.id = null;
        this.name = name;
        this.color = color | 0xff000000;
    }

    public Category(Long id, String name, int color) {
        this.id = id;
        this.name = name;
        this.color = color | 0xff000000;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getColor() {
        return color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Category category = (Category) o;

        if (color != category.color) {
            return false;
        }
        if (id != null ? !id.equals(category.id) : category.id != null) {
            return false;
        }
        return name.equals(category.name);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + name.hashCode();
        result = 31 * result + color;
        return result;
    }
}
