package com.niusales.soft.platform.controller;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.niusales.soft.enums.OperateRecordsFromEnum;
import com.niusales.soft.enums.OperateRecordsTypeEnum;
import com.niusales.soft.interceptor.PageBean;
import com.niusales.soft.platform.dto.CriteriaMenu;
import com.niusales.soft.platform.service.OperaterecordsService;
import com.niusales.soft.platform.service.PayMentService;
import com.niusales.soft.platform.service.PayTypeService;
import com.niusales.soft.platform.vo.ReusltItem;
import com.niusales.soft.po.Paymode;
import com.niusales.soft.po.Paytype;
import com.niusales.soft.util.DebugConfig;
import com.niusales.soft.util.LogType;
import com.niusales.soft.util.SessionState;
import com.niusales.soft.util.SessionUser;
import com.niusales.soft.util.StringUtilsEX;
import com.yl.soft.log.LogHandle;

@Controller
@RequestMapping("/platform/payMent")
public class PayMentController {
	
	@Autowired
	private PayMentService payMentService;
	
	@Autowired
	private PayTypeService payTypeService;
	
	@Autowired
    private OperaterecordsService operaterecordsService ;
	
	SessionUser user=null;
	
	/**
	 * 跳转到支付方式列表页面
	 * @return
	 */
	@RequestMapping("/toPayModeList")
	public String toPayModeList(){
		return "platform/payment/paymodeList";
	}
	
