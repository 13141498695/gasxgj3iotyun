package com.jzkj.modules.sys.controller;

import com.jzkj.modules.sys.service.SysLogService;
import com.jzkj.common.utils.PageUtils;
import com.jzkj.common.utils.ReturnResult;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.Map;


/**
 * 系统日志
 * 
 * @author  
 * @email  
 * @date  
 */
@Controller
@RequestMapping("/sys/log")
public class SysLogController {
	@Autowired
	private SysLogService sysLogService;
	
	/**
	 * 列表
	 */
	@ResponseBody
	@RequestMapping("/list")
	@RequiresPermissions("sys:log:list")
	public ReturnResult list(@RequestParam Map<String, Object> params){
		PageUtils page = sysLogService.queryPage(params);

		return ReturnResult.ok().put("page", page);
	}
	///sys/log/delete
	@ResponseBody
	@RequestMapping("/delete")
//	@RequiresPermissions("sys:log:list")
	public ReturnResult delete(@RequestBody String [] id){
		System.out.println("id:"+id);
		sysLogService.deleteBatchIds(Arrays.asList(id));
		return ReturnResult.ok();
	}



	
}
