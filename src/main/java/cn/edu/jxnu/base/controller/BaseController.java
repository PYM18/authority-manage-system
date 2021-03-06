/* 梦境迷离 (C)2020 */
package cn.edu.jxnu.base.controller;

import cn.edu.jxnu.base.utils.DateEditor;
import java.io.IOException;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

/**
 * 主控制类
 *
 * @author 梦境迷离
 * @version V2.0 2020年11月20日
 */
@Component
public class BaseController {

    private final Logger log = LoggerFactory.getLogger(getClass());
    //    @Autowired
    //    protected HttpServletRequest request;
    //
    //    @Autowired
    //    protected HttpServletResponse response;

    // 由@InitBinder表示的方法，可以对WebDataBinder对象进行初始化。WebDataBinder是DataBinder的子类，用于完成由表单到JavaBean属性的绑定。
    // @InitBinder方法不能有返回值，它必须名为void。
    // @InitBinder方法的参数通常是WebDataBinder，@InitBinder可以对WebDataBinder进行初始化。

    /**
     * 由InitBinder表示的方法，可以对WebDataBinder对象进行初始化。WebDataBinder是DataBinder的子类， 用于完成由表单到JavaBean属性的绑定。
     * InitBinder方法不能有返回值，它必须名为void。
     * InitBinder方法的参数通常是WebDataBinder，@InitBinder可以对WebDataBinder进行初始化。
     *
     * @param webDataBinder webDataBinder
     */
    @InitBinder
    protected void initBinder(WebDataBinder webDataBinder) {
        /**
         * 一个用于trim 的 String类型的属性编辑器 如默认删除两边的空格，charsToDelete属性：可以设置为其他字符
         * emptyAsNull属性：将一个空字符串转化为null值的选项。
         */
        webDataBinder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
        webDataBinder.registerCustomEditor(Date.class, new DateEditor(true));
    }

    /**
     * 带参重定向
     *
     * @param path path
     * @return String
     */
    protected String redirect(String path) {
        return "redirect:" + path;
    }

    /**
     * 不带参重定向
     *
     * @param response response
     * @param path path
     * @return String
     */
    protected String redirect(HttpServletResponse response, String path) {
        try {
            response.sendRedirect(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取分页请求 并排序
     *
     * @param request request
     * @return PageRequest
     */
    protected PageRequest getPageRequest(HttpServletRequest request) {
        int page = 1;
        int size = 10;
        Sort sort = null;
        try {
            String sortName = request.getParameter("sortName");
            String sortOrder = request.getParameter("sortOrder");
            if (StringUtils.isNoneBlank(sortName) && StringUtils.isNoneBlank(sortOrder)) {
                if (sortOrder.equalsIgnoreCase("desc")) {
                    sort = new Sort(Direction.DESC, sortName);
                } else {
                    sort = new Sort(Direction.ASC, sortName);
                }
            }

            page = Integer.parseInt(request.getParameter("pageNumber")) - 1;
            size = Integer.parseInt(request.getParameter("pageSize"));
            log.info("页数: " + page);
            log.info("容量: " + size);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sort == null ? PageRequest.of(page, size) : PageRequest.of(page, size, sort);
    }

    /**
     * 获取分页请求 带排序
     *
     * @param request request
     * @param sort sort
     * @return PageRequest
     */
    protected PageRequest getPageRequest(HttpServletRequest request, Sort sort) {
        int page = 0;
        int size = 10;
        try {
            String sortName = request.getParameter("sortName");
            String sortOrder = request.getParameter("sortOrder");
            if (StringUtils.isNoneBlank(sortName) && StringUtils.isNoneBlank(sortOrder)) {
                if (sortOrder.equalsIgnoreCase("desc")) {
                    sort.and(new Sort(Direction.DESC, sortName));
                } else {
                    sort.and(new Sort(Direction.ASC, sortName));
                }
            }
            page = Integer.parseInt(request.getParameter("pageNumber")) - 1;
            size = Integer.parseInt(request.getParameter("pageSize"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sort == null ? PageRequest.of(page, size) : PageRequest.of(page, size, sort);
    }
}