	/**
	 * 获取支付方式列表
	 * @param name
	 * @param page
	 * @param size
	 * @return
	 */
	@RequestMapping("/getPayModelList")
	@ResponseBody
	public ReusltItem getPayModelList(String name, String page,String size){
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
			PageBean pBean = payMentService.getPayModelList(cMenu,StringUtilsEX.ToInt(page), StringUtilsEX.ToInt(size));
			item.setCode(0);
			item.setData(pBean.getBeanList());
			item.setMaxRow(pBean.getTr());
			item.setPageIndex(pBean.getPc());
		} catch (Exception e) {
			item.setCode(-900);
			 if (DebugConfig.BLUETOOTH_DEBUG) {
				 item.setDesc("获取支付方式出现的异常：" + e.getMessage());
				} else {
					item.setDesc("系统错误！");
				}
			LogHandle.error(LogType.Role, "查询支付方式列表出现的异常信息:" , e,"/platform/payMent/getPayModelList");
		}
		return item;
	}
	
	/**
	 * 跳转到添加支付方式页面
	 * @param id
	 * @param request
	 * @return
	 */
	@RequestMapping("/toPaymodeEdit")
	public String toPaymodeEdit(String id, HttpServletRequest request){
		Paymode paymode= new Paymode();
		String roleaction = "addPaymode";
		try {
			if (StringUtilsEX.ToInt(id) > 0) {
				paymode = payMentService.getPaymodeById(StringUtilsEX.ToInt(id));
				if (paymode != null) {
					roleaction = "updatePaymoth";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("data", paymode);
		request.setAttribute("roleaction", roleaction);
		return "platform/payment/paymodeEdit";
	}
	
	/**
	 * 添加支付方式
	 * @param name
	 * @param currencydic
	 * @param fee
	 * @param online
	 * @param interfacetype
	 * @param draworder
	 * @param describes
	 * @return
	 */
	@RequestMapping("/addPaymode")
	@ResponseBody
	public ReusltItem addPaymode(String name,String currencydic,String fee,String online,String interfacetype,String draworder,String describes){
		ReusltItem item= new ReusltItem();
		try {
			user=SessionState.GetCurrentUser();
			if (StringUtilsEX.IsNullOrWhiteSpace(name)) {
				item.setCode(-101);
				item.setDesc("支付方式名称不能为空");
				return item;
			}
			if (StringUtilsEX.ToInt(currencydic) < 0) {
				item.setCode(-102);
				item.setDesc("支持交易货币状态参数错误，currencydic:" + currencydic);
				return item;
			}
			if (StringUtilsEX.IsNullOrWhiteSpace(fee)) {
				item.setCode(-103);
				item.setDesc("手续费不能为空，fee:" + fee);
				return item;
			}
			if (StringUtilsEX.ToInt(online) < 0) {
				item.setCode(-104);
				item.setDesc("支持在线支付状态参数错误，online:" + online);
				return item;
			}
			if (StringUtilsEX.ToInt(interfacetype) < 0) {
				item.setCode(-105);
				item.setDesc("接口类型参数错误，interfacetype:" + interfacetype);
				return item;
			}
			if (StringUtilsEX.ToInt(draworder) < 0) {
				item.setCode(-102);
				item.setDesc("显示顺序参数错误，draworder:" + draworder);
				return item;
			}
			if (StringUtilsEX.IsNullOrWhiteSpace(describes)) {
				item.setCode(-102);
				item.setDesc("描述参数错误，describes:" + describes);
				return item;
			}
			Paymode paymode= new Paymode();
			paymode.setCreatetime(new Date());
			paymode.setName(name);
			paymode.setCurrencydic(StringUtilsEX.ToInt(currencydic));
			paymode.setFee(new BigDecimal(fee.trim()));
			paymode.setOnline(StringUtilsEX.ToInt(online));
			paymode.setInterfacetype(StringUtilsEX.ToInt(interfacetype));
			paymode.setDraworder(StringUtilsEX.ToInt(draworder));
			paymode.setDescribes(describes);
			
			if (payMentService.insert(paymode) > 0) {
				item.setCode(0);
				item.setDesc("添加支付方式成功");
				LogHandle.info(LogType.Role, MessageFormat.format("添加支付方式成功!名称:{0},操作人ID:{1}", name,user.getUserId())
						,"/role/addRole");
				//异步操作 不影响正常流程
                ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    			cachedThreadPool.execute(() -> {
					try{
						operaterecordsService.insertOperaterecords(OperateRecordsTypeEnum.添加.getValue(), OperateRecordsFromEnum.系统后台.getValue(),
								user.getUserId(), user.getLoginName(), "Control_RoleEdit.jsp", "/platform/payMent/addPaymode", "添加支付方式");
					}
					catch(Exception e){
						LogHandle.error(LogType.OperateRecords,"添加支付方式操作记录出错! 异常信息:",
								e, "/platform/payMent/addRole");
					}
				});
			} else {
				item.setCode(-200);
				item.setDesc("添加支付方式失败");
				LogHandle.info(LogType.Role, MessageFormat.format("添加支付方式失败!名称:{0},操作人ID:{1}", name,user.getUserId()),
						"/payMent/addPaymode");
			}
		} catch (Exception e) {
			item.setCode(-900);
			 if (DebugConfig.BLUETOOTH_DEBUG) {
				 e.printStackTrace();
				 item.setDesc("添加支付方式出现的异常：" + e.getMessage());
				} else {
					item.setDesc("系统错误！");
				}
			LogHandle.error(LogType.Role, "添加支付方式出现的异常信息:" ,e,"/platform/payMent/addPaymode");
		}
		return item; 
	}
	
	@RequestMapping("/deletePaymode")
	@ResponseBody
	public ReusltItem deletePaymode(String id){
		
		ReusltItem item = new ReusltItem();
		try {
			user=SessionState.GetCurrentUser();
			if (StringUtilsEX.ToInt(id) <= 0) {
				item.setCode(-101);
				item.setDesc("角色ID参数错误，id:" + id);
				return item;
			}
			/*if(userService.getByRoleID(StringUtilsEX.ToInt(id))!=null){
				item.setCode(-102);
				item.setDesc("支付方式已有使用，不能删除！");
				return item;
			}*/
			if (payMentService.delete(StringUtilsEX.ToInt(id)) > 0) {
				item.setCode(0);
				item.setDesc("删除支付方式成功");
				LogHandle.info(LogType.Role, MessageFormat.format("删除支付方式成功! ID:{0},操作人ID:{1}",
						id,user.getUserId()),"/payMent/deletePaymode");
				//异步操作 不影响正常流程
                ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    			cachedThreadPool.execute(new Runnable() {
    				@Override
    				public void run() {
    					try{
    						operaterecordsService.insertOperaterecords(
                            		OperateRecordsTypeEnum.删除.getValue(), OperateRecordsFromEnum.系统后台.getValue(),
                            		user.getUserId(), user.getLoginName(), "Control_paymothList.jsp", "/platform/payMent/deletePaymode", "删除支付方式");
    					}
    					catch(Exception e){
    						LogHandle.error(LogType.OperateRecords,"删除支付方式操作记录出错! 异常信息:",
    								e, "/platform/payMent/deletePaymode");
    					}
    					
    				}
    			});
			} else {
				item.setCode(-200);
				item.setDesc("删除支付方式失败");
				LogHandle.info(LogType.Role, MessageFormat.format("删除支付方式失败! ID:{0},操作人ID:{1}",
						id,user.getUserId()),"/payMent/deletePaymode");
			}
		} catch (Exception e) {
			item.setCode(-900);
			 if (DebugConfig.BLUETOOTH_DEBUG) {
				 item.setDesc("删除支付方式的异常：" + e.getMessage());
				} else {
					item.setDesc("系统错误！");
				}
			LogHandle.error(LogType.Role, "删除支付方式的异常信息:" , e,"/platform/payMent/deletePaymode");
		}
		return item;
	}
	
	/**
	 * 更新支付方式
	 * @param id
	 * @param name
	 * @param currencydic
	 * @param fee
	 * @param online
	 * @param interfacetype
	 * @param draworder
	 * @param describes
	 * @return
	 */
	@RequestMapping("/updatePaymoth")
	@ResponseBody
	public ReusltItem updatePaymoth(String id,String name,String currencydic,String fee,String online,String interfacetype,String draworder,String describes){
		ReusltItem item= new ReusltItem();
		try {
			user=SessionState.GetCurrentUser();
			if (StringUtilsEX.ToInt(id) <= 0) {
				item.setCode(-100);
				item.setDesc("支付方式ID参数错误，id:" + id);
				return item;
			}
			if (StringUtilsEX.IsNullOrWhiteSpace(name)) {
				item.setCode(-101);
				item.setDesc("支付方式名称不能为空");
				return item;
			}
			if (StringUtilsEX.ToInt(currencydic) < 0) {
				item.setCode(-102);
				item.setDesc("支持交易货币状态参数错误，currencydic:" + currencydic);
				return item;
			}
			if (StringUtilsEX.IsNullOrWhiteSpace(fee)) {
				item.setCode(-103);
				item.setDesc("手续费不能为空，fee:" + fee);
				return item;
			}
			if (StringUtilsEX.ToInt(online) < 0) {
				item.setCode(-104);
				item.setDesc("支持在线支付状态参数错误，online:" + online);
				return item;
			}
			if (StringUtilsEX.ToInt(interfacetype) < 0) {
				item.setCode(-105);
				item.setDesc("接口类型参数错误，interfacetype:" + interfacetype);
				return item;
			}
			if (StringUtilsEX.ToInt(draworder) < 0) {
				item.setCode(-102);
				item.setDesc("显示顺序参数错误，draworder:" + draworder);
				return item;
			}
			if (StringUtilsEX.IsNullOrWhiteSpace(describes)) {
				item.setCode(-102);
				item.setDesc("描述参数错误，describes:" + describes);
				return item;
			}
			Paymode paymode= new Paymode();
			paymode.setCreatetime(new Date());
			paymode.setId(StringUtilsEX.ToInt(id));
			paymode.setName(name);
			paymode.setCurrencydic(StringUtilsEX.ToInt(currencydic));
			paymode.setFee(new BigDecimal(fee.trim()));
			paymode.setOnline(StringUtilsEX.ToInt(online));
			paymode.setInterfacetype(StringUtilsEX.ToInt(interfacetype));
			paymode.setDraworder(StringUtilsEX.ToInt(draworder));
			paymode.setDescribes(describes);
			
			if (payMentService.update(paymode) > 0) {
				item.setCode(0);
				item.setDesc("编辑支付方式成功");
				LogHandle.info(LogType.Role, MessageFormat.format("编辑支付方式成功!名称:{0},操作人ID:{1}", name,user.getUserId())
						,"/role/updatePaymoth");
				//异步操作 不影响正常流程
                ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    			cachedThreadPool.execute(() -> {
					try{
						operaterecordsService.insertOperaterecords(OperateRecordsTypeEnum.添加.getValue(), OperateRecordsFromEnum.系统后台.getValue(),
								user.getUserId(), user.getLoginName(), "Control_RoleEdit.jsp", "/platform/payMent/updatePaymoth", "添加支付方式");
					}
					catch(Exception e){
						LogHandle.error(LogType.OperateRecords,"编辑支付方式操作记录出错! 异常信息:",
								e, "/platform/payMent/updatePaymoth");
					}
				});
			} else {
				item.setCode(-200);
				item.setDesc("编辑支付方式失败");
				LogHandle.info(LogType.Role, MessageFormat.format("编辑支付方式失败!名称:{0},操作人ID:{1}", name,user.getUserId()),
						"/payMent/updatePaymoth");
			}
		} catch (Exception e) {
			item.setCode(-900);
			 if (DebugConfig.BLUETOOTH_DEBUG) {
				 e.printStackTrace();
				 item.setDesc("编辑支付方式出现的异常：" + e.getMessage());
				} else {
					item.setDesc("系统错误！");
				}
			LogHandle.error(LogType.Role, "编辑支付方式出现的异常信息:" ,e,"/platform/payMent/updatePaymoth");
		}
		return item; 
	}
	
	/**
	 * 跳转到支付类型列表页面
	 * @return
	 */
	@RequestMapping("/toPayTypeList")
	public String toPayTypeList(){
		return "platform/payment/paytypeList";
	}
	
	/**
	 * 获取支付类型数据
	 * @param name
	 * @param page
	 * @param size
	 * @return
	 */
	@RequestMapping("/getPayTypeList")
	@ResponseBody
	public ReusltItem getPayTypeList(String name, String page,String size){
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
			PageBean pBean = payTypeService.getPayModelList(cMenu,StringUtilsEX.ToInt(page), StringUtilsEX.ToInt(size));
			item.setCode(0);
			item.setData(pBean.getBeanList());
			item.setMaxRow(pBean.getTr());
			item.setPageIndex(pBean.getPc());
		} catch (Exception e) {
			item.setCode(-900);
			 if (DebugConfig.BLUETOOTH_DEBUG) {
				 item.setDesc("获取支付类型出现的异常：" + e.getMessage());
				} else {
					item.setDesc("系统错误！");
				}
			LogHandle.error(LogType.Role, "查询支付类型列表出现的异常信息:" , e,"/platform/payMent/getPayTypeList");
		}
		return item;
	}
	
	/**
	 * 跳转到添加支付方式页面
	 * @param id
	 * @param request
	 * @return
	 */
	@RequestMapping("/toPaytypeEdit")
	public String toPaytypeEdit(String id, HttpServletRequest request){
		Paytype paytype= new Paytype();
		String roleaction = "addPaytype";
		try {
			if (StringUtilsEX.ToInt(id) > 0) {
				paytype = payTypeService.getPaymodeById(StringUtilsEX.ToInt(id));
				if (paytype != null) {
					roleaction = "updatePaytype";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("data", paytype);
		request.setAttribute("roleaction", roleaction);
		return "platform/payment/paytypeEdit";
	}
	
	/**
	 * 添加支付类型
	 * @param channeldic
	 * @param interfacetype
	 * @param merchantnum
	 * @param accountnum
	 * @param url
	 * @param status
	 * @param describes
	 * @return
	 */
	@RequestMapping("/addPaytype")
	@ResponseBody
	public ReusltItem addPaytype(String channeldic,String interfacetype,String merchantnum,String accountnum,String url,String status,String describes){
		ReusltItem item= new ReusltItem();
		try {
			user=SessionState.GetCurrentUser();
			if (StringUtilsEX.ToInt(channeldic) < 0) {
				item.setCode(-101);
				item.setDesc("支付通道不能为空");
				return item;
			}
			if (StringUtilsEX.ToInt(interfacetype) < 0) {
				item.setCode(-102);
				item.setDesc("接入应用状态参数错误，interfacetype:" + interfacetype);
				return item;
			}
			if (!StringUtilsEX.IsNullOrWhiteSpace(merchantnum)) {
				item.setCode(-103);
				item.setDesc("商户号不能为空，merchantnum:" + merchantnum);
				return item;
			}
			if (!StringUtilsEX.IsNullOrWhiteSpace(accountnum)) {
				item.setCode(-104);
				item.setDesc("收款账号值不能为空，accountnum:" + accountnum);
				return item;
			}
			if (!StringUtilsEX.IsNullOrWhiteSpace(url)) {
				item.setCode(-105);
				item.setDesc("调用地址不能为空，url:" + url);
				return item;
			}
			if (StringUtilsEX.ToInt(status) < 0) {
				item.setCode(-106);
				item.setDesc("是否支持在线支付状态不能为空，status:" + status);
				return item;
			}
			if (!StringUtilsEX.IsNullOrWhiteSpace(describes)) {
				item.setCode(-107);
				item.setDesc("描述参数错误，describes:" + describes);
				return item;
			}
			Paytype paytype= new Paytype();
			paytype.setCreatetime(new Date());
			paytype.setChanneldic(StringUtilsEX.ToInt(channeldic));
			paytype.setInterfacetype(StringUtilsEX.ToInt(interfacetype));
			paytype.setMerchantnum(merchantnum);
			paytype.setAccountnum(accountnum);
			paytype.setUrl(url);
			paytype.setStatus(StringUtilsEX.ToInt(status));
			paytype.setDescribes(describes);
			
			if (payTypeService.insert(paytype) > 0) {
				item.setCode(0);
				item.setDesc("添加支付类型成功");
				LogHandle.info(LogType.Role, MessageFormat.format("添加支付类型成功!名称:{0},操作人ID:{1}", channeldic,user.getUserId())
						,"/payment/addPaytype");
				//异步操作 不影响正常流程
                ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    			cachedThreadPool.execute(() -> {
					try{
						operaterecordsService.insertOperaterecords(OperateRecordsTypeEnum.添加.getValue(), OperateRecordsFromEnum.系统后台.getValue(),
								user.getUserId(), user.getLoginName(), "addPaytype.jsp", "/platform/payMent/addPaytype", "添加支付类型");
					}
					catch(Exception e){
						LogHandle.error(LogType.OperateRecords,"添加支付类型操作记录出错! 异常信息:",
								e, "/platform/payMent/addPaytype");
					}
				});
			} else {
				item.setCode(-200);
				item.setDesc("添加类型方式失败");
				LogHandle.info(LogType.Role, MessageFormat.format("添加类型方式失败!名称:{0},操作人ID:{1}", channeldic,user.getUserId()),
						"/payMent/addPaytype");
			}
		} catch (Exception e) {
			item.setCode(-900);
			 if (DebugConfig.BLUETOOTH_DEBUG) {
				 e.printStackTrace();
				 item.setDesc("添加类型方式出现的异常：" + e.getMessage());
				} else {
					item.setDesc("系统错误！");
				}
			LogHandle.error(LogType.Role, "添加类型方式出现的异常信息:" ,e,"/platform/payMent/addPaytype");
		}
		return item; 
	}
	
	@RequestMapping("/deletePaytype")
	@ResponseBody
	public ReusltItem deletePaytype(String id){
		
		ReusltItem item = new ReusltItem();
		try {
			user=SessionState.GetCurrentUser();
			if (StringUtilsEX.ToInt(id) <= 0) {
				item.setCode(-101);
				item.setDesc("支付类型ID参数错误，id:" + id);
				return item;
			}
			/*if(userService.getByRoleID(StringUtilsEX.ToInt(id))!=null){
				item.setCode(-102);
				item.setDesc("支付方式已有使用，不能删除！");
				return item;
			}*/
			if (payTypeService.delete(StringUtilsEX.ToInt(id)) > 0) {
				item.setCode(0);
				item.setDesc("删除支付类型成功");
				LogHandle.info(LogType.Role, MessageFormat.format("删除支付类型成功! ID:{0},操作人ID:{1}",
						id,user.getUserId()),"/payMent/deletePaytype");
				//异步操作 不影响正常流程
                ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    			cachedThreadPool.execute(new Runnable() {
    				@Override
    				public void run() {
    					try{
    						operaterecordsService.insertOperaterecords(
                            		OperateRecordsTypeEnum.删除.getValue(), OperateRecordsFromEnum.系统后台.getValue(),
                            		user.getUserId(), user.getLoginName(), "Control_paytypeList.jsp", "/platform/payMent/deletePaytype", "删除支付类型");
    					}
    					catch(Exception e){
    						LogHandle.error(LogType.OperateRecords,"删除支付类型操作记录出错! 异常信息:",
    								e, "/platform/payMent/deletePaytype");
    					}
    					
    				}
    			});
			} else {
				item.setCode(-200);
				item.setDesc("删除支付方式失败");
				LogHandle.info(LogType.Role, MessageFormat.format("删除支付类型失败! ID:{0},操作人ID:{1}",
						id,user.getUserId()),"/payMent/deletePaytype");
			}
		} catch (Exception e) {
			item.setCode(-900);
			 if (DebugConfig.BLUETOOTH_DEBUG) {
				 item.setDesc("删除支付类型的异常：" + e.getMessage());
				} else {
					item.setDesc("系统错误！");
				}
			LogHandle.error(LogType.Role, "删除支付类型的异常信息:" , e,"/platform/payMent/deletePaytype");
		}
		return item;
	}
	
	/**
	 * 更新支付方式
	 * @param id
	 * @param name
	 * @param currencydic
	 * @param fee
	 * @param online
	 * @param interfacetype
	 * @param draworder
	 * @param describes
	 * @return
	 */
	@RequestMapping("/updatePaytype")
	@ResponseBody
	public ReusltItem updatePaytype(String id,String channeldic,String interfacetype,String merchantnum,String accountnum,String url,String status,String describes){
		ReusltItem item= new ReusltItem();
		try {
			user=SessionState.GetCurrentUser();
			if (StringUtilsEX.ToInt(id) < 0) {
				item.setCode(-100);
				item.setDesc("id不能为空");
				return item;
			}
			if (StringUtilsEX.ToInt(channeldic) < 0) {
				item.setCode(-101);
				item.setDesc("支付通道不能为空");
				return item;
			}
			if (StringUtilsEX.ToInt(interfacetype) < 0) {
				item.setCode(-102);
				item.setDesc("接入应用状态参数错误，interfacetype:" + interfacetype);
				return item;
			}
			if (StringUtilsEX.IsNullOrWhiteSpace(merchantnum)) {
				item.setCode(-103);
				item.setDesc("商户号不能为空，merchantnum:" + merchantnum);
				return item;
			}
			if (StringUtilsEX.IsNullOrWhiteSpace(accountnum)) {
				item.setCode(-104);
				item.setDesc("收款账号值不能为空，accountnum:" + accountnum);
				return item;
			}
			if (StringUtilsEX.IsNullOrWhiteSpace(url)) {
				item.setCode(-105);
				item.setDesc("调用地址不能为空，url:" + url);
				return item;
			}
			if (StringUtilsEX.ToInt(status) < 0) {
				item.setCode(-106);
				item.setDesc("是否支持在线支付状态不能为空，status:" + status);
				return item;
			}
			if (StringUtilsEX.IsNullOrWhiteSpace(describes)) {
				item.setCode(-107);
				item.setDesc("描述参数错误，describes:" + describes);
				return item;
			}
			Paytype paytype= new Paytype();
			paytype.setId(StringUtilsEX.ToInt(id));
			paytype.setCreatetime(new Date());
			paytype.setChanneldic(StringUtilsEX.ToInt(channeldic));
			paytype.setInterfacetype(StringUtilsEX.ToInt(interfacetype));
			paytype.setMerchantnum(merchantnum);
			paytype.setAccountnum(accountnum);
			paytype.setUrl(url);
			paytype.setStatus(StringUtilsEX.ToInt(status));
			paytype.setDescribes(describes);
			
			if (payTypeService.update(paytype) > 0) {
				item.setCode(0);
				item.setDesc("编辑支付类型型成功");
				LogHandle.info(LogType.Role, MessageFormat.format("编辑支付类型类型成功!名称:{0},操作人ID:{1}", channeldic,user.getUserId())
						,"/payment/updatePaytype");
				//异步操作 不影响正常流程
                ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    			cachedThreadPool.execute(() -> {
					try{
						operaterecordsService.insertOperaterecords(OperateRecordsTypeEnum.添加.getValue(), OperateRecordsFromEnum.系统后台.getValue(),
								user.getUserId(), user.getLoginName(), "addPaytype.jsp", "/platform/payMent/updatePaytype", "编辑支付类型类型");
					}
					catch(Exception e){
						LogHandle.error(LogType.OperateRecords,"编辑支付类型操作记录出错! 异常信息:",
								e, "/platform/payMent/updatePaytype");
					}
				});
			} else {
				item.setCode(-200);
				item.setDesc("编辑支付类型失败");
				LogHandle.info(LogType.Role, MessageFormat.format("编辑支付类型失败!名称:{0},操作人ID:{1}", channeldic,user.getUserId()),
						"/payMent/updatePaytype");
			}
		} catch (Exception e) {
			item.setCode(-900);
			 if (DebugConfig.BLUETOOTH_DEBUG) {
				 e.printStackTrace();
				 item.setDesc("编辑支付类型方式出现的异常：" + e.getMessage());
				} else {
					item.setDesc("系统错误！");
				}
			LogHandle.error(LogType.Role, "编辑支付类型出现的异常信息:" ,e,"/platform/payMent/updatePaytype");
		}
		return item; 
	}
	
	/**
	 * 编辑支付类型启用状态
	 * @param id
	 * @param status
	 * @return
	 */
	@RequestMapping("/updateStatus")
	@ResponseBody
	public ReusltItem updateStatus(String id, String status) {
		ReusltItem item = new ReusltItem();
		try {
			user=SessionState.GetCurrentUser();
			if (StringUtilsEX.ToInt(id) <= 0) {
				item.setCode(-101);
				item.setDesc("支付类型ID参数错误，id:" + id);
				return item;
			}
			if (StringUtilsEX.ToInt(status) < 0) {
				item.setCode(-102);
				item.setDesc("支付类型状态参数错误，status:" + status);
				return item;
			}
			if (payTypeService.updateStatus(StringUtilsEX.ToInt(status),
					StringUtilsEX.ToInt(id)) > 0) {
				item.setCode(0);
				item.setDesc("编辑支付类型状态成功");
				LogHandle.info(LogType.Role, MessageFormat.format("编辑支付类型状态成功! ID:{0},状态:{1},操作人ID:{2}",
						id,status,user.getUserId()),"/payMent/updateStatus");
				//异步操作 不影响正常流程
                ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    			cachedThreadPool.execute(new Runnable() {
    				@Override
    				public void run() {
    					try{
    						operaterecordsService.insertOperaterecords(
                            		OperateRecordsTypeEnum.修改.getValue(), OperateRecordsFromEnum.系统后台.getValue(),
                            		user.getUserId(), user.getLoginName(), "Control_paytypeList.jsp", "/platform/payMent/updateStatus", "修改支付类型状态");
    					}
    					catch(Exception e){
    						LogHandle.error(LogType.OperateRecords,"修改支付类型状态操作记录出错! 异常信息:",
    								e, "/platform/payMent/updateStatus");
    					}
    					
    				}
    			});
			} else {
				item.setCode(-200);
				item.setDesc("编辑支付类型状态失败");
				LogHandle.info(LogType.Role, MessageFormat.format("编辑支付类型状态失败! ID:{0},状态:{1},操作人ID:{2}",
						id,status,user.getUserId()),"/payMent/updateStatus");
			}

		} catch (Exception e) {
			item.setCode(-900);
			 if (DebugConfig.BLUETOOTH_DEBUG) {
				 item.setDesc("编辑支付类型状态出现的异常信息：" + e.getMessage());
				} else {
					item.setDesc("系统错误！");
				}
			LogHandle.error(LogType.Role, "编辑支付类型状态出现的异常信息:" ,e,"/platform/payMent/updateStatus");
		}
		return item;
	}
}
