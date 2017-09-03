package com.sales;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

import static com.sales.JsonUtil.json;
import static spark.Spark.get;
import static spark.Spark.port;

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

            String queryStatement = "SELECT * from Homepage";
            ResultSet resultSet = stmt.executeQuery(queryStatement);

            while(resultSet.next()){
                Product product = new Product();
                product.setProductName(resultSet.getString("Product_Name"));
                product.setBrand(resultSet.getString("Brand"));
                product.setOriginalPrice(resultSet.getString("Original_Price"));
                product.setSalePrice(resultSet.getString("Sale_Price"));
                product.setDiscountPercent(resultSet.getString("Discount_Perc"));
                product.setStoreName(resultSet.getString("Store_Name"));
                product.setStoreLocation(resultSet.getString("Store_Location"));
                product.setEstimatedDistance(resultSet.getString("Est_Distance"));
                Blob imageBlob = resultSet.getBlob("Images");
                if(imageBlob != null) {
                    byte[] imageBytes = imageBlob.getBytes(1, (int) imageBlob.length());
                    product.setImage(imageBytes);
                }
                else{
                    product.setImage(new byte[1024]);
                }

                allProducts.add(product);
            }

        }
        catch (Exception exception){
            logger.error(exception.getMessage());
        }

        return allProducts;
    }

    public static void main(String[] args) {

        port(8081);

        get("/products", new Route() {
            public Object handle(Request req, Response res) throws Exception {
                return getProducts();
            }
        }, json());
    }
}
