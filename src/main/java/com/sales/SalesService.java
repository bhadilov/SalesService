package com.sales;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;

//import javax.annotation.PostConstruct;
//import javax.xml.ws.spi.http.HttpExchange;
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

            String queryStatement = "SELECT Sale_Image,Sales_Brands_id,Sales_Product_name, Sales_New_Price FROM SALES.Sales_Brand where Sales_Product_name like '%" + searchProduct + "%'";
            ResultSet resultSet = stmt.executeQuery(queryStatement);

            while(resultSet.next()){
                Product product = new Product();
                product.setProductName(resultSet.getString("Sales_Product_name"));
                product.setBrandsID(resultSet.getString("Sales_Brands_id"));
                product.setNewPrice(resultSet.getString("Sales_New_Price"));
                product.setSaleImage(resultSet.getString("Sale_Image"));


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

    private static ArrayList<Recommend> getRecommendations(){
        Connection conn;
        Statement stmt;
        String[] dbDetails = getDBDetails();

        ArrayList<Recommend> recommendations= new ArrayList<Recommend>();

        try{
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(dbDetails[0], dbDetails[1], dbDetails[2]);
            stmt = conn.createStatement();
            String queryStatement = "SELECT sb.Sales_Brands_id, sb.Sales_Product_name, sbt.Sales_Brand_Type_name, sc.Sales_Category_id, sc.Sales_Category_name, sbn.Sales_Brands_Name, sb.Sales_Original_Price, sb.Sales_New_Price, sb.Sale_Image,sd.Sale_Malls_Name " +
                    "FROM SALES.Sales_Brand sb INNER JOIN SALES.Sales_Brand_Type sbt ON sb.Sales_brand_Type_id = sbt.Sales_Brand_Type_id " +
                    "INNER JOIN SALES.Sales_Malls_Location sd ON sb.Sales_Malls_Location_id = sd.Sales_Malls_Location_id " +
                    "INNER JOIN SALES.Sales_Category sc ON sb.Sales_Brand_Category_id = sc.Sales_Category_id " +
                    "INNER JOIN SALES.Sales_Brands_Name sbn ON sb.Sales_Brand_Name_id = sbn.Sales_Brands_Name_id " +
                    "ORDER BY sb.Sales_New_Price desc, sb.Sales_Product_name desc";
            ResultSet resultSet = stmt.executeQuery(queryStatement);

            while(resultSet.next()){
                Recommend recommend = new Recommend();
                recommend.setSalesBrandID(resultSet.getInt("Sales_Brands_id"));
                recommend.setSalesProductName(resultSet.getString("Sales_Product_name"));
                recommend.setSalesBrandTypeName(resultSet.getString("Sales_Brand_Type_name"));
                recommend.setSalesCategoryName(resultSet.getString("Sales_Category_name"));
                recommend.setSalesBrandsName(resultSet.getString("Sales_Brands_Name"));
                recommend.setSaleOriginalPrice(resultSet.getInt("Sales_Original_Price"));
                recommend.setSaleNewPrice(resultSet.getInt("Sales_New_Price"));
                recommend.setSaleCategoryID(resultSet.getInt("Sales_Category_id"));
                recommend.setSaleImage(resultSet.getString("Sale_Image"));
                recommend.setSaleMallName(resultSet.getString("Sale_Malls_Name"));
                recommendations.add(recommend);

            }

        }
        catch (Exception exception){
            logger.error(exception.getMessage());
        }
        return recommendations;
    }

    private static ArrayList<AllStores> getAllStores(){
        Connection conn = null;
        Statement stmt = null;
        String[] dbDetails = getDBDetails();
        ArrayList<AllStores> all_Stores = new ArrayList<AllStores>();

        try{
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(dbDetails[0], dbDetails[1], dbDetails[2]);
            stmt = conn.createStatement();

            String queryStatement = "SELECT sl.Title,sl.TitleUrl, sc.Sales_Category_name, sl.NewPrice, " +
                                           "sl.OldPrice, sl.Discount, sl.ImageUrl " +
                                    "FROM sales.all_stores sl " +
                                    "INNER JOIN sales.sales_category sc " +
                                    "ON sl.sales_category_id = sc.Sales_Category_id";
            ResultSet resultSet = stmt.executeQuery(queryStatement);

            while(resultSet.next()){
                AllStores allStores = new AllStores();
                allStores.setTitle(resultSet.getString("Title"));
                allStores.setTitleUrl(resultSet.getString("TitleUrl"));
                allStores.setImageUrl(resultSet.getString("ImageUrl"));
                allStores.setNewPrice(resultSet.getString("NewPrice"));
                allStores.setOldPrice(resultSet.getString("OldPrice"));
                allStores.setDiscount(resultSet.getString("Discount"));
                all_Stores.add(allStores);
            }

        }
        catch (Exception exception){
            logger.error(exception.getMessage());
        }
        return all_Stores;
    }

    private static ArrayList<AllStores> getAllStoreCategories(String categoryNumber){
        Connection conn = null;
        Statement stmt = null;
        String[] dbDetails = getDBDetails();
        ArrayList<AllStores> all_StoresCategories = new ArrayList<AllStores>();

        try{
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(dbDetails[0], dbDetails[1], dbDetails[2]);
            stmt = conn.createStatement();

            String queryStatement = "SELECT * FROM SALES.all_stores where sales_category_id = " + categoryNumber;
            ResultSet resultSet = stmt.executeQuery(queryStatement);

            while(resultSet.next()){
                AllStores allStoresCat = new AllStores();
                allStoresCat.setTitle(resultSet.getString("Title"));
                allStoresCat.setTitleUrl(resultSet.getString("TitleUrl"));
                allStoresCat.setImageUrl(resultSet.getString("ImageUrl"));
                allStoresCat.setNewPrice(resultSet.getString("NewPrice"));
                allStoresCat.setOldPrice(resultSet.getString("OldPrice"));
                allStoresCat.setDiscount(resultSet.getString("Discount"));

                all_StoresCategories.add(allStoresCat);
            }

        }
        catch (Exception exception){
            logger.error(exception.getMessage());
        }
        return all_StoresCategories;
    }

    public static void main(String[] args) {

        //port(8082);
        port(8082);

        get("/category", new Route() {
            public Object handle(Request req, Response res) throws Exception {
                return getCategories();
            }
        }, json());



//        get("/products", new Route() {
//            public Object handle(Request req, Response res) throws Exception {
//                return getProducts();
//            }
//        }, json());

        get("/all_Stores", new Route() {
            public Object handle(Request req, Response res) throws Exception {
                return getAllStores();
            }
        }, json());

        get("/recommendations", new Route() {
            public Object handle(Request req, Response res) throws Exception {
                return getRecommendations();
            }
        }, json());

        get("/clothes", new Route() {
            public Object handle(Request req, Response res) throws Exception {
                return "Test " + req;
            }
        }, json());

        get("/allCategoryProducts", new Route() {
            public Object handle(Request req, Response res) throws Exception {
                logger.info("Called the method successfully");
                return getAllCategoryProducts();
            }
        }, json());

        post("/searchProducts/:searchProduct", new Route() {
            public Object handle(Request req, Response res) throws Exception {

                return searchForProductsByName(req.params(":searchProduct"));
            }
        }, json());

        post("/getAllStoreCategories/:categoryNumber", new Route() {
            public Object handle(Request req, Response res) throws Exception {

                return getAllStoreCategories(req.params(":categoryNumber"));
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

