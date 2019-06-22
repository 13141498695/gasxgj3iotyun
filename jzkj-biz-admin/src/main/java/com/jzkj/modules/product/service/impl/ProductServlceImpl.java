package com.jzkj.modules.product.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.jzkj.common.annotation.DataFilter;
import com.jzkj.common.utils.Query;
import com.jzkj.modules.customer.service.CustomerUserService;
import com.jzkj.modules.product.dao.ProductDao;
import com.jzkj.modules.product.service.ProduceService;
import com.jzkj.modules.sys.entity.SysDeptEntity;
import com.jzkj.modules.sys.entity.SysDictEntity;
import com.jzkj.modules.sys.entity.SysUserEntity;
import com.jzkj.modules.sys.service.SysDeptService;
import com.jzkj.modules.sys.service.SysUserRoleService;
import com.jzkj.modules.sys.service.SysUserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.jzkj.common.utils.PageUtils;
import com.jzkj.miservice.entity.product.ProductEntity;
import com.jzkj.miservice.entity.product.ProductEntityExample;
import com.github.pagehelper.PageHelper;

@Service("ProduceService")
public class ProductServlceImpl implements ProduceService {
@Resource
private ProductDao productdao;


	@Autowired
	private SysUserRoleService sysUserRoleService;
	@Autowired
	private SysDeptService sysDeptService;
	@Autowired
	private SysUserService sysUserService;
	@Autowired
	private CustomerUserService customerUserService;
	@Override
	public int save(ProductEntity product) {
		ProductEntity products=this.productdao.selectLastEntity();
		product.setProductId(products.getProductId());
		// TODO Auto-generated method stub
		return productdao.updateByPrimaryKeySelective(product);
	}

	@Override
	public int update(ProductEntity product) {
		// TODO Auto-generated method stub
		return this.productdao.updateByPrimaryKeySelective(product);
	}

	@Override
	public int delete(String productId) {
		// TODO Auto-generated method stub
		System.out.println("来了");
		return this.productdao.deleteByid(productId);
	}

	@Override
	public ProductEntity selectByid(String productid) {
		// TODO Auto-generated method stub
		return this.productdao.selectByPrimaryKey(productid);
	}

	@Override
	@DataFilter(subDept = true, user = false)
	public PageUtils queryPage(Map<String, Object> params) {
		 String productName = (String)params.get("productName");

		Page<ProductEntity> page = new Query<ProductEntity>(params).getPage();
        PageHelper.startPage(page.getCurrent(), page.getSize());  
        
        ProductEntityExample example=new ProductEntityExample();
    	ProductEntityExample.Criteria  criteria=example.createCriteria();

        if(productName!=null){
        	criteria.andProductNameLike("%"+productName+"%");
        }

		for(ProductEntity productEntity : page.getRecords()){
			SysDeptEntity sysDeptEntity = sysDeptService.selectById(productEntity.getDeptId());
		}

        Byte delflag=0;
        criteria.andDelFlagEqualTo(delflag);
        List<ProductEntity> allItems = productdao.selectByExample(example);
        long total = productdao.countByExample(example);
        
        System.out.println("total:"+total);
        page.setTotal((int)total);
        page.setRecords(allItems);
        return new PageUtils(page);
	}

	@Override
	public void devlopr(String s) {
		ProductEntity productEntity=this.productdao.selectByPrimaryKey(s);
		productEntity.setProductStatus(2);
		this.productdao.updateByPrimaryKeySelective(productEntity);
	}

	@Override
	public void low(String s) {
		ProductEntity productEntity=this.productdao.selectByPrimaryKey(s);
		productEntity.setProductStatus(3);
		this.productdao.updateByPrimaryKeySelective(productEntity);
	}

	@Override
	public List<ProductEntity> selectAll() {

		return this.productdao.productlist();
	}

	@Override
	public void saveContext(ProductEntity product) {
		product.setCreateTime(new Date());
		this.productdao.insertSelective(product);
	}
}
