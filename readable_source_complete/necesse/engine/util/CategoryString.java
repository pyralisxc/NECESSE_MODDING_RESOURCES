/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util;

public class CategoryString
implements Comparable<CategoryString> {
    public static final String splitterRegex = "-";
    public final String[] categories;
    public final String name;

    public CategoryString(String[] categories, String name) {
        this.categories = categories;
        this.name = name;
    }

    public CategoryString(String fullString) {
        String[] split = fullString.split(splitterRegex);
        this.categories = new String[split.length - 1];
        System.arraycopy(split, 0, this.categories, 0, this.categories.length);
        this.name = split[split.length - 1];
    }

    public CategoryString(String categoryString, String name) {
        this.categories = CategoryString.getCategories(categoryString);
        this.name = name;
    }

    @Override
    public int compareTo(CategoryString b) {
        CategoryString a = this;
        int compare = CategoryString.compareCategories(a.categories, b.categories);
        if (compare == 0) {
            return a.name.compareTo(b.name);
        }
        return compare;
    }

    public String toString() {
        return CategoryString.getCategoryString(this.categories) + splitterRegex + this.name;
    }

    public static int compareCategories(String[] a, String[] b) {
        int compare;
        int index = 0;
        while (true) {
            if (index >= a.length && index < b.length) {
                return -1;
            }
            if (index >= b.length && index < a.length) {
                return 1;
            }
            if (index >= a.length && index >= b.length) {
                return 0;
            }
            String aCat = a[index];
            String bCat = b[index];
            compare = aCat.compareTo(bCat);
            if (compare != 0) break;
            ++index;
        }
        return compare;
    }

    public static String[] getCategories(String categoryString) {
        return categoryString.split(splitterRegex);
    }

    public static String getCategoryString(String[] categories) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < categories.length; ++i) {
            out.append(categories[i]);
            if (i >= categories.length - 1) continue;
            out.append(splitterRegex);
        }
        return out.toString();
    }

    public static CategoryString[] toCategoryStrings(String[] strings) {
        CategoryString[] out = new CategoryString[strings.length];
        for (int i = 0; i < strings.length; ++i) {
            out[i] = new CategoryString(strings[i]);
        }
        return out;
    }
}

