package productcrudapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.view.RedirectView;

import jakarta.servlet.http.HttpServletRequest;
import productcrudapp.dao.ProductDao;
import productcrudapp.model.Product;

@Controller
public class MainController {
	
	@Autowired
	private ProductDao productDao;

	@RequestMapping("/")
	public String home(Model m) {
		
		List<Product> products = this.productDao.getAllProducts();
		m.addAttribute("products",products);
		m.addAttribute("title", "Product List");
		return "index";
	}
	
	
	// show add product form
	@RequestMapping("/add-product")
	public String addProduct(Model m) {
		m.addAttribute("product", new Product());
		m.addAttribute("title","Add Product");
		return "add_product_form";
	}
	
	// handle the product form
	
	@RequestMapping(value="/handle-product", method = RequestMethod.POST)
	public RedirectView handleProduct(@ModelAttribute("product") Product product,HttpServletRequest req)
	{
		System.out.println(product);
		RedirectView redirectview = new RedirectView();
		this.productDao.createProduct(product); 
		redirectview.setUrl(req.getContextPath() + "/");
		return redirectview;
	}
	
	
	// Use curly braces {} for the path variable placeholder
	@RequestMapping("/delete-product/{productId}")
	public RedirectView deleteProduct(@PathVariable("productId") int productId, HttpServletRequest req)
	{
	    this.productDao.deleteProduct(productId);
	    RedirectView redirectView = new RedirectView();
	    redirectView.setUrl(req.getContextPath() + "/");
	   return redirectView;
	}
	
	@RequestMapping("/update-product/{productId}")
	public String updateForm(@PathVariable("productId") int pid, Model model)
	{
		Product product = this.productDao.getProduct(pid);
		model.addAttribute("product",product);
		return "update_form";
	}
	
	@RequestMapping(value="/handle-update", method = RequestMethod.POST)
	public RedirectView handleUpdate(@ModelAttribute("product") Product product, HttpServletRequest req) {
	    
	    // Call the DAO update method
	    this.productDao.updateProduct(product); 
	    
	    // Redirect to the home page to show the updated list
	    RedirectView redirectview = new RedirectView(req.getContextPath() + "/");
	    return redirectview;
	}
}
