package com.niusales.soft.platform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.niusales.soft.interceptor.PageBean;
import com.niusales.soft.platform.dto.CriteriaMenu;
import com.niusales.soft.platform.service.AreaService;
import com.niusales.soft.platform.service.UserService;
import com.niusales.soft.platform.vo.ReusltItem;
import com.niusales.soft.po.Area;
import com.niusales.soft.po.Dictionary;
import com.niusales.soft.util.DebugConfig;
import com.niusales.soft.util.LogType;
import com.niusales.soft.util.SessionState;
import com.niusales.soft.util.SessionUser;
import com.niusales.soft.util.StringUtilsEX;
import com.yl.soft.log.LogHandle;

/**
 * 类说明：区域管理控制层
 * @auth jiangxl
 * @date 2018-05-27 13:30
 */
@Controller
@RequestMapping("/platform/area")
public class AreaController {
	
	@Autowired
	private AreaService areaService;
	
	/**
	 * 方法说明：跳转到 区域列表页
	 * @return 视图层
	 */
	@RequestMapping("/toAreaList")
	public String toAreaList(){
		return "platform/area/areaList";
	}
	
	/**
	 * 方法说明：跳转到区域编辑页
	 * @param id 区域ID
	 * @param model
	 * @return 视图层
	 */
	@RequestMapping("/toAreaEdit")
	public String toAreaEdit(Integer id, Model model){
		try {
			if(id != null){
				Area area = areaService.getAreaById(id);
				model.addAttribute("area", area);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogHandle.error(LogType.Area, "跳转到区域编辑页出现的异常信息:" , e, "/platform/area/toAreaEdit");
		}
		return "platform/area/areaEdit";
	}
	
	/**
	 * 方法说明：分页查询区域信息
	 * @param name 区域名称
	 * @param page 页码
	 * @param size 每页显示数
	 * @return 分页信息JSON数据
	 */
	@RequestMapping("/getAreaList")
	public @ResponseBody ReusltItem getAreaList(String name, String page, String size) {
		ReusltItem item = new ReusltItem();
		try {
			if (StringUtilsEX.ToInt(page) <= 0
					|| StringUtilsEX.ToInt(size) <= 0) {
				item.setCode(-101);
				item.setDesc("分页参数错误，pageindex:" + page + ",pagesize:" + size);
				return item;
			}
			PageBean pBean = areaService.getAreaList(name, StringUtilsEX.ToInt(page), StringUtilsEX.ToInt(size));
			item.setCode(0);
			item.setData(pBean.getBeanList());
			item.setMaxRow(pBean.getTr());
			item.setPageIndex(pBean.getPc());
		} catch (Exception e) {
			e.printStackTrace();
			item.setCode(-900);
			item.setDesc("系统错误！");
			LogHandle.error(LogType.Area, "查询平台区域列表出现的异常信息:" , e, "/platform/area/getAreaList");
		}
		return item;
	}
	
	/**
	 * 方法说明：删除区域
	 * @param id 区域ID
	 * @return 返回值对象
	 */
	@RequestMapping("/deleteArea")
	public @ResponseBody ReusltItem deleteArea(Integer id) {
		ReusltItem item = new ReusltItem();
		try {
			areaService.deleteAreaById(id);
			item.setCode(0);
		} catch (Exception e) {
			e.printStackTrace();
			item.setCode(-900);
			item.setDesc("系统错误！");
			LogHandle.error(LogType.Area, "删除区域信息出现的异常信息:" , e, "/platform/area/deleteArea");
		}
		return item;
	}
	
	/**
	 * 方法说明：保存区域信息
	 * @param area 区域对象
	 * @return 返回值对象
	 */
	@RequestMapping("/saveArea")
	public @ResponseBody ReusltItem saveArea(Area area) {
		ReusltItem item = new ReusltItem();
		try {
			if(area != null){
				if(area.getId() != null)
					areaService.updateArea(area);
				else
					areaService.insertArea(area);
			}
			item.setCode(0);
		} catch (Exception e) {
			e.printStackTrace();
			item.setCode(-900);
			item.setDesc("系统错误！");
			LogHandle.error(LogType.Area, "保存区域信息出现的异常信息:" , e, "/platform/area/saveArea");
		}
		return item;
	}
}
