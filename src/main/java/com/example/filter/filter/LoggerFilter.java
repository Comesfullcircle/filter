package com.example.filter.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.stream.Collectors;

@Slf4j
@Component
public class LoggerFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 진입 전
        log.info("진입 전");

        var req = new ContentCachingRequestWrapper((HttpServletRequest) request);
        var res = new ContentCachingResponseWrapper((HttpServletResponse) response);


        // 다음 필터 또는 서블릿으로 요청 전달
        chain.doFilter(req,res);

        var reqJson = new String(req.getContentAsByteArray());
        log.info("req : {}", reqJson);

        var resJson = new String(res.getContentAsByteArray());
        log.info("res : {}", resJson);

        // 리턴
        log.info("<<<< 리턴");

        res.copyBodyToResponse();
    }
}

