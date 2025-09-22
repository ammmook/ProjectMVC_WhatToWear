package com.springmvc.manager;

import java.util.*;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.springmvc.model.Category;
import com.springmvc.model.ClothingItem;
import com.springmvc.model.FormalityType;
import com.springmvc.model.HibernateConnection;
import com.springmvc.model.SubCategory;

public class ClothingManager {
	public List<SubCategory> listSubcategories() {
	    List<SubCategory> subcates = null;
	    SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
	    Session session = sessionFactory.openSession();
	    Transaction tx = null;

	    try {
	        tx = session.beginTransaction();
	        subcates = session.createQuery("from SubCategory", SubCategory.class).list();
	        
	        tx.commit();
	    } catch (Exception ex) {
	        if (tx != null) {
	            tx.rollback();
	        }
	        ex.printStackTrace();
	    } finally {
	        session.close();
	    }

	    return subcates;
	}
	
	public List<Category> listCategories() {
	    List<Category> categories = null;
	    SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
	    Session session = sessionFactory.openSession();
	    Transaction tx = null;

	    try {
	        tx = session.beginTransaction();
	        categories = session.createQuery("from Category", Category.class).list();
	        
	        tx.commit();
	    } catch (Exception ex) {
	        if (tx != null) {
	            tx.rollback();
	        }
	        ex.printStackTrace();
	    } finally {
	        session.close();
	    }

	    return categories;
	}

