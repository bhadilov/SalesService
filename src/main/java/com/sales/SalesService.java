package com.sales;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.annotation.PostConstruct;
import javax.xml.ws.spi.http.HttpExchange;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

import static com.sales.JsonUtil.json;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;


public class SalesService {
    final static Logger logger = LoggerFactory.getLogger(SalesService.class);

    private static String[] getDBDetails() {
        try {
            Properties props = new Properties();
            String configFile = System.getProperty("user.dir") + "/config/config.properties";
            InputStream in = new FileInputStream(configFile);
            props.load(in);
            in.close();

            String[] dbDetails = new String[4];
            dbDetails[0] = props.get("SALES_DB_URL").toString();
            dbDetails[1] = props.get("SALES_DB_USERNAME").toString();
            dbDetails[2] = props.get("SALES_DB_PASSWORD").toString();
            dbDetails[3] = props.get("MY_SQL_URL").toString();


            return dbDetails;
        }
        catch (Exception exception){
            logger.error(exception.getMessage());
            return new String[] {"", "", "", ""};
        }
    }



    private static ArrayList<Product> getProducts(){
        Connection conn = null;
        Statement stmt = null;
        String[] dbDetails = getDBDetails();
        ArrayList<Product> allProducts = new ArrayList<Product>();

        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(dbDetails[0], dbDetails[1], dbDetails[2]);
            stmt = conn.createStatement();

            String queryStatement = "SELECT Product_Name, Brand, Discount_Perc, Images from SALES.Clothes";
            ResultSet resultSet = stmt.executeQuery(queryStatement);

            while(resultSet.next()){
                Product product = new Product();
                product.setProductName(resultSet.getString("Product_Name"));
                //product.setBrand(resultSet.getString("Brand"));
//                product.setOriginalPrice(resultSet.getString("Original_Price"));
//                product.setSalePrice(resultSet.getString("Sale_Price"));
                product.setDiscountPercent(resultSet.getString("Discount_Perc"));
//                product.setStoreName(resultSet.getString("Store_Name"));
//                product.setStoreLocation(resultSet.getString("Store_Location"));
//                product.setEstimatedDistance(resultSet.getString("Est_Distance"));
                Blob imageBlob = resultSet.getBlob("Images");

                if(imageBlob != null) {
                    byte[] imageBytes = imageBlob.getBytes(1, (int) imageBlob.length());
                    product.setImage(imageBytes);
                }
                else{product.setImage(new byte[1024]);
                }

                allProducts.add(product);
            }
        }
        catch (Exception exception){
            logger.error(exception.getMessage());
        }

