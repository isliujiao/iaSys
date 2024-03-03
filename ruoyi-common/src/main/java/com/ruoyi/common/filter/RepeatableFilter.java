package com.ruoyi.common.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.MediaType;
import com.ruoyi.common.utils.StringUtils;

/**
 * Repeatable 过滤器
 * 这个类是一个过滤器，它实现了`Filter`接口。在`doFilter`方法中，
 * 它首先检查传入的请求是否为`HttpServletRequest`类型且内容类型为JSON。
 * 如果满足条件，它会创建一个`RepeatedlyRequestWrapper`对象来包装原始请求和响应。
 * 然后，根据`requestWrapper`是否为null，它将调用`chain.doFilter()`方法
 * 来处理原始请求或包装后的请求。
 */
public class RepeatableFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        ServletRequest requestWrapper = null;
        if (request instanceof HttpServletRequest
                && StringUtils.startsWithIgnoreCase(request.getContentType(), MediaType.APPLICATION_JSON_VALUE)) {
            requestWrapper = new RepeatedlyRequestWrapper((HttpServletRequest) request, response);
        }
        if (null == requestWrapper) {
            chain.doFilter(request, response);
        } else {
            chain.doFilter(requestWrapper, response);
        }
    }

    @Override
    public void destroy() {

    }
}
