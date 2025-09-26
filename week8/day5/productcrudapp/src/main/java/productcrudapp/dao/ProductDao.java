package productcrudapp.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import productcrudapp.model.Product;



@Component
public class ProductDao {
	@Autowired
	private SessionFactory sessionFactory;
	
	
	// creating the product
	@Transactional
	public void createProduct(Product product)
	{
		Session session = this.sessionFactory.getCurrentSession();
		session.persist(product);
	}
	
	
	// read the product
	@Transactional(readOnly = true)
	public Product getProduct(int pid)
	{
		Session session = this.sessionFactory.getCurrentSession();
		return session.get(Product.class,pid);
	}
	
	
	@Transactional(readOnly = true)
	public List<Product> getAllProducts()
	{
		Session session = this.sessionFactory.getCurrentSession();
		return session.createQuery("from Product",Product.class).getResultList();
	}
	
	@Transactional
	public void deleteProduct(int pid)
	{
	    Session session = this.sessionFactory.getCurrentSession();
	    Product p = session.load(Product.class, pid); // Loads a proxy (optimized)
	    session.remove(p); // Removes the persistent object
	}
	
	
	
	@Transactional
	public void updateProduct(Product product)
	{
		Session session = this.sessionFactory.getCurrentSession();
		session.merge(product);
	}
}
	
