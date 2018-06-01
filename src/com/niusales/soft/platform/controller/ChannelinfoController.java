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
import com.niusales.soft.po.Channelinfo;
import com.niusales.soft.platform.service.ChannelinfoService;
import com.niusales.soft.search.CriteriaChannelinfo;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/platform/channelinfo")
public class ChannelinfoController{

    @Autowired
	private ChannelinfoService channelinfoService;
	
       /**
	 *  页面跳转
	 * @return
	 */
	@RequestMapping("/jumpchannelinfo")
	public  String jumpchannelinfo() {
		return "/platform/microPolice/channelinfoList";
	}	

     /**
	 *  页面跳转
	 * @return
	 */
	@RequestMapping("/jumpChannelinfoEdit")
	public String channelinfoEdit(String id, HttpServletRequest request) {
		Channelinfo channelinfo = new Channelinfo();
		try {
			if (StringUtilsEX.ToInt(id) > 0) {
				channelinfo  = channelinfoService.getChannelinfoById(StringUtilsEX.ToInt(id));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("data", channelinfo );
		return "platform/microPolice/channelinfoEdit";
	}

	@RequestMapping("/jumpChannelinfoAdd")
	public String channelinfoAdd(HttpServletRequest request) {
		return "platform/microPolice/channelinfoAdd";
	}

	/**
	 * 查询信息列表
	 * @param pageindex
	 * @param pagesize
	 * @return
	 */
	@RequestMapping("/querylistpage")
	public @ResponseBody ResultItem queryListPage(CriteriaChannelinfo channelinfo,String pageindex, String pagesize) {
		ResultItem item = new ResultItem();
		try {
			int index = StringUtilsEX.ToInt(pageindex);
			index = index == -1 ? 1 : index;
			int size = StringUtilsEX.ToInt(pagesize);
			size = size == -1 ? 10 : size;
			if(channelinfo==null) {
				channelinfo = new CriteriaChannelinfo();
			}
			channelinfo.setICsort("desc");
			PageBean pageBean = channelinfoService.queryListPage(channelinfo, index, size);
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
			LogHandle.error(LogType.Error, "获取列表信息异常:", e, "/platform/channelinfo/querylistpage");
		}
		return item;
	}
	
	/**
	 * 录入数据
	 * @return
	 */
	@RequestMapping("/add")
	public @ResponseBody ResultItem addChannelinfo(Channelinfo channelinfo) {
		ResultItem item = new ResultItem();
		try {
			channelinfoService.addChannelinfo(channelinfo);
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
			LogHandle.error(LogType.Error, "新增异常:", e, "/platform/channelinfo/add");
		}
		return item;
	}
	
	/**
	 * 删除用户
	 * @return
	 */
	@RequestMapping("/del")
	public @ResponseBody ResultItem delChannelinfo(@RequestParam(value="id[]") String[] id) {
		ResultItem item = new ResultItem();
		try {
			List<Integer> list = new ArrayList<Integer>();
			for (String anId : id) {
				list.add(Integer.valueOf(anId));
			}
			channelinfoService.deleteBatchChannelinfo(list);
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
			LogHandle.error(LogType.Error, "删除异常:", e, "/platform/channelinfo/del");
		}
		return item;
	}

	/**
	 * 修改信息
	 * @return
	 */
	@RequestMapping("/edit")
	public @ResponseBody ResultItem editChannelinfo(Channelinfo channelinfo) {
		ResultItem item = new ResultItem();
		try {
			channelinfoService.updateChannelinfo(channelinfo);
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
			LogHandle.error(LogType.Error, "修改信息异常:", e, "/platform/channelinfo/edit");
		}
		return item;
	}
	/**
	 * 查询单个用户
	 * @return
	 */
	@RequestMapping("/findbyid")
	public @ResponseBody ResultItem findbyChannelinfo(Integer id) {
		ResultItem item = new ResultItem();
		try {
			Channelinfo channelinfo= channelinfoService.getChannelinfoById(id);
			item.setData(channelinfo);
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
			LogHandle.error(LogType.Error, "查询异常:", e, "/platform/channelinfo/findbyid");
		}
		return item;
	}
}
