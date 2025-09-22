package com.springmvc.manager;

import java.util.*;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import com.springmvc.model.*;

public class StyleManager {
	
	public boolean saveFavoriteStyle(MatchStyle style, String userEmail) {
	    SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
	    Session session = sessionFactory.openSession();
	    Transaction tx = null;
	    
	    try {
	        tx = session.beginTransaction();
	        
	        System.out.println("=== Saving Favorite Style ===");
	        System.out.println("User: " + userEmail);
	        System.out.println("Number of items: " + style.getClothingItems().size());
	        
	        // สร้าง list ของ clothing IDs
	        List<Long> clothingIds = new ArrayList<>();
	        for (ClothingItem item : style.getClothingItems()) {
	            clothingIds.add(item.getClothid());
	        }
	        Collections.sort(clothingIds);
	        
	        // ตรวจสอบว่ามีชุดที่มี items เหมือนกันหรือไม่
	        String checkHql = "select ms from MatchStyle ms " +
	                         "join ms.clothingItems ci " +
	                         "where ci.user.email = :email " +
	                         "group by ms " +
	                         "having count(ci) = :itemCount";
	        
	        List<MatchStyle> existingStyles = session.createQuery(checkHql, MatchStyle.class)
	            .setParameter("email", userEmail)
	            .setParameter("itemCount", Long.valueOf(clothingIds.size())) // ใช้ Long แทน int
	            .list();
	        
	        // ตรวจสอบว่ามี style ที่มี items เหมือนกันทุกตัวหรือไม่
	        boolean isDuplicate = false;
	        for (MatchStyle existingStyle : existingStyles) {
	            List<Long> existingIds = new ArrayList<>();
	            for (ClothingItem item : existingStyle.getClothingItems()) {
	                existingIds.add(item.getClothid());
	            }
	            Collections.sort(existingIds);
	            
	            if (existingIds.equals(clothingIds)) {
	                isDuplicate = true;
	                System.out.println("⚠️ Duplicate style found with ID: " + existingStyle.getStyleId());
	                break;
	            }
	        }
	        
	        if (!isDuplicate) {
	            // ดึง managed entities
	            List<ClothingItem> managedItems = new ArrayList<>();
	            for (ClothingItem item : style.getClothingItems()) {
	                ClothingItem managedItem = session.get(ClothingItem.class, item.getClothid());
	                if (managedItem != null) {
	                    managedItems.add(managedItem);
	                }
	            }
	            
	            // สร้าง MatchStyle ใหม่
	            MatchStyle newStyle = new MatchStyle();
	            newStyle.setFormalityType(session.get(FormalityType.class, style.getFormalityType().getTypeId()));
	            newStyle.setClothingItems(managedItems);
	            
	            // บันทึก
	            session.save(newStyle);
	            System.out.println("✅ New MatchStyle saved with ID: " + newStyle.getStyleId());
	        } else {
	            System.out.println("⚠️ Style already exists, skipping save");
	        }
	        
	        tx.commit();
	        return true;
	        
	    } catch (Exception ex) {
	        if (tx != null) {
	            tx.rollback();
	        }
	        System.out.println("❌ Error saving MatchStyle: " + ex.getMessage());
	        ex.printStackTrace();
	        return false;
	    } finally {
	        session.close();
	    }
	}
	
	public List<MatchStyle> listFavoriteStylesByEmail(String email) {
	    List<MatchStyle> styles = null;
	    SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
	    Session session = sessionFactory.openSession();
	    Transaction tx = null;

	    try {
	        tx = session.beginTransaction();

	        styles = session.createQuery(
	            "select distinct ms from MatchStyle ms " +
	            "join fetch ms.formalityType " +
	            "left join fetch ms.clothingItems ci " +
	            "left join fetch ci.subCategory sc " +
	            "left join fetch sc.category c " +
	            "where ci.user.email = :email " +
	            "order by size(ms.clothingItems) asc, " +
	            "case when c.categoryId = 'CG004' then 1 " +  // เสื้อคลุม
	            "     when c.categoryId = 'CG001' then 2 " +  // ท่อนบน
	            "     when c.categoryId = 'CG002' then 3 " +  // ท่อนล่าง
	            "     when c.categoryId = 'CG003' then 4 " +  // เดรส
	            "     else 5 end, " +
	            "ms.styleId asc", MatchStyle.class)
	            .setParameter("email", email)
	            .list();

	        tx.commit();
	    } catch (Exception ex) {
	        if (tx != null) {
	            tx.rollback();
	        }
	        ex.printStackTrace();
	    } finally {
	        session.close();
	    }

	    return styles;
	}
	
