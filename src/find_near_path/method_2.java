package find_near_path;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.json.JSONArray;
import org.json.JSONObject;


public class method_2 {
       
    static double  lat = 0;
    static double log = 0;

    //    Getting Lat & log from the user's input
 
    public static void lat_log()
    {
        
        try{
            
            
            StringBuffer response = null;
            
            Scanner sc = new Scanner(System.in);
            System.out.print(" Enter Place name : ");
            String place = sc.nextLine();
            
            String uri = "http://api.positionstack.com/v1/forward?access_key=b3dd6b86fb462c367f4e83c5ee34e427&query="+place;
            URL obj = new URL(uri);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Opera");
            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' Request to url : "+uri);
            System.out.println("Response Code : "+responseCode);
            System.out.println("-----------------------------------------------------");
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            response = new StringBuffer();
            
             while((inputLine = br.readLine())!= null )
            {
                response.append(inputLine);
            }
            
            br.close();
            
            JSONObject myresponse = new JSONObject(response.toString());
            JSONArray ja = myresponse.getJSONArray("data");
            
            JSONObject city_name = (JSONObject) ja.get(0);
             lat = city_name.getDouble("latitude");
             log = city_name.getDouble("longitude");
            
//            System.out.println("Lat : "+lat);
//            System.out.println("Log : "+log);
            
            
           
        }
        
        catch(Exception e)
    {
        e.printStackTrace();
    }
        
    }

//    calculating distance from user's input & saving to db
    
    public static void cal_dist()
    {
        
        try{
            
        
            lat_log();
        
            Configuration cfg = new Configuration();
            cfg.configure("hibernate.cfg.xml");
            
            SessionFactory sf = cfg.buildSessionFactory();
            Session ss = sf.openSession();
            
            Transaction tx = ss.beginTransaction();
            
            
            double lat2 = lat;
            double log2 = log;
            
            StringBuffer response = null;
            
            String uri = "https://api.tomtom.com/search/2/nearbySearch/.json?lat="+lat2+"&lon="+log2+"&key=KzPcSHbl8tLdKcM5ORqWqH56g3PmkPUg";
            URL obj = new URL(uri);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Opera");
            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' Request to url : "+uri);
            System.out.println("Response Code : "+responseCode);
            System.out.println("-----------------------------------------------------");
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            response = new StringBuffer();
            
             while((inputLine = br.readLine())!= null )
            {
                response.append(inputLine);
            }
            
            br.close();
            
            
            JSONObject myresponse = new JSONObject(response.toString());
            JSONArray ja = myresponse.getJSONArray("results");
            
            
            
            JSONObject obb = new JSONObject();
            JSONObject poi = new JSONObject();
            JSONObject pos = new JSONObject();
            
            
            for(int i = 0; i < ja.length();i++)
            {
                 
                 obb = ja.getJSONObject(i);
                
                 poi = obb.getJSONObject("poi");
                 pos = obb.getJSONObject("position");
                
                
                double dist_1 = obb.getDouble("dist");
                double lat_1  = pos.getDouble("lat");
                double log_1  = pos.getDouble("lon");
                
                String dist = String.valueOf(dist_1);
                String lat  = String.valueOf(lat_1);
                String log = String.valueOf(log_1);
                
                
                User user = new User();
                
                user.setName(poi.getString("name"));
                user.setDist(dist);
                user.setLat(lat);
                user.setLog(log);
                
                ss.save(user);
                ss.flush();
                ss.clear();
                
            }
                
             tx.commit();
            
        }
        
        
        catch(Exception e)
    {
        
        e.printStackTrace();
    }
        
    }
    
    
    //    showing results
    public static void show_results()
    {
        try{
            cal_dist();
            
            Configuration cfg = new Configuration();
            cfg.configure("hibernate.cfg.xml");
            
            SessionFactory sf = cfg.buildSessionFactory();
            Session ss = sf.openSession();
            
            String hbm = "from findnearplace order by dist asc";
            
            Criteria ct = ss.createCriteria(User.class).setMaxResults(2);
            
            List list = ct.list();
            
            Iterator it = list.iterator();
            
            System.out.println("");
            System.out.println("-------------------------------------------------");
            while(it.hasNext())
            {
                
                User user = (User) it.next();
                
                System.out.println("name : "+user.getName()+ " Lat : "+user.getLat()+" log : "+user.getLog());
                
            }
            
            
             ss.close();
             sf.close();
            
        }
        catch(Exception e)
    {
            
        e.printStackTrace();
    }
        
    }
    
    
 
    public static void main(String[] args) {
        
        
        try{
           
           method_2.show_results();
            
                        

    }
    catch(Exception e)
    {
        e.printStackTrace();
    }
             
    }
}