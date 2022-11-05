package com.eth.framework.base.sysUser.action;

import com.eth.framework.base.model.WebApiBaseResult;
import com.eth.framework.base.sysUser.model.SysUserDTO;
import com.eth.framework.base.sysUser.model.SysUserModel;
import com.eth.framework.base.sysUser.service.ISysUserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/user")
public class SysUserAction {
    @Resource
    ISysUserService sysUserService;

    /**
     * 用户登录
     * @param httpSession
     * @param request
     * @param address
     * @return
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public WebApiBaseResult login(HttpSession httpSession, HttpServletRequest request
            ,@RequestParam(value = "address", required = false) String address) throws Exception {
        WebApiBaseResult result = new WebApiBaseResult();
        SysUserModel sysUser = sysUserService.logIn(address);
        httpSession.setAttribute(SysUserModel.LOGIN_USER, sysUser);
        result.setData(new SysUserDTO(sysUser.getUser_id(), sysUser.getUser_name()));
        return result;
    }
    /**
     * 用户登出
     * @param httpSession
     * @param request
     * @return
     */
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public WebApiBaseResult logout(HttpSession httpSession, HttpServletRequest request) throws Exception {
        WebApiBaseResult result = new WebApiBaseResult();
        httpSession.removeAttribute(SysUserModel.LOGIN_USER);
        return result;
    }
    /**
     * 获取用户登录信息
     * @param httpSession
     * @param request
     * @return
     */
    @RequestMapping(value = "/getUserSession", method = RequestMethod.GET)
    public WebApiBaseResult getUserSession(HttpSession httpSession, HttpServletRequest request) throws Exception {
        WebApiBaseResult result = new WebApiBaseResult();
        SysUserModel sysUser = (SysUserModel) httpSession.getAttribute(SysUserModel.LOGIN_USER);
        if(sysUser == null){
            throw new Exception("用户未登录");
        }
        result.setData(new SysUserDTO(sysUser.getUser_id(), sysUser.getUser_name()));
        return result;
    }
    /**
     * 用户登录
     * @param httpSession
     * @param request
     * @return
     */
    @RequestMapping(value = "/modifyUserName", method = RequestMethod.POST)
    public WebApiBaseResult modifyUserName(HttpSession httpSession, HttpServletRequest request
            ,@RequestParam(value = "userName", required = false) String userName) throws Exception {
        WebApiBaseResult result = new WebApiBaseResult();
        SysUserModel sysUser = (SysUserModel) httpSession.getAttribute(SysUserModel.LOGIN_USER);
        sysUserService.modifyUserName(sysUser.getUser_id(), userName);
        httpSession.setAttribute(SysUserModel.LOGIN_USER, sysUser);
        result.setData(new SysUserDTO(sysUser.getUser_id(), sysUser.getUser_name()));
        return result;
    }
}