	public List<FormalityType> listFormalityTypes(String email) {
	    List<FormalityType> formalityTypes = null;
	    SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
	    Session session = sessionFactory.openSession();
	    Transaction tx = null;

	    try {
	        tx = session.beginTransaction();
	        formalityTypes = session.createQuery(
	            "select distinct f " +
	            "from MatchStyle m " +
	            "join m.formalityType f " +
	            "join m.clothingItems ci " +
	            "where ci.user.email = :email", FormalityType.class)
	            .setParameter("email", email)
	            .list();

	        tx.commit();
	    } catch (Exception ex) {
	        if (tx != null) {
	            tx.rollback();
	        }
	        ex.printStackTrace();
	    } finally {
	        session.close();
	    }

	    return formalityTypes;
	}
	
	public List<MatchStyle> getFavoriteStylesWithOuterwear(String email, String typeId, boolean showOuterwear) {
	    List<MatchStyle> styles = null;
	    SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
	    Session session = sessionFactory.openSession();
	    Transaction tx = null;

	    try {
	        tx = session.beginTransaction();

	        String hql = "select distinct ms from MatchStyle ms " +
	            "join fetch ms.formalityType " +
	            "left join fetch ms.clothingItems ci " +
	            "left join fetch ci.subCategory sc " +
	            "left join fetch sc.category c " +
	            "where ci.user.email = :email ";

	        // เพิ่มเงื่อนไข type ถ้าไม่ใช่ alltype
	        if (typeId != null && !typeId.equals("alltype")) {
	            hql += "and ms.formalityType.typeId = :typeId ";
	        }

	        // เพิ่มเงื่อนไขแสดงเฉพาะ style ที่มีเสื้อคลุม
	        if (showOuterwear) {
	            hql += "and ms.styleId in (" +
	                "select distinct ms2.styleId from MatchStyle ms2 " +
	                "join ms2.clothingItems ci2 " +
	                "join ci2.subCategory sc2 " +
	                "join sc2.category c2 " +
	                "where ci2.user.email = :email " +
	                "and c2.categoryId = 'CG004') ";
	        }

	        // เพิ่ม ORDER BY เรียงตามจำนวนสินค้า และลำดับ category
	        hql += "order by size(ms.clothingItems) asc, " +
	               "case when c.categoryId = 'CG004' then 1 " +
	               "     when c.categoryId = 'CG001' then 2 " +
	               "     when c.categoryId = 'CG002' then 3 " +
	               "     when c.categoryId = 'CG003' then 4 " +
	               "     else 5 end, " +
	               "ms.styleId asc";

	        Query<MatchStyle> query = session.createQuery(hql, MatchStyle.class)
	            .setParameter("email", email);

	        if (typeId != null && !typeId.equals("alltype")) {
	            query.setParameter("typeId", typeId);
	        }

	        styles = query.list();

	        tx.commit();
	    } catch (Exception ex) {
	        if (tx != null) {
	            tx.rollback();
	        }
	        ex.printStackTrace();
	    } finally {
	        session.close();
	    }

	    return styles;
	}
	
