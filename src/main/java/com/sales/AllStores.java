package com.sales;

public class AllStores {

    private String CategoryID, CategoryName, Title,TitleUrl, ImageUrl, NewPrice, OldPrice, Discount;

    public void setCategoryID(String salesCategoryID) {
        this.CategoryID = salesCategoryID;
    }

    public void setCategoryName(String salesCategoryName) {
        this.CategoryName = salesCategoryName;
    }

    public void setTitle(String salesTitle) {
        this.Title = salesTitle;
    }

    public void setTitleUrl(String salesTitle) {
        this.TitleUrl = salesTitle;
    }

    public void setImageUrl(String saleImageUrl) {
        this.ImageUrl = saleImageUrl;
    }

    public void setNewPrice(String saleNewPrice) {
        this.NewPrice = saleNewPrice;
    }

    public void setOldPrice(String saleOldPrice) {
        this.OldPrice = saleOldPrice;
    }

    public void setDiscount(String saleDiscount) {
        this.Discount = saleDiscount;
    }

}
