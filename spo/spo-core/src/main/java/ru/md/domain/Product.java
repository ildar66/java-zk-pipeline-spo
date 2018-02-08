package ru.md.domain;

/**
 * вид сделки.
 * @author Andrey Pavlenko
 */
public class Product {
	private String productid;
	private String name;
	private String actualid;
	
	public Product() { }
	public Product(String productid) {
	    super();
	    this.productid = productid;
	}
	
	public String getProductid() {
	    return productid;
	}
	public void setProductid(String productid) {
		this.productid = productid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getActualid() {
		return actualid;
	}
	public void setActualid(String actualid) {
		this.actualid = actualid;
	}
}
