package com.eth.ens.action;

import com.eth.ens.model.EnsDomainsDTO;
import com.eth.ens.model.EnsDomainsQO;
import com.eth.ens.service.IEthEnsInfoService;
import com.eth.framework.base.model.PageData;
import com.eth.framework.base.model.PageParam;
import com.eth.framework.base.model.WebApiBaseResult;
import com.eth.framework.base.utils.PageUtils;
import com.eth.framework.base.utils.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;

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
    public WebApiBaseResult listEnsDomain(HttpSession httpSession, HttpServletRequest request
            ,@RequestParam(value = "page", required = false) Integer page
            ,@RequestParam(value = "rows", required = false) Integer size
            ,@RequestParam(value = "sidx", required = false) String sidx
            ,@RequestParam(value = "sord", required = false) String sord
            ,@RequestParam(value = "domain", required = false) String domain
            ,@RequestParam(value = "expiration_date_start", required = false)@DateTimeFormat(pattern="yyyy-MM-dd") Date expiration_date_start
            ,@RequestParam(value = "expiration_date_end", required = false)@DateTimeFormat(pattern="yyyy-MM-dd") Date expiration_date_end
            ,@RequestParam(value = "available", required = false) String available
            ,@RequestParam(value = "length_min", required = false) Integer length_min
            ,@RequestParam(value = "length_max", required = false) Integer length_max
            ,@RequestParam(value = "starts_with", required = false) String starts_with
            ,@RequestParam(value = "ends_with", required = false) String ends_with
            ,@RequestParam(value = "letters_only", required = false) String letters_only
            ,@RequestParam(value = "has_numbers", required = false) String has_numbers
            ,@RequestParam(value = "has_unicode", required = false) String has_unicode
            ,@RequestParam(value = "has_emoji", required = false) String has_emoji
            ,@RequestParam(value = "has_invisibles", required = false) String has_invisibles){
        WebApiBaseResult result = new WebApiBaseResult();
        EnsDomainsQO qo = new EnsDomainsQO();
        qo.setDomain(domain);
        Date now = new Date();
        if(!StringUtils.isEmpty(available)){
            if("0".equals(available)){//域名不可用,查询已过期的域名
                if(expiration_date_start == null || expiration_date_start.getTime() < now.getTime()){
                    expiration_date_start = now;
                }
            }else if("1".equals(available)){//域名可用，查询未过期的域名
                if(expiration_date_end == null || expiration_date_start.getTime() > now.getTime()){
                    expiration_date_end = now;
                }
            }
        }
        qo.setExpiration_date_start(expiration_date_start);
        qo.setExpiration_date_end(expiration_date_end);
        qo.setLength_min(length_min);
        qo.setLength_max(length_max);
        qo.setStarts_with(starts_with);
        qo.setEnds_with(ends_with);
        qo.setLetters_only(letters_only);
        qo.setHas_numbers(has_numbers);
        qo.setHas_unicode(has_unicode);
        qo.setHas_emoji(has_emoji);
        qo.setHas_invisibles(has_invisibles);
        PageParam pageParam = PageUtils.constructPageParam(page, size, 0, sidx, sord);
        PageData<EnsDomainsDTO> pageData = ethEnsInfoService.listEnsDomain(qo, pageParam);
        result.setData(pageData);
        return result;
    }
}
