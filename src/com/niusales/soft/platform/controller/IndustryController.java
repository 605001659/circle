package com.niusales.soft.platform.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.ArrayList;
import com.niusales.soft.util.StringUtilsEX;
import com.niusales.soft.util.ResultItem;
import com.niusales.soft.interceptor.PageBean;
import com.niusales.soft.util.DebugConfig;
import com.niusales.soft.util.LogType;
import com.yl.soft.log.LogHandle;
import com.niusales.soft.po.Industry;
import com.niusales.soft.platform.service.IndustryService;
import com.niusales.soft.search.CriteriaIndustry;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/platform/industry")
public class IndustryController{

    @Autowired
	private IndustryService industryService;
	
       /**
	 *  页面跳转
	 * @return
	 */
	@RequestMapping("/jumpindustry")
	public  String jumpindustry() {
		return "/platform/microPolice/industryList";
	}	

	@RequestMapping("/jumpIndustryEdit")
	public String industryEdit(String id, HttpServletRequest request) {
		Industry industry = new Industry();
		try {
			if (StringUtilsEX.ToInt(id) > 0) {
				industry  = industryService.getIndustryById(StringUtilsEX.ToInt(id));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("data", industry );
		return "platform/microPolice/industryEdit";
	}

	@RequestMapping("/jumpIndustryAdd")
	public String industryAdd(HttpServletRequest request) {
		return "platform/microPolice/industryAdd";
	}

	/**
	 * 查询信息列表
	 * @param pageindex
	 * @param pagesize
	 * @return
	 */
	@RequestMapping("/querylistpage")
	public @ResponseBody ResultItem queryListPage(CriteriaIndustry industry,String pageindex, String pagesize) {
		ResultItem item = new ResultItem();
		try {
			int index = StringUtilsEX.ToInt(pageindex);
			index = index == -1 ? 1 : index;
			int size = StringUtilsEX.ToInt(pagesize);
			size = size == -1 ? 10 : size;
			if(industry==null) {
				industry = new CriteriaIndustry();
			}
			industry.setICsort("desc");
			PageBean pageBean = industryService.queryListPage(industry, index, size);
			item.setCode(0);
			item.setData(pageBean.getBeanList());
			item.setMaxRow(pageBean.getTr());
			item.setPageIndex(pageBean.getPc());
		} catch (Exception e) {
			e.printStackTrace();
			item.setCode(-900);
			if (DebugConfig.BLUETOOTH_DEBUG) {
				item.setDesc("获取列表信息出错" + e);
			} else {
				item.setDesc("系统错误！");
			}
			LogHandle.error(LogType.Error, "获取列表信息异常:", e, "/platform/industry/querylistpage");
		}
		return item;
	}
	
	/**
	 * 录入数据
	 * @return
	 */
	@RequestMapping("/add")
	public @ResponseBody ResultItem addIndustry(Industry industry) {
		ResultItem item = new ResultItem();
		try {
			industryService.addIndustry(industry);
			item.setCode(0);
			item.setDesc("新增成功");
		} catch (Exception e) {
			e.printStackTrace();
			item.setCode(-900);
			if (DebugConfig.BLUETOOTH_DEBUG) {
				item.setDesc("新增信息出错：" + e);
			} else {
				item.setDesc("系统错误！");
			}
			LogHandle.error(LogType.Error, "新增异常:", e, "/platform/industry/add");
		}
		return item;
	}
	
	/**
	 * 删除用户
	 * @param id
	 * @return
	 */
	@RequestMapping("/del")
	public @ResponseBody ResultItem delIndustry(@RequestParam(value="id[]") String[] id) {
		ResultItem item = new ResultItem();
		try {
			List<Integer> list = new ArrayList<Integer>();
			for (String anId : id) {
				list.add(Integer.valueOf(anId));
			}
			industryService.deleteBatchIndustry(list);
			item.setCode(0);
			item.setDesc("删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			item.setCode(-900);
			if (DebugConfig.BLUETOOTH_DEBUG) {
				item.setDesc("删除出错：" + e);
			} else {
				item.setDesc("系统错误！");
			}
			LogHandle.error(LogType.Error, "删除异常:", e, "/platform/industry/del");
		}
		return item;
	}

	/**
	 * 修改信息
	 * @return
	 */
	@RequestMapping("/edit")
	public @ResponseBody ResultItem editIndustry(Industry industry) {
		ResultItem item = new ResultItem();
		try {
			industryService.updateIndustry(industry);
			item.setCode(0);
			item.setDesc("修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			item.setCode(-900);
			if (DebugConfig.BLUETOOTH_DEBUG) {
				item.setDesc("修改信息出错：" + e);
			} else {
				item.setDesc("系统错误！");
			}
			LogHandle.error(LogType.Error, "修改信息异常:", e, "/platform/industry/edit");
		}
		return item;
	}
	/**
	 * 查询单个用户
	 * @param id
	 * @return
	 */
	@RequestMapping("/findbyid")
	public @ResponseBody ResultItem findbyIndustry(Integer id) {
		ResultItem item = new ResultItem();
		try {
			Industry industry= industryService.getIndustryById(id);
			item.setData(industry);
			item.setCode(0);
			item.setDesc("查询成功");
		} catch (Exception e) {
			e.printStackTrace();
			item.setCode(-900);
			if (DebugConfig.BLUETOOTH_DEBUG) {
				item.setDesc("查询信息出错：" + e);
			} else {
				item.setDesc("系统错误！");
			}
			LogHandle.error(LogType.Error, "查询异常:", e, "/platform/industry/findbyid");
		}
		return item;
	}
}
