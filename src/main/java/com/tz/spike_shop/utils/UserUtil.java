package com.tz.spike_shop.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tz.spike_shop.pojo.User;
import com.tz.spike_shop.vo.ResponseResult;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserUtil {

    public static void createUser(int count) throws SQLException, ClassNotFoundException, IOException {
        List<User> list = new ArrayList<User>(count);
        for (int i = 0; i < count; i++) {
            String salt = MD5Util.randomSalt(30);
            User user = new User();
            user.setId(13000000000L + i);
            user.setUsername("user_" + i);
            user.setPassword(MD5Util.md5("123456", salt));
            user.setSlat(salt);
            user.setRegisterDate(new Date());
            user.setLastLoginDate(new Date());
            user.setLoginCount(0);
            list.add(user);
        }
        System.out.println("准备创建用户");
//        Connection connection = getConnection();
//        String sql = "insert into t_user(id, username, password, slat, register_date, last_login_date, login_count) values(?, ?, ?, ?, ?, ?, ?)";
//        PreparedStatement ps = connection.prepareStatement(sql);
//
//        for (int i = 0; i < list.size(); i++) {
//            User user = list.get(i);
//            ps.setLong(1, user.getId());
//            ps.setString(2, user.getUsername());
//            ps.setString(3, user.getPassword());
//            ps.setString(4, user.getSlat());
//            ps.setTimestamp(5, new Timestamp(user.getRegisterDate().getTime()));
//            ps.setTimestamp(6, new Timestamp(user.getLastLoginDate().getTime()));
//            ps.setInt(7, user.getLoginCount());
//            ps.addBatch();
//        }
//        ps.executeBatch();
//        ps.clearParameters();
//        connection.close();
//        System.out.println("创建成功");

        //登录，生成userTicket
        String urlString = "http://localhost:8080/user/doLogin";
        File file = new File("C:\\Users\\Laymedown\\Desktop\\config.txt");
        if (file.exists()) {
            file.delete();
        }
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        file.createNewFile();
        raf.seek(0);
        for (int i = 0; i < list.size(); i++) {
            User user = list.get(i);
            URL url = new URL(urlString);
            HttpURLConnection co = (HttpURLConnection) url.openConnection();
            co.setRequestMethod("POST");
            co.setDoOutput(true);
            OutputStream out = co.getOutputStream();
            String params = "mobile=" + user.getId() + "&password=" + "123456";
            out.write(params.getBytes());
            out.flush();
            InputStream inputStream = co.getInputStream();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte buff[] = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(buff)) >= 0) {
                bout.write(buff, 0, len);
            }
            inputStream.close();
            bout.close();
            String response = new String(bout.toByteArray());
            ObjectMapper mapper = new ObjectMapper();
            ResponseResult respBean = mapper.readValue(response, ResponseResult.class);
            String cookie = ((String) respBean.getData());
            //System.out.println("create userTicket : " + user.getId());

            String row = user.getId() + "," + cookie;
            raf.seek(raf.length());
            raf.write(row.getBytes());
            raf.write("\r\n".getBytes());
            System.out.println("write to file : " + user.getId());
        }
        raf.close();

        System.out.println("over");
    }

    public static Connection getConnection() throws ClassNotFoundException, SQLException {
        String url = "jdbc:mysql://localhost:3306/spike?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai";
        String username = "root";
        String password = "082908";
        String driver = "com.mysql.cj.jdbc.Driver";
        Class<?> aClass = Class.forName(driver);

        return DriverManager.getConnection(url, username, password);
    }


    public static void main(String[] args) throws SQLException, IOException, ClassNotFoundException {
        createUser(5000);
    }

}
