import java.sql.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;

import java.net.URI;
import java.net.URISyntaxException;

import org.json.JSONObject;
import static spark.Spark.*;
import spark.template.freemarker.FreeMarkerEngine;
import spark.ModelAndView;
import static spark.Spark.get;

import com.heroku.sdk.jdbc.DatabaseUrl;
//import org.json.simple.JSONObject;
import spark.Request;
import spark.Response;

public class Main {

  public static void main(String[] args) {

    port(Integer.valueOf(System.getenv("PORT")));
    staticFileLocation("/public");
//    get("/", (req, res) -> "/index.html");
    get("/", (req, res) -> "please use /index.html to open my website");

    get("/abc", (req, res) -> {
      Connection connection = null;
      Map<String, Object> attributes = new HashMap<>();
      try{
      connection = DatabaseUrl.extract().getConnection();

      Statement stmt = connection.createStatement();

      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS user_information (user_email varchar(100),  user_password  varchar(30),  user_name  varchar(30) )");

      ResultSet rs = stmt.executeQuery("SELECT user_email, user_password FROM user_information");
      ArrayList<String> output = new ArrayList<String>();

    while(rs.next())
    {
       output.add("read user " + "email: " + rs.getString("user_email") + "     password: " + rs.getString("user_password") );

     }
    attributes.put("results",output);
     return new ModelAndView(attributes, "form.ftl");
     } catch (Exception e) {
     attributes.put("message", "There was an error: " + e);
     return new ModelAndView(attributes, "error.ftl");
     } finally {
     if (connection != null) try{connection.close();} catch(SQLException e){}
    }}, new FreeMarkerEngine());

    get("/db", (req, res) -> {
      Connection connection = null;
      Map<String, Object> attributes = new HashMap<>();
      try {
        connection = DatabaseUrl.extract().getConnection();

        Statement stmt = connection.createStatement();
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ticks (tick timestamp)");
        stmt.executeUpdate("INSERT INTO ticks VALUES (now())");
        ResultSet rs = stmt.executeQuery("SELECT tick FROM ticks");

        ArrayList<String> output = new ArrayList<String>();
        while (rs.next()) {
          output.add( "Read from DB: " + rs.getTimestamp("tick"));
        }

        attributes.put("results", output);
        return new ModelAndView(attributes, "db.ftl");
      } catch (Exception e) {
        attributes.put("message", "There was an error: " + e);
        return new ModelAndView(attributes, "error.ftl");
      } finally {
        if (connection != null) try{connection.close();} catch(SQLException e){}
      }
    }, new FreeMarkerEngine());
    
       get("/api/info", (req, res ) ->
                    {
                      Map<String, Object> data = new HashMap<>();
                      data.put("username","William");
                      String xml= "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                                  "<customer>"+
                                  "<user_profile>" +
                                          "<Email>abcd@163.com</Email>"+
                                          "<Sex>Male</Sex>"+
                                          "<Language>Enlgish</Language>"+
                                          "<Birth>January</Birth>"+
                                  "</user_profile>"+
                                  "<user_profile>" +
                                          "<Email>453@163.com</Email>"+
                                          "<Sex>Male</Sex>"+
                                          "<Language>Enlgish</Language>"+
                                          "<Birth>May</Birth>"+
                                  "</user_profile>"+
                                  "<user_profile>" +
                                          "<Email>344@163.com</Email>"+
                                          "<Sex>Female</Sex>"+
                                          "<Language>Enlgish</Language>"+
                                          "<Birth>Feb</Birth>"+
                                  "</user_profile>"+
                                  "</customer>";
                      res.type("text/xml");
                      return xml;
                    });
    
    post("/adduser",(Request req, Response res)->

      {

        Connection connection = null;
        Map<String, Object> attributes = new HashMap<>();
        try{
        connection = DatabaseUrl.extract().getConnection();

//        JSONObject obj = new JSONObject(req.body());
       String email = (String) req.queryParams("signup-email");
       String password = (String) req.queryParams("signup-password");
       Statement stmt = connection.createStatement();
       stmt.executeUpdate("CREATE TABLE IF NOT EXISTS user_information (user_email varchar(100),  user_password  varchar(30),  user_name  varchar(30) )");
       stmt.executeUpdate("INSERT INTO user_information(user_email, user_password, user_name)" +
                "VALUES('" + email + "', '" + password + "', 'null')");
       ResultSet rs = stmt.executeQuery("SELECT user_email, user_password FROM user_information");
       ArrayList<String> output = new ArrayList<String>();

        while(rs.next())
        {
           output.add("read user " + "email: " + rs.getString("user_email") + "     , password: " + rs.getString("user_password") );

         }
         attributes.put("results",output);
       
        return new ModelAndView(attributes, "form.ftl");
      } catch (Exception e) {
        attributes.put("message", "There was an error: " + e);
        return new ModelAndView(attributes, "error.ftl");
      } finally {
        if (connection != null) try{connection.close();} catch(SQLException e){}
      }
    }, new FreeMarkerEngine());
    
  }

}
