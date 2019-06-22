package com.jzkj.modules.sys.service.impl;


import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.jzkj.common.annotation.DataFilter;
import com.jzkj.common.utils.Constant;
import com.jzkj.common.utils.Query;
import com.jzkj.modules.customer.service.CustomerUserService;
import com.jzkj.modules.sys.dao.SysUserDao;
import com.jzkj.modules.sys.entity.SysDeptEntity;
import com.jzkj.modules.sys.entity.SysUserEntity;
import com.jzkj.modules.sys.service.SysDeptService;
import com.jzkj.modules.sys.service.SysUserRoleService;
import com.jzkj.modules.sys.service.SysUserService;
import com.jzkj.modules.sys.shiro.ShiroUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.jzkj.common.utils.PageUtils;
import com.jzkj.miservice.entity.customer.CustomerUserEntity;


/**
 * 系统用户
 */
@Service("sysUserService")
public class SysUserServiceImpl extends ServiceImpl<SysUserDao, SysUserEntity> implements SysUserService {
	@Autowired
	private SysUserRoleService sysUserRoleService;
	@Autowired
	private SysDeptService sysDeptService;
	@Autowired
	private SysUserService sysUserService;	
	@Autowired
	private CustomerUserService customerUserService;
	
	@Override
	public List<Long> queryAllMenuId(Long userId) {

		return baseMapper.queryAllMenuId(userId);
	}

	@Override
	@DataFilter(subDept = true, user = false)
	public PageUtils queryPage(Map<String, Object> params) {
		String username = (String)params.get("username");

		Page<SysUserEntity> page = this.selectPage(
			new Query<SysUserEntity>(params).getPage(),
			new EntityWrapper<SysUserEntity>()
				.like(StringUtils.isNotBlank(username),"username", username)
				.addFilterIfNeed(params.get(Constant.SQL_FILTER) != null, (String)params.get(Constant.SQL_FILTER))
		);

		for(SysUserEntity sysUserEntity : page.getRecords()){
			SysDeptEntity sysDeptEntity = sysDeptService.selectById(sysUserEntity.getDeptId());
		//	sysUserEntity.setDeptName(sysDeptEntity.getName());
		}

		return new PageUtils(page);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void save(SysUserEntity user) {
		user.setCreateTime(new Date());
		String customerPassword=user.getPassword();
		//sha256加密
		String salt = RandomStringUtils.randomAlphanumeric(20);
		user.setSalt(salt);
		user.setPassword(ShiroUtils.sha256(user.getPassword(), user.getSalt()));
		this.insert(user);		
		//保存用户与角色关系
		sysUserRoleService.saveOrUpdate(user.getUserId(), user.getRoleIdList());
		customerUserInsert(user,customerPassword);
	}
	//添加到APP登录账号
	private void customerUserInsert(SysUserEntity user,String customerPassword) {
		CustomerUserEntity customerUser = new CustomerUserEntity();	
		customerUser.setUserId(user.getUserId());
		customerUser.setMobile(user.getUsername());
	 	customerUser.setUsername(user.getUsername());
	 	customerUser.setPassword(customerPassword);
//	 	customerUser.setPassword(DigestUtils.sha256Hex(user.getPassword()));
//	 	customerUser.setPassword(ShiroUtils.sha256(user.getPassword(), user.getSalt()));
	 	customerUser.setCreateTime(new Date());
        customerUserService.insert(customerUser);
	}
	 
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void update(SysUserEntity user) {
		//String customerPassword=user.getPassword();
		if(StringUtils.isBlank(user.getPassword())){
			user.setPassword(null);
		}else{
			SysUserEntity userEntity = this.selectById(user.getUserId());
			user.setPassword(ShiroUtils.sha256(user.getPassword(), userEntity.getSalt()));
		}
		this.updateById(user);
		//保存用户与角色关系
		sysUserRoleService.saveOrUpdate(user.getUserId(), user.getRoleIdList());
		//customerUserUpdateById(user,customerPassword);
	}

	////修改Customer
	private void customerUserUpdateById(SysUserEntity user,String customerPassword) {
		CustomerUserEntity customerUserSelectOne = customerUserService.selectById(user.getUserId());

			if (StringUtils.isBlank(user.getPassword())) {
				customerUserSelectOne.setPassword(null);
			} else {
				customerUserSelectOne.setPassword(user.getPassword());
//			customerUserSelectOne.setPassword(DigestUtils.sha256Hex(user.getPassword()));
//			SysUserEntity userEntity = this.selectById(user.getUserId());
//			customerUserSelectOne.setPassword(ShiroUtils.sha256(user.getPassword(), userEntity.getSalt()));
			}
		customerUserService.updateById(customerUserSelectOne);
	}


	@Override
	public boolean updatePassword(Long userId, String password, String newPassword) {		
        SysUserEntity userEntity = new SysUserEntity();
        userEntity.setPassword(newPassword);

        boolean updatePassword = sysUserService.update(userEntity,new EntityWrapper<SysUserEntity>().
				eq("user_id", userId).eq("password", password));
        if(updatePassword){
			System.out.println("原密码不正确");
        }else{
			return updatePassword;
		}
//        CustomerUserEntity customerUserEntity = new CustomerUserEntity();
//        customerUserEntity.setPassword(newPassword);
//        updatePassword = customerUserService.update(customerUserEntity,new EntityWrapper<CustomerUserEntity>().eq("user_id", userId).eq("password", password));
        return false;
    }
	
	//删除系统用户
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean deleteSysUserAsList(Long[] userIds){
		deleteCustomerUser(userIds[0]);
		return sysUserService.deleteBatchIds(Arrays.asList(userIds));
	}
	//删除APP用户
	public boolean deleteCustomerUser(Long userId) {
	        return customerUserService.delete(new EntityWrapper<CustomerUserEntity>().eq("user_id", userId));
	}
	

}
