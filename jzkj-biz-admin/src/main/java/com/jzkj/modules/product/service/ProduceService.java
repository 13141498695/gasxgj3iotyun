package com.jzkj.modules.product.service;

import java.util.List;
import java.util.Map;

import com.jzkj.common.utils.PageUtils;
import com.jzkj.miservice.entity.product.ProductEntity;

public interface ProduceService {

	

	int save(ProductEntity product);

	int update(ProductEntity product);

	int delete(String productId);

	ProductEntity selectByid(String productid);

	PageUtils queryPage(Map<String, Object> params);

    void devlopr(String s);
	void low(String s);

    List<ProductEntity> selectAll();

	void saveContext(ProductEntity product);
}