        return allProducts;
    }

    private static ArrayList<Clothes> getClothes(){
        Connection conn = null;
        Statement stmt = null;
        String[] dbDetails = getDBDetails();
        ArrayList<Clothes> allClothes = new ArrayList<Clothes>();

        try{
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(dbDetails[0], dbDetails[1], dbDetails[2]);
            stmt = conn.createStatement();

            String queryStatement = "SELECT Sales_Product_name, Sales_brand_Type_id, Sales_Original_Price, Sales_New_Price, Sale_Image FROM SALES.Sales_Brand where Sales_Brand_Category_id = 1";
            ResultSet resultSet = stmt.executeQuery(queryStatement);

            while(resultSet.next()){
                Clothes clothes = new Clothes();
                clothes.setSalesProductName(resultSet.getString("Sales_Product_name"));
                clothes.setSalesBrandType(resultSet.getString("Sales_brand_Type_id"));
                clothes.setSaleOriginalPrice(resultSet.getString("Sales_Original_Price"));
                clothes.setSaleNewPrice(resultSet.getString("Sales_New_Price"));
                clothes.setSaleImage(resultSet.getString("Sale_Image"));
                allClothes.add(clothes);
            }

        }
        catch (Exception exception){
            logger.error(exception.getMessage());
        }
        return allClothes;
    }

    private static ArrayList<Categories> getAllCategoryProducts(){
        Connection conn;
        Statement stmt;
        String[] dbDetails = getDBDetails();

        ArrayList<Categories> allCategoryProducts= new ArrayList<Categories>();

        try{
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(dbDetails[0], dbDetails[1], dbDetails[2]);
            stmt = conn.createStatement();
            String queryStatement = "SELECT sb.Sales_Brands_id, sb.Sales_Product_name, sbt.Sales_Brand_Type_name, sc.Sales_Category_id, sc.Sales_Category_name, sbn.Sales_Brands_Name, sb.Sales_Original_Price, sb.Sales_New_Price, sb.Sale_Image,sd.Sale_Malls_Name " +
                    "FROM SALES.Sales_Brand sb INNER JOIN SALES.Sales_Brand_Type sbt ON sb.Sales_brand_Type_id = sbt.Sales_Brand_Type_id " +
                    "INNER JOIN SALES.Sales_Malls_Location sd ON sb.Sales_Malls_Location_id = sd.Sales_Malls_Location_id " +
                    "INNER JOIN SALES.Sales_Category sc ON sb.Sales_Brand_Category_id = sc.Sales_Category_id " +
                    "INNER JOIN SALES.Sales_Brands_Name sbn ON sb.Sales_Brand_Name_id = sbn.Sales_Brands_Name_id " +
                    "ORDER BY sc.Sales_Category_name, sbt.Sales_Brand_Type_name  Desc;";
            ResultSet resultSet = stmt.executeQuery(queryStatement);

            while(resultSet.next()){
                Categories categories = new Categories();
                categories.setSalesBrandID(resultSet.getInt("Sales_Brands_id"));
                categories.setSalesProductName(resultSet.getString("Sales_Product_name"));
                categories.setSalesBrandTypeName(resultSet.getString("Sales_Brand_Type_name"));
                categories.setSalesCategoryName(resultSet.getString("Sales_Category_name"));
                categories.setSalesBrandsName(resultSet.getString("Sales_Brands_Name"));
                categories.setSaleOriginalPrice(resultSet.getInt("Sales_Original_Price"));
                categories.setSaleNewPrice(resultSet.getInt("Sales_New_Price"));
                categories.setSaleCategoryID(resultSet.getInt("Sales_Category_id"));
                categories.setSaleImage(resultSet.getString("Sale_Image"));
                categories.setSaleMallName(resultSet.getString("Sale_Malls_Name"));
                allCategoryProducts.add(categories);

            }

        }
        catch (Exception exception){
            logger.error(exception.getMessage());
        }
        return allCategoryProducts;
    }

    private static ArrayList<Category> getCategories(){
        Connection conn;
        Statement stmt;
        String[] dbDetails = getDBDetails();

        ArrayList<Category> categories= new ArrayList<Category>();

        try{
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(dbDetails[0], dbDetails[1], dbDetails[2]);
            stmt = conn.createStatement();
            String queryStatement = "SELECT Sales_Category_id, Sales_Category_name FROM SALES.Sales_Category";
            ResultSet resultSet = stmt.executeQuery(queryStatement);

            while(resultSet.next()){
                Category category = new Category();
                category.setSalesCategoryId(resultSet.getInt("Sales_Category_id"));
                category.setSalesCategoryName(resultSet.getString("Sales_Category_name"));

                categories.add(category);
            }

        }
        catch (Exception exception){
            logger.error(exception.getMessage());
        }
        return categories;
    }

    private static ArrayList<Product> searchForProductsByName(String searchProduct){
        Connection conn = null;
        Statement stmt = null;
        String[] dbDetails = getDBDetails();
        ArrayList<Product> allProducts = new ArrayList<Product>();

        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(dbDetails[0], dbDetails[1], dbDetails[2]);
            stmt = conn.createStatement();

            String queryStatement = "SELECT Sales_Brands_id,Sales_Product_name, Sales_New_Price FROM SALES.Sales_Brand where Sales_Product_name like '%" + searchProduct + "%'";
            ResultSet resultSet = stmt.executeQuery(queryStatement);

            while(resultSet.next()){
                Product product = new Product();
                product.setProductName(resultSet.getString("Sales_Product_name"));
                product.setBrandsID(resultSet.getString("Sales_Brands_id"));
                product.setNewPrice(resultSet.getString("Sales_New_Price"));

               // Blob imageBlob = resultSet.getBlob("Images");

//                if(imageBlob != null) {
//                    byte[] imageBytes = imageBlob.getBytes(1, (int) imageBlob.length());
//                    product.setImage(imageBytes);
//                }
//                else{product.setImage(new byte[1024]);
//                }

                allProducts.add(product);
            }
        }
        catch (Exception exception){
            logger.error(exception.getMessage());
        }

        return allProducts;
    }

    private static ArrayList<Categories> searchForProductByID(String productID){
        Connection conn;
        Statement stmt;
        String[] dbDetails = getDBDetails();

        ArrayList<Categories> allCategoryProducts= new ArrayList<Categories>();

        try{
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(dbDetails[0], dbDetails[1], dbDetails[2]);
            stmt = conn.createStatement();
            String queryStatement = "SELECT sb.Sales_Brands_id, sb.Sales_Product_name, sbt.Sales_Brand_Type_name, sc.Sales_Category_id, sc.Sales_Category_name, sbn.Sales_Brands_Name, sb.Sales_Original_Price, sb.Sales_New_Price,sb.Sale_Image,sd.Sale_Malls_Name " +
                    "FROM SALES.Sales_Brand sb INNER JOIN SALES.Sales_Brand_Type sbt ON sb.Sales_brand_Type_id = sbt.Sales_Brand_Type_id " +
                    "INNER JOIN SALES.Sales_Malls_Location sd ON sb.Sales_Malls_Location_id = sd.Sales_Malls_Location_id " +
                    "INNER JOIN SALES.Sales_Category sc ON sb.Sales_Brand_Category_id = sc.Sales_Category_id " +
                    "INNER JOIN SALES.Sales_Brands_Name sbn ON sb.Sales_Brand_Name_id = sbn.Sales_Brands_Name_id " +
                    "where sb.Sales_Brands_id = " + productID + " " +
                    "ORDER BY sc.Sales_Category_name, sbt.Sales_Brand_Type_name  Desc;";
            ResultSet resultSet = stmt.executeQuery(queryStatement);

            while(resultSet.next()){
                Categories categories = new Categories();
                categories.setSalesBrandID(resultSet.getInt("Sales_Brands_id"));
                categories.setSalesProductName(resultSet.getString("Sales_Product_name"));
                categories.setSalesBrandTypeName(resultSet.getString("Sales_Brand_Type_name"));
                categories.setSalesCategoryName(resultSet.getString("Sales_Category_name"));
                categories.setSalesBrandsName(resultSet.getString("Sales_Brands_Name"));
                categories.setSaleOriginalPrice(resultSet.getInt("Sales_Original_Price"));
                categories.setSaleNewPrice(resultSet.getInt("Sales_New_Price"));
                categories.setSaleCategoryID(resultSet.getInt("Sales_Category_id"));
                categories.setSaleImage(resultSet.getString("Sale_Image"));
                categories.setSaleMallName(resultSet.getString("Sale_Malls_Name"));
                allCategoryProducts.add(categories);

            }

        }
        catch (Exception exception){
            logger.error(exception.getMessage());
        }
        return allCategoryProducts;
    }

    public static void main(String[] args) {

        //port(8081);
        port(1234);

        get("/category", new Route() {
            public Object handle(Request req, Response res) throws Exception {
                return getCategories();
            }
        }, json());

        get("/products", new Route() {
            public Object handle(Request req, Response res) throws Exception {
                return getProducts();
            }
        }, json());


        get("/clothes", new Route() {
            public Object handle(Request req, Response res) throws Exception {
                return "Test " + req;
            }
        }, json());

        get("/allCategoryProducts", new Route() {
            public Object handle(Request req, Response res) throws Exception {
                return getAllCategoryProducts();
            }
        }, json());

        post("/searchProducts/:searchProduct", new Route() {
            public Object handle(Request req, Response res) throws Exception {

                return searchForProductsByName(req.params(":searchProduct"));
            }
        }, json());

        post("/getProductsByID/:productID", new Route() {
            public Object handle(Request req, Response res) throws Exception {

                return searchForProductByID(req.params(":productID"));
            }
        }, json());

        post("/:search", new Route() {
            public Object handle(Request req, Response res) throws Exception {

                return "Test " + req.params(":search");
            }
        }, json());

        post("getProductByID/:SalesBrandID", new Route() {
            public Object handle(Request req, Response res) throws Exception {

                return "Test " + req.params(":search");
            }
        }, json());




    }
}

