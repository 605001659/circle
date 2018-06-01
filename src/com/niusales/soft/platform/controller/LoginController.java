package com.niusales.soft.platform.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.niusales.soft.enums.UserTypeEnum;
import com.niusales.soft.platform.service.MenuService;
import com.niusales.soft.platform.service.UserService;
import com.niusales.soft.po.Menus;
import com.niusales.soft.search.Platfrom_SYCriteria;
import com.niusales.soft.util.ConstanValue;
import com.niusales.soft.util.DebugConfig;
import com.niusales.soft.util.JsonUtil;
import com.niusales.soft.util.LogType;
import com.niusales.soft.util.ResultItem;
import com.niusales.soft.util.SessionState;
import com.niusales.soft.util.SessionUser;
import com.niusales.soft.util.StringUtilsEX;
import com.yl.soft.log.LogHandle;

/**
 * 类说明：后台登录控制层
 * @author jiangxl
 * @date 2018-05-18 11:04
 */
@Controller("platformLoginController")
@RequestMapping("/platform/login")
public class LoginController {
	
	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private MenuService menuService;
	
	/**
	 * 方法说明：登录成功跳转后台欢迎页
	 * @return 后台欢迎页视图层
	 */
	@RequestMapping(value = "/toLogin", method = RequestMethod.GET)
	public String toLogin(){
		return "platform/login";
	}
	
	@RequestMapping("/loginValidate")
	public @ResponseBody ResultItem  toLogin(String name,String pwd,String ChannelType,String LoginType){
		ResultItem item = new ResultItem();
		try {
			if (StringUtilsEX.IsNullOrWhiteSpace(name)) {
				item.setCode(-102);
				item.setDesc("登录名(name)不能为空！");
				return item;
			}
			if (StringUtilsEX.IsNullOrWhiteSpace(pwd)) {
				item.setCode(-103);
				item.setDesc("密码(pwd)不能为空！");
				return item;
			}
			String token=UUID.randomUUID().toString();

			Integer[] array={UserTypeEnum.SupAdmin.getValue(),UserTypeEnum.Admin.getValue()};
			 String userInfo = "";
	            Object[] rls= userService.queryLogin(name, pwd, array);
	            switch (Integer.parseInt(rls[0].toString())) {
	            case 0:
	                userInfo = JsonUtil.getJsonStringFromObject(rls[1]);
	                item.setDesc("登录成功！");
	                SessionUser sessionUser= (SessionUser)rls[1];
		            SessionState.SetSessionUser(ConstanValue.TOKEN_KEY, sessionUser);
		            item.setToken(token);
		            item.setCode(0);
		            Map<String, Object> map = new HashMap<String, Object>();
		            map.put("Token", token);
		            map.put("Channel", ChannelType);
		            map.put("sessionUser", sessionUser);
		            map.put("UserID",sessionUser.getUserId());
		            map.put("UserName", sessionUser.getLoginName());
		            item.setData(JsonUtil.getJsonStringFromMap(map));
	                break;
	            case -1: 
	                item.setCode(-104);
	                item.setDesc("登录失败,用户名或密码错误！");
	                break;
	            case -2:
	                item.setCode(-105);
	                item.setDesc("登录失败,账户被锁定！");
	                break;
	            case -3:
	                item.setCode(-106);
	                item.setDesc("登录失败,账户异常！");
	                break;
	            default:
	                item.setCode(-104);
	                item.setDesc("登录失败,用户名或密码错误！");
	                break;
	           }
		} catch (Exception e) {
			e.printStackTrace();
		   item.setCode(-900);
            if (DebugConfig.BLUETOOTH_DEBUG) {
            	item.setDesc("平台登录出错：" + e);
			} else {
				item.setDesc("系统错误！");
			}
            LogHandle.error(LogType.platform, "平台登录信息出错! 异常信息:", e,
                "login/loginValidate");
		}
		return item;
	}
	
	@RequestMapping("/index")
	public String index(Model model, HttpServletRequest request) {
		try {
			List<Menus> menus = new ArrayList<Menus>();
			SessionUser sessionUser = SessionState.GetCurrentUser();
			Integer userid = sessionUser.getUserId();
			if (userid != null) {
				String loginname = sessionUser.getLoginName();
				if (loginname != null && sessionUser.getUtype() == 3) {
					menus = menuService.queryAllPlatformMenus(0);
				} else {
					List<Integer> rigths = sessionUser.getRights();
					menus = menuService.queryByRrigth(rigths);
				}
				request.setAttribute("name", loginname);
			}
			model.addAttribute("list", menus);

		} catch (Exception e) {
			e.printStackTrace();
			LogHandle.error(LogType.Login, "查询登录用户所有菜单异常：", e,
					"platform/index");
		}
		return "platform/index";
	}
	
	/**
	 * 显示右边的页面
	 *
	 * @return
	 */
	@RequestMapping("/right")
	public String right(HttpServletRequest request, Model model) {
		try {
			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			String end = formatter.format(calendar.getTime());
			calendar.add(Calendar.DAY_OF_MONTH, -6);
			String start = formatter.format(calendar.getTime());

			Platfrom_SYCriteria criteria = new Platfrom_SYCriteria();
			criteria.setBegintime(start);
			criteria.setEndtime(end);

//			List<Accounts> accoutslist = accountsService
//					.queryAccounts(criteria);
			Integer accountCount = 0;
			Integer ordercount = 0;
			Float orderPrice = 0.0f;

//			if (accoutslist != null) {
//				accountCount = accoutslist.size();
//			}
			model.addAttribute("accounts", accountCount);

			//Map<String, Object> map = orderService.queryOrders(criteria);
			//ordercount = ParseUtil.toInteger(map.get("orderCount"));
			//orderPrice = Float.parseFloat(map.get("orderprice").toString());

			//model.addAttribute("orderCount", ordercount);
			//model.addAttribute("orderprice", orderPrice);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "platform/salesStatistics";
	}
}
