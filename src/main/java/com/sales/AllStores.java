package com.sales;

public class AllStores {

    private int Prodnum;

    private String productID,CategoryID, CategoryName, Title,TitleUrl, ImageUrl, NewPrice, OldPrice, Store;

    public void setProductID(String salesProductID) {  this.productID = salesProductID; }

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

    public void setStore(String saleStore) {
        this.Store= saleStore;
    }



//    public void setProdnum(String saleProdnum){this.Prodnum =saleProdnum; }

}
