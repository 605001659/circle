package com.niusales.soft.platform.controller;

import com.niusales.soft.enums.MenusTypeEnum;
import com.niusales.soft.enums.OperateRecordsFromEnum;
import com.niusales.soft.enums.OperateRecordsTypeEnum;
import com.niusales.soft.interceptor.PageBean;
import com.niusales.soft.platform.dto.CriteriaMenu;
import com.niusales.soft.platform.service.OperaterecordsService;
import com.niusales.soft.platform.service.PointService;
import com.niusales.soft.platform.service.RoleService;
import com.niusales.soft.platform.service.UserService;
import com.niusales.soft.platform.vo.ReusltItem;
import com.niusales.soft.po.Role;
import com.niusales.soft.util.DebugConfig;
import com.niusales.soft.util.LogType;
import com.niusales.soft.util.SessionState;
import com.niusales.soft.util.SessionUser;
import com.niusales.soft.util.StringUtilsEX;
import com.yl.soft.log.LogHandle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/platform/point")
public class PointController {

	@Autowired
	private RoleService roleService;

	@Autowired
	private UserService userService;

    SessionUser user=null;
    @Autowired
    private OperaterecordsService operaterecordsService ;
    
    @Autowired
    private PointService pointService ;
    
    /**
	 * 方法说明：跳转积分列表页面
	 * @return 视图层
	 */
	@RequestMapping("/toPointList")
	public String toPointList() {
		return "platform/point/pointList";
	}

	/**
	 * 获取支付方式列表
	 * @param name
	 * @param page
	 * @param size
	 * @return
	 */
	@RequestMapping("/getPointList")
	@ResponseBody
	public ReusltItem getPointList(String name, String page,String size){
		ReusltItem item= new ReusltItem();
		try {
			user=SessionState.GetCurrentUser();
			if (StringUtilsEX.ToInt(page) <= 0|| StringUtilsEX.ToInt(size) <= 0) {
				item.setCode(-101);
				item.setDesc("分页参数错误，pageindex:" + page + ",pagesize:" + size);
				return item;
			}
			CriteriaMenu cMenu = new CriteriaMenu();
			if (!StringUtilsEX.IsNullOrWhiteSpace(name)) {
				cMenu.setRolename(name);
			}
			PageBean pBean = pointService.getPointList(cMenu,StringUtilsEX.ToInt(page), StringUtilsEX.ToInt(size));
			item.setCode(0);
			item.setData(pBean.getBeanList());
			item.setMaxRow(pBean.getTr());
			item.setPageIndex(pBean.getPc());
		} catch (Exception e) {
			item.setCode(-900);
			 if (DebugConfig.BLUETOOTH_DEBUG) {
				 item.setDesc("删除角色出现的异常：" + e.getMessage());
				} else {
					item.setDesc("系统错误！");
				}
			LogHandle.error(LogType.Role, "查询支付方式列表出现的异常信息:" , e,"/platform/payMent/getPayModelList");
		}
		return item;
	}

}
