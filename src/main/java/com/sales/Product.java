package com.sales;

public class Product {
    private String ProductName,BrandsID , NewPrice, salePrice, discountPercent, storeName, storeLocation, estimatedDistance, saleImage;
    private byte[] image;

    public void setProductName(String ProductName) {this.ProductName = ProductName; }

    public void setBrandsID(String BrandsID) {
        this.BrandsID = BrandsID;
    }

    public void setNewPrice(String NewPrice) { this.NewPrice = NewPrice; }




    public void setSalePrice(String salePrice) {
        this.salePrice = salePrice;
    }

    public void setDiscountPercent(String discountPercent) {
        this.discountPercent = discountPercent;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public void setStoreLocation(String storeLocation) {
        this.storeLocation = storeLocation;
    }

    public void setEstimatedDistance(String estimatedDistance) {
        this.estimatedDistance = estimatedDistance;
    }

    public void setSaleImage(String saleImage) {this.saleImage = saleImage; }

    public void setImage(byte[] image) {
        this.image = image;
    }

}



