package com.sales;

public class Categories {

    private String ProductName, BrandTypeName, CategoryName, BrandsName;
    int OriginalPrice, NewPrice, CategoryID;

    public void setSalesProductName(String salesProductName) {
        this.ProductName = salesProductName;
    }

    public void setSalesBrandTypeName(String salesBrandType) {
        this.BrandTypeName = salesBrandType;
    }

    public void setSalesCategoryName(String salesCategoryName) {
        this.CategoryName = salesCategoryName;
    }

    public void setSalesBrandsName(String salesBrandsName) {
        this.BrandsName = salesBrandsName;
    }

    public void setSaleOriginalPrice(int saleOriginalPrice) {
        this.OriginalPrice = saleOriginalPrice;
    }

    public void setSaleNewPrice(int saleNewPrice) { this.NewPrice = saleNewPrice;}

    public void setSaleCategoryID(int salesCategoryID) { this.CategoryID = salesCategoryID;}

}
