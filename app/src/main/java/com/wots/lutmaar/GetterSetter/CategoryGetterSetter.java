package com.wots.lutmaar.GetterSetter;

/**
 * Created by Super Star on 14-08-2015.
 */
public class CategoryGetterSetter {
    String name;
    String term_id;

    public CategoryGetterSetter(String name, String term_id) {
        this.name = name;
        this.term_id = term_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTerm_id() {
        return term_id;
    }

    public void setTerm_id(String term_id) {
        this.term_id = term_id;
    }
}