	public List<Category> listCategoriesByEmail(String email) {
	    List<Category> categories = null;
	    SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
	    Session session = sessionFactory.openSession();
	    Transaction tx = null;

	    try {
	        tx = session.beginTransaction();
	        categories = session.createQuery(
	            "select c from Category c " +
	            "join SubCategory s on c.categoryId = s.category.categoryId " +
	            "join ClothingItem ci on s.subCategoryId = ci.subCategory.subCategoryId " +
	            "where ci.user.email = :email " +
	            "group by c.categoryId", Category.class)
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

	    return categories;
	}

	public List<SubCategory> listSubcatesByEmailAndCategory(String email, String categoryId) {
	    List<SubCategory> subcategories = null;
	    SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
	    Session session = sessionFactory.openSession();
	    Transaction tx = null;

	    try {
	        tx = session.beginTransaction();
	        subcategories = session.createQuery(
	            "select s from SubCategory s " +
	            "join ClothingItem c on s.subCategoryId = c.subCategory.subCategoryId " +
	            "where c.user.email = :email and s.category.categoryId = :categoryId " +
	            "group by s.subCategoryId", SubCategory.class)
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

	    return subcategories;
	}

	public List<ClothingItem> getClothesByEmailAndCategory(String email, String categoryId) {
	    List<ClothingItem> clothes = null;
	    SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
	    Session session = sessionFactory.openSession();
	    Transaction tx = null;

	    try {
	        tx = session.beginTransaction();
	        clothes = session.createQuery(
	            "select ci from ClothingItem ci " +
	            "join fetch ci.subCategory sc " +
	            "join fetch sc.category c " +
	            "join fetch ci.formalityType ft " +
	            "join fetch ci.user u " +
	            "where u.email = :email and c.categoryId = :categoryId " +
	            "order by sc.subCategoryId", ClothingItem.class)
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

	    return clothes;
	}
	
	public List<FormalityType> listFormalityTypes() {
	    List<FormalityType> types = null;
	    SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
	    Session session = sessionFactory.openSession();
	    Transaction tx = null;

	    try {
	        tx = session.beginTransaction();
	        types = session.createQuery("from FormalityType", FormalityType.class).list();
	        
	        tx.commit();
	    } catch (Exception ex) {
	        if (tx != null) {
	            tx.rollback();
	        }
	        ex.printStackTrace();
	    } finally {
	        session.close();
	    }

	    return types;
	}
	
	public boolean addClothingItem(ClothingItem clothingItem) {
	    SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
	    Session session = sessionFactory.openSession();
	    Transaction tx = null;

	    try {
	        tx = session.beginTransaction();
	        
	        session.saveOrUpdate(clothingItem);
	        
	        tx.commit();
	        return true;
	    } catch (Exception ex) {
	        if (tx != null) {
	            tx.rollback();
	        }
	        ex.printStackTrace();
	        return false;
	    } finally {
	        session.close();
	    }
	}
	
	public FormalityType getFormalityTypeById(String typeId) {
	    FormalityType formalityType = null;
	    SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
	    Session session = sessionFactory.openSession();
	    Transaction tx = null;

	    try {
	        tx = session.beginTransaction();
	        formalityType = session.createQuery("from FormalityType ft where ft.typeId = :typeId", FormalityType.class)
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

	    return formalityType;
	}
	
	public SubCategory getSubCategoryById(String subcateId) {
	    SubCategory subCategory = null;
	    SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
	    Session session = sessionFactory.openSession();
	    Transaction tx = null;

	    try {
	        tx = session.beginTransaction();
	        subCategory = session.createQuery("from SubCategory sc where sc.subCategoryId = :subcateId", SubCategory.class)
	            .setParameter("subcateId", subcateId)
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

	    return subCategory;
	}
	
	public List<SubCategory> listSubcatesByEmail(String email) {
	    List<SubCategory> subcategories = null;
	    SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
	    Session session = sessionFactory.openSession();
	    Transaction tx = null;

	    try {
	        tx = session.beginTransaction();
	        subcategories = session.createQuery(
	            "select s from SubCategory s join ClothingItem c on s.subCategoryId = c.subCategory.subCategoryId " +
	            "where c.user.email = :email group by s.subCategoryId", SubCategory.class)
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

	    return subcategories;
	}
	
	public List<ClothingItem> getClothesByEmail(String email) {
	    List<ClothingItem> clothes = null;
	    SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
	    Session session = sessionFactory.openSession();
	    Transaction tx = null;

	    try {
	        tx = session.beginTransaction();
	        clothes = session.createQuery(
	            "select ci from ClothingItem ci " +
	            "join fetch ci.subCategory sc " +
	            "join fetch sc.category c " +
	            "join fetch ci.formalityType ft " +
	            "join fetch ci.user u " +
	            "where u.email = :email " +
	            "order by sc.subCategoryId", ClothingItem.class)
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

	    return clothes;
	}
	
	public List<ClothingItem> getClothesByEmailAndSubCate(String email, String subcateId) {
	    List<ClothingItem> clothes = null;
	    SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
	    Session session = sessionFactory.openSession();
	    Transaction tx = null;

	    try {
	        tx = session.beginTransaction();
	        clothes = session.createQuery(
	            "from ClothingItem c join fetch c.subCategory s " +
	            "where c.user.email = :email and s.subCategoryId = :subcateId", ClothingItem.class)
	            .setParameter("email", email)
	            .setParameter("subcateId", subcateId)
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

	    return clothes;
	}
	
	public ClothingItem getClothesById(Long clothingId) {
	    ClothingItem clothingItem = null;
	    SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
	    Session session = sessionFactory.openSession();
	    Transaction tx = null;

	    try {
	        tx = session.beginTransaction();
	        clothingItem = session.createQuery("from ClothingItem c where c.clothid = :clothingId", ClothingItem.class)
	            .setParameter("clothingId", clothingId)
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

	    return clothingItem;
	}
	
	public List<ClothingItem> getClothingItemsByIds(String[] ids, String email) {
	    List<ClothingItem> items = null;
	    SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
	    Session session = sessionFactory.openSession();
	    Transaction tx = null;
	    
	    try {
	        tx = session.beginTransaction();
	        
	        // Convert String[] to Long[]
	        Long[] clothIds = new Long[ids.length];
	        for (int i = 0; i < ids.length; i++) {
	            clothIds[i] = Long.parseLong(ids[i]);
	        }
	        
	        items = session.createQuery(
	            "select ci from ClothingItem ci " +
	            "join fetch ci.subCategory sc " +
	            "join fetch sc.category " +
	            "join fetch ci.formalityType " +
	            "where ci.clothid in :ids " +
	            "and ci.user.email = :email", ClothingItem.class)
	            .setParameterList("ids", clothIds)
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
	    
	    return items;
	}
	
	public boolean deleteClothes(Long[] clothingIds) {
	    SessionFactory sessionFactory = HibernateConnection.doHibernateConnection();
	    Session session = sessionFactory.openSession();
	    Transaction tx = null;
	    boolean success = true;

	    try {
	        tx = session.beginTransaction();

	        for (Long clothingId : clothingIds) {
	            // Step 1 & 2: หา styleId และลบทั้งหมดที่เกี่ยวข้อง
	            List<Object> styleIdObjects = session.createNativeQuery(
	                "SELECT DISTINCT style_id FROM favorite_styles WHERE clothing_id = :clothingId")
	                .setParameter("clothingId", clothingId)
	                .list();

	            // Convert Objects to Strings
	            List<String> styleIds = new ArrayList<>();
	            for (Object obj : styleIdObjects) {
	                if (obj != null) {
	                    styleIds.add(obj.toString());
	                }
	            }

	            // ถ้ามี styleId ให้ลบทั้งหมด
	            for (String styleId : styleIds) {
	                // ลบทุกอันใน favorite_styles ที่มี styleId นี้
	                session.createNativeQuery(
	                    "DELETE FROM favorite_styles WHERE style_id = :styleId")
	                    .setParameter("styleId", styleId)
	                    .executeUpdate();

	                // ลบ style
	                session.createNativeQuery(
	                    "DELETE FROM match_styles WHERE style_id = :styleId")
	                    .setParameter("styleId", styleId)
	                    .executeUpdate();
	            }

	            // Step 3: ลบเสื้อผ้า
	            session.createNativeQuery(
	                "DELETE FROM clothing_items WHERE clothing_id = :clothingId")
	                .setParameter("clothingId", clothingId)
	                .executeUpdate();
	        }

	        tx.commit();
	    } catch (Exception ex) {
	        if (tx != null) {
	            tx.rollback();
	        }
	        ex.printStackTrace();
	        success = false;
	    } finally {
	        session.close();
	    }

	    return success;
	}
}
