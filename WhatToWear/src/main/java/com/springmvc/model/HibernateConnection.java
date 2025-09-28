package com.springmvc.model;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class HibernateConnection {

    // 1. สร้าง SessionFactory เป็น private static final เพื่อให้มีได้แค่ตัวเดียวและแก้ไขไม่ได้
    private static final SessionFactory SESSION_FACTORY = buildSessionFactory();

    // 2. สร้างเมธอด buildSessionFactory() ให้เป็น private
    private static SessionFactory buildSessionFactory() {
        try {
            // สร้าง Configuration object แค่ครั้งเดียว
            Configuration configuration = new Configuration();
            
            // ตั้งค่าต่างๆ (สามารถย้ายไปไว้ในไฟล์ hibernate.cfg.xml ได้เพื่อความสะอาด)
            configuration.setProperty("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver"); // Driver Class ที่ใหม่กว่า
            configuration.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/whattowear_project?characterEncoding=UTF-8&useSSL=false&serverTimezone=UTC");
            configuration.setProperty("hibernate.connection.username", "root");
            configuration.setProperty("hibernate.connection.password", "1234");
            configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect"); // Dialect ที่ใหม่กว่า
            configuration.setProperty("hibernate.hbm2ddl.auto", "update");
            
            // การตั้งค่า Connection Pool (ใช้ของ Hibernate เอง)
            configuration.setProperty("hibernate.c3p0.min_size", "5");
            configuration.setProperty("hibernate.c3p0.max_size", "20");
            configuration.setProperty("hibernate.c3p0.timeout", "300");
            configuration.setProperty("hibernate.c3p0.max_statements", "50");
            configuration.setProperty("hibernate.c3p0.idle_test_period", "3000");

            // เพิ่ม Annotated Class
            configuration.addAnnotatedClass(User.class);
            configuration.addAnnotatedClass(Category.class);
            configuration.addAnnotatedClass(SubCategory.class);
            configuration.addAnnotatedClass(ClothingItem.class);
            configuration.addAnnotatedClass(FormalityType.class);
            configuration.addAnnotatedClass(MatchStyle.class);
            
            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties()).build();
            
            System.out.println("Hibernate SessionFactory created successfully.");
            return configuration.buildSessionFactory(serviceRegistry);

        } catch (Throwable ex) {
            // ถ้าสร้างไม่สำเร็จ ให้ log error ไว้
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    // 3. สร้าง public static method สำหรับให้คลาสอื่นมาเรียกใช้ SessionFactory ตัวเดียวนี้
    public static SessionFactory doHibernateConnection() {
        return SESSION_FACTORY;
    }

    // 4. (แนะนำ) สร้างเมธอดสำหรับปิดการเชื่อมต่อทั้งหมด ตอนที่แอปพลิเคชันปิดตัวลง
    public static void shutdown() {
    	doHibernateConnection().close();
    }
}