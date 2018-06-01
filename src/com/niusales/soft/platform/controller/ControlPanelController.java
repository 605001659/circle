package com.niusales.soft.platform.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.niusales.soft.platform.dto.AccountsDTO;
import com.niusales.soft.platform.service.MenuService;
import com.niusales.soft.platform.service.RoleService;
import com.niusales.soft.platform.service.UserService;
import com.niusales.soft.po.Menus;
import com.niusales.soft.po.Role;
import com.niusales.soft.util.StringUtilsEX;


@Controller
@RequestMapping("/platform/controlpanel")
public class ControlPanelController {

	private static final Logger logger = LoggerFactory.getLogger(ControlPanelController.class);

	@Autowired
	private MenuService menuService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private UserService userService;

	/**
	 * 显示部门管理页面
	 * 
	 * @return
	 */
	@RequestMapping("/department_list")
	public String department_list(Model model) {

		return "platform/controlpanel/Department_list";
	}



	/**
	 * 操作员列表
	 * 
	 * @return
	 */
	@RequestMapping("/operator_list")
	public String operator_list() {
		return "platform/controlpanel/operator_list";
	}

	/**
	 * 操作员编辑
	 * 
	 * @param id
	 * @param request
	 * @return
	 */
	@RequestMapping("/operator_edit")
	public String operator_edit(String id, HttpServletRequest request) {
		AccountsDTO dto = new AccountsDTO();
		String actionString = "addOperator";
		String fid = "", sid = "", tid = "";
		try {
			if (StringUtilsEX.ToInt(id) > 0) {
				dto = userService.selectScByID(StringUtilsEX.ToInt(id));
				actionString = "updateOperator";
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		request.setAttribute("data", dto);
		request.setAttribute("action", actionString);
		request.setAttribute("fid", fid);
		request.setAttribute("sid", sid);
		request.setAttribute("tid", tid);
		return "platform/controlpanel/operator_edit";
	}

	/**
	 * 显示发送模板页面
	 * 
	 * @return
	 */
	@RequestMapping("/showSendTemplate")
	public String showSendTemplate() {

		return "platform/controlpanel/sendTemplate";
	}

	/**
	 * 显示添加和编辑页面
	 * 
	 * @return
	 */
	@RequestMapping("/showSendTemplateEdit")
	public String showSendTemplateEdit() {

		return "platform/controlpanel/sendTemplateEdit";
	}

	@RequestMapping("/UserOperater")
	public String UserOperater() {
		return "platform/controlpanel/UserOperater";
	}
    /**
     * app最新版本号配置
     * @return
     */
    @RequestMapping("/Control_Version")
    public String Control_Version(){
    	return "platform/controlpanel/Control_Version";
    }

    
}
