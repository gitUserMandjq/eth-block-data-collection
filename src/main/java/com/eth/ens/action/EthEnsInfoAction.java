package com.eth.ens.action;

import com.eth.ens.model.EnsDomainsDTO;
import com.eth.ens.model.EnsDomainsQO;
import com.eth.ens.service.IEthEnsInfoService;
import com.eth.framework.base.model.PageData;
import com.eth.framework.base.model.PageParam;
import com.eth.framework.base.model.WebApiBaseResult;
import com.eth.framework.base.utils.PageUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/eth/ens")
public class EthEnsInfoAction {
    @Resource
    IEthEnsInfoService ethEnsInfoService;
    /**
     * 查询eth信息
     * @param httpSession
     * @param request
     * @param page
     * @param size
     * @param sidx
     * @param sord
     * @return
     */
    @RequestMapping(value = "/listEnsDomains", method = RequestMethod.GET)
    public WebApiBaseResult listEnsDomain(HttpSession httpSession, HttpServletRequest request,
                                          @RequestParam(value = "page", required = false) Integer page,
                                          @RequestParam(value = "size", required = false) Integer size,
                                          @RequestParam(value = "sidx", required = false) String sidx,
                                          @RequestParam(value = "sord", required = false) String sord){
        WebApiBaseResult result = new WebApiBaseResult();
        EnsDomainsQO qo = new EnsDomainsQO();
        PageParam pageParam = PageUtils.constructPageParam(page, size, 1, sidx, sord);
        PageData<EnsDomainsDTO> pageData = ethEnsInfoService.listEnsDomain(qo, pageParam);
        result.setData(pageData);
        return result;
    }
}
