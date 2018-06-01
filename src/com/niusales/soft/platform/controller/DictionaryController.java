package com.niusales.soft.platform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.niusales.soft.interceptor.PageBean;
import com.niusales.soft.platform.service.DictionaryService;
import com.niusales.soft.platform.vo.ReusltItem;
import com.niusales.soft.po.Dictionary;
import com.niusales.soft.util.LogType;
import com.niusales.soft.util.StringUtilsEX;
import com.yl.soft.log.LogHandle;

@Controller
@RequestMapping("/platform/dictionary")
public class DictionaryController {
	
	@Autowired
	private DictionaryService dictionaryService;

	/**
	 * 方法说明：跳转到数据字典列表页
	 * @return 视图层
	 */
	@RequestMapping("/toDictionaryList")
	public String toDictionaryList(){
		return "platform/dictionary/dictionaryList";
	}
	
	/**
	 * 方法说明：跳转到数据字典编辑页
	 * @param id 字典ID
	 * @param model
	 * @return 视图层
	 */
	@RequestMapping("/toDictionaryEdit")
	public String toDictionaryEdit(Integer id, Model model){
		try {
			if(id != null){
				Dictionary dic = dictionaryService.getDictionaryById(id);
				model.addAttribute("dic", dic);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogHandle.error(LogType.Dictionary, "跳转到数据字典编辑页出现的异常信息:" , e, "/platform/dictionary/toDictionaryEdit");
		}
		return "platform/dictionary/dictionaryEdit";
	}
	
	/**
	 * 方法说明：分页查询数据字典信息
	 * @param name 字典名称
	 * @param page 页码
	 * @param size 每页显示数
	 * @return 分页信息JSON数据
	 */
	@RequestMapping("/getDictionaryList")
	public @ResponseBody ReusltItem getDictionaryList(String name, String page, String size) {
		ReusltItem item = new ReusltItem();
		try {
			if (StringUtilsEX.ToInt(page) <= 0
					|| StringUtilsEX.ToInt(size) <= 0) {
				item.setCode(-101);
				item.setDesc("分页参数错误，pageindex:" + page + ",pagesize:" + size);
				return item;
			}
			PageBean pBean = dictionaryService.getDictionaryList(name, StringUtilsEX.ToInt(page), StringUtilsEX.ToInt(size));
			item.setCode(0);
			item.setData(pBean.getBeanList());
			item.setMaxRow(pBean.getTr());
			item.setPageIndex(pBean.getPc());
		} catch (Exception e) {
			e.printStackTrace();
			item.setCode(-900);
			item.setDesc("系统错误！");
			LogHandle.error(LogType.Dictionary, "查询数据字典列表出现的异常信息:" , e, "/platform/dictionary/getDictionaryList");
		}
		return item;
	}
	
	/**
	 * 方法说明：删除数据字典
	 * @param id 字典ID
	 * @return 返回值对象
	 */
	@RequestMapping("/deleteDictionary")
	public @ResponseBody ReusltItem deleteDictionary(Integer id) {
		ReusltItem item = new ReusltItem();
		try {
			dictionaryService.deleteDictionaryById(id);
			item.setCode(0);
		} catch (Exception e) {
			e.printStackTrace();
			item.setCode(-900);
			item.setDesc("系统错误！");
			LogHandle.error(LogType.Dictionary, "删除数据字典出现的异常信息:" , e, "/platform/dictionary/deleteDictionary");
		}
		return item;
	}
	
	/**
	 * 方法说明：保存数据字典
	 * @param dic 数据字典对象
	 * @return 返回值对象
	 */
	@RequestMapping("/saveDictionary")
	public @ResponseBody ReusltItem saveDictionary(Dictionary dic) {
		ReusltItem item = new ReusltItem();
		try {
			if(dic != null){
				if(dic.getId() != null)
					dictionaryService.updateDictionary(dic);
				else
					dictionaryService.insertDictionary(dic);
			}
			item.setCode(0);
		} catch (Exception e) {
			e.printStackTrace();
			item.setCode(-900);
			item.setDesc("系统错误！");
			LogHandle.error(LogType.Dictionary, "保存数据字典出现的异常信息:" , e, "/platform/dictionary/saveDictionary");
		}
		return item;
	}
}