	public boolean isStyleAlreadyFavorited(MatchStyle potentialStyle, String userEmail) {
	    SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
	    Session session = sessionFactory.openSession();
	    Transaction tx = null;
	    
	    try {
	        tx = session.beginTransaction();
	        
	        // 1. ดึง ID ของเสื้อผ้าในชุดที่กำลังจะสร้าง แล้วเรียงลำดับ
	        List<Long> potentialIds = new ArrayList<>();
	        for (ClothingItem item : potentialStyle.getClothingItems()) {
	            potentialIds.add(item.getClothid());
	        }
	        Collections.sort(potentialIds);
	        
	        // 2. HQL เพื่อค้นหาชุดทั้งหมดใน DB ที่มีจำนวนเสื้อผ้าเท่ากันและเป็นของ user คนนี้
	        String hql = "select ms from MatchStyle ms " +
	                     "join ms.clothingItems ci " +
	                     "where ci.user.email = :email " +
	                     "group by ms.styleId " + // แก้ไข group by ให้ถูกต้อง
	                     "having count(ci) = :itemCount";
	        
	        List<MatchStyle> existingStyles = session.createQuery(hql, MatchStyle.class)
	            .setParameter("email", userEmail)
	            .setParameter("itemCount", (long) potentialIds.size())
	            .list();
	        
	        tx.commit();
	        
	        // 3. วนลูปใน Java เพื่อเปรียบเทียบ ID ของเสื้อผ้าทุกชิ้น
	        for (MatchStyle existingStyle : existingStyles) {
	            List<Long> existingIds = new ArrayList<>();
	            // Eagerly fetch items if they are lazy
	            for (ClothingItem item : existingStyle.getClothingItems()) {
	                existingIds.add(item.getClothid());
	            }
	            Collections.sort(existingIds);
	            
	            // ถ้า ID ทั้งสองลิสต์เหมือนกันเป๊ะ แสดงว่าซ้ำ
	            if (existingIds.equals(potentialIds)) {
	                return true; // เจอชุดที่ซ้ำใน DB
	            }
	        }
	        
	    } catch (Exception ex) {
	        if (tx != null) tx.rollback();
	        ex.printStackTrace();
	    } finally {
	        session.close();
	    }
	    
	    return false; // ไม่เจอชุดที่ซ้ำ
	}
	
	public boolean deleteFavorite(Long styleId) {
	    SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
	    Session session = sessionFactory.openSession();
	    Transaction tx = null;

	    try {
	        tx = session.beginTransaction();
	        
	        MatchStyle style = session.get(MatchStyle.class, styleId);
	        session.delete(style);
	        
	        tx.commit();
	    } catch (Exception ex) {
	        if (tx != null) {
	            tx.rollback();
	        }
	        ex.printStackTrace();
	        return false;
	    } finally {
	        session.close();
	    }

	    return true;
	}
	
	public List<ClothingItem> getClothingByCategory(String email, String categoryId) {
	    List<ClothingItem> items = null;
	    SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
	    Session session = sessionFactory.openSession();
	    Transaction tx = null;
	    
	    try {
	        tx = session.beginTransaction();
	        
	        items = session.createQuery(
	            "select ci from ClothingItem ci " +
	            "join fetch ci.subCategory sc " +
	            "join fetch sc.category c " +
	            "join fetch ci.formalityType " +
	            "where ci.user.email = :email " +
	            "and c.categoryId = :categoryId " +
	            "order by ci.clothid", ClothingItem.class)
	            .setParameter("email", email)
	            .setParameter("categoryId", categoryId)
	            .list();
	            
	        tx.commit();
	    } catch (Exception ex) {
	        if (tx != null) {
	            tx.rollback();
	        }
	        ex.printStackTrace();
	    } finally {
	        session.close();
	    }
	    
	    return items;
	}
	
	public FormalityType getFormalityTypeById(String typeId) {
	    FormalityType type = null;
	    SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
	    Session session = sessionFactory.openSession();
	    Transaction tx = null;
	    
	    try {
	        tx = session.beginTransaction();
	        
	        type = session.createQuery(
	            "from FormalityType ft where ft.typeId = :typeId", FormalityType.class)
	            .setParameter("typeId", typeId)
	            .uniqueResult();
	            
	        tx.commit();
	    } catch (Exception ex) {
	        if (tx != null) {
	            tx.rollback();
	        }
	        ex.printStackTrace();
	    } finally {
	        session.close();
	    }
	    
	    return type;
	}
	
	public String getLastStyleId() {
	    String lastId = null;
	    SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
	    Session session = sessionFactory.openSession();
	    Transaction tx = null;
	    
	    try {
	        tx = session.beginTransaction();
	        
	        lastId = (String) session.createQuery(
	            "select ms.styleId from MatchStyle ms " +
	            "where ms.styleId like 'ST%' " +
	            "order by ms.styleId desc")
	            .setMaxResults(1)
	            .uniqueResult();
	            
	        tx.commit();
	    } catch (Exception ex) {
	        if (tx != null) {
	            tx.rollback();
	        }
	        ex.printStackTrace();
	    } finally {
	        session.close();
	    }
	    
	    return lastId;
	}
}
