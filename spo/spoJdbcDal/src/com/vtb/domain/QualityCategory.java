package com.vtb.domain;

public class QualityCategory extends VtbObject {
    private static final long serialVersionUID = 1L;
    private Long idCategory;
    private String nameCategory;

    public void setIdCategory(Long idCategory) {
        this.idCategory = idCategory;
    }

    public Long getIdCategory() {
        return idCategory;
    }

    public void setNameCategory(String nameCategory) {
        this.nameCategory = nameCategory;
    }

    public String getNameCategory() {
        return nameCategory;
    }
}
