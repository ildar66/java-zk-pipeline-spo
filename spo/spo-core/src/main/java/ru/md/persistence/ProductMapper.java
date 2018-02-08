package ru.md.persistence;

import java.util.List;

import ru.md.domain.Product;

/**
 * 
 * @author Andrey Pavlenko
 *
 */
public interface ProductMapper {
	Product getById(String id);
	
	/**
	 * Возвращает список видов продукта (сделки).
	 * @return список видов продукта (сделки)
	 */
	List<Product> getProducts();
}
