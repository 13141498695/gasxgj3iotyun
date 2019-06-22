package com.jzkj.modules.product.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.jzkj.common.annotation.SysLog;
import com.jzkj.common.validator.ValidatorUtils;
import com.jzkj.common.validator.group.AddGroup;
import com.jzkj.modules.product.service.BarCodeService;
import com.jzkj.modules.product.service.ProduceService;
import com.jzkj.modules.sys.entity.SysUserEntity;

import io.swagger.annotations.ApiOperation;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

import com.jzkj.common.utils.PageUtils;
import com.jzkj.common.utils.ReturnResult;
import com.jzkj.miservice.entity.product.ProductEntity;


@RestController
@RequestMapping("/sys/product")
public class ProductController {
	/**
	 * 
	 *
	 * @author zhangbin
	 * @date 2019-05-10 15:55:39
	 */

	@Resource
	private ProduceService produceService;
	@Resource
	private BarCodeService barCodeervice;



		/**
		 * 保存产品
		 */
		@ApiOperation("保存产品1")
		@SysLog("保存产品")
		@RequestMapping("/add")
		@RequiresPermissions("sys:product:save")
		public ReturnResult save(@RequestBody ProductEntity product, HttpServletRequest request){

			String username = ((SysUserEntity) SecurityUtils.getSubject().getPrincipal()).getUsername();
			product.setCreatePeople(username);
			product.setCreateTime(new Date());
			produceService.save(product);
			return ReturnResult.ok();
		}

	/**
		 * 修改产品
		 */
	@ApiOperation("修改产品")
	@SysLog("修改产品")
		@RequestMapping("/update")
		@RequiresPermissions("sys:product:update")
		public ReturnResult update(@RequestBody ProductEntity product){
			int i =produceService.update(product);

			return ReturnResult.ok();
		}



		/**
		 * 查询单个
		 */
		@ApiOperation("修改单个产品回显")
		@SysLog("修改单个产品回显")
		@RequestMapping("/info/{productId}")
		/*@RequiresPermissions("sys:model:select")*/
		public ReturnResult select(@PathVariable("productId") String productId){
			ProductEntity	product=this.produceService.selectByid(productId);
			System.out.println("修改查询："+product.getProductName());
			//查询单个用户的信息
			return ReturnResult.ok().put("product", product);
			
			
		}
		
		/**
		 * 删除模型
		 */
		@ApiOperation("删除模型")
		@SysLog("产品模型")
		@RequestMapping("/delete")
		//@ResponseBody
		@RequiresPermissions("sys:product:delete")
		public ReturnResult delete(@RequestBody String [] productId){
			//ValidatorUtils.validateEntity(model, UpdateGroup.class);

		     for (int i = 0; i < productId.length; i++) {
		       produceService.delete(productId[i]);
				 System.out.println("删除的id:"+(productId[i]));
			 }
				return ReturnResult.ok();

		}
		
		/**
		 * 产品列表		 */
		@SysLog("产品列表")
		@ApiOperation("产品列表")
		@RequestMapping("/list")
	  @RequiresPermissions("sys:product:list")
		public ReturnResult list(@RequestParam Map<String, Object> params){
			PageUtils page = produceService.queryPage(params);
        System.out.println("页面："+page);
			return ReturnResult.ok().put("page", page);
		}

		@SysLog("上架产品")
		@ApiOperation("上架产品")
		@RequestMapping("/devlopr")
	    @RequiresPermissions("sys:product:devlopr")
		public ReturnResult devlopr(@RequestBody String [] productId){
			for (int i = 0; i < productId.length; i++) {
				produceService.devlopr(productId[i]);
				System.out.println("上架id:"+(productId[i]));

			}
			return ReturnResult.ok();
		}

		@SysLog("选择产品名称")
		@ApiOperation("选择产品名称")
		@RequestMapping("/prodcutlist")
	  //  @RequiresPermissions("sys:product:devlopr")
		public ReturnResult prodcutlist(){
			List<ProductEntity> product=this.produceService.selectAll();
			return ReturnResult.ok().put("product",product);
		}



	@SysLog("下架产品")
	@ApiOperation("下架产品")
	@RequestMapping("/low")
	@RequiresPermissions("sys:product:low")
		public ReturnResult low(@RequestBody String [] productId){
		for (int i = 0; i < productId.length; i++) {
			produceService.low(productId[i]);
			System.out.println("下架id:"+(productId[i]));
		}
		return ReturnResult.ok();
	}
}
