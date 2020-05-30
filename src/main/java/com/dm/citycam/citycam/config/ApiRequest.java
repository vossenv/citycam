package com.dm.citycam.citycam.config;


import com.dm.citycam.citycam.exception.InvalidParameterException;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.net.URLDecoder.decode;

@Data
@NoArgsConstructor
public class ApiRequest {

    private Integer size = 100;
    private Integer page = 0;
    private String query="";
    private String URL="";
    private String client_ip="";
    private Boolean incDisabled = false;
    private Pageable pageable = PageRequest.of(0, 100);
    private Long requestTime = System.nanoTime();
    private List<String> errors = new ArrayList<>();

    public ApiRequest(String query){
        this.query = query;
    }

    public ApiRequest(HttpServletRequest request) throws InvalidParameterException, UnsupportedEncodingException{

        Map<String, String> requestMap = Collections.list(request.getHeaderNames()).stream()
               .collect(Collectors.toMap(Object::toString, request::getHeader));

        (Collections.list(request.getParameterNames()))
                .forEach(p -> requestMap.put(p, request.getParameter(p)));

        this.client_ip = getClientIpAddress(request);
        this.URL = request.getRequestURL().toString();
        this.incDisabled = requestMap.containsKey("disabled");
        this.query = requestMap.containsKey("query") ? decode(requestMap.get("query"), "UTF-8") : this.query;
        String page = requestMap.containsKey("page") ? requestMap.get("page") : String.valueOf(this.page + 1);
        String size = requestMap.containsKey("size") ? requestMap.get("size") : String.valueOf(this.size);
        this.size = validateParameter("size", size, 1, 1000);
        this.page = validateParameter("page", page, 1, Integer.MAX_VALUE) - 1;

        if (errors.size() > 0) {
            throw new InvalidParameterException(errors);
        }

        this.pageable = PageRequest.of(this.page, this.size);
    }

    private int validateParameter (String type, String param, int min, int max){
        try {
            int p = Integer.parseInt(param);
            if (p < min || p > max){
                errors.add("Valid range exceeded for " + type + ".  Expected range: " + min + " and " + max + ", Got: " + p);
                return 0;
            } else return p;
        }
        catch (NumberFormatException e){
           errors.add("Error parsing " + type + ": '" + param + "'.  Please enter a valid integer between " + min + " and " + max);
           return 0;
        }
    }

    private static final String[] IP_HEADER_CANDIDATES = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR" };

    public static String getClientIpAddress(HttpServletRequest request) {
        for (String header : IP_HEADER_CANDIDATES) {
            String ip = request.getHeader(header);
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                return ip;
            }
        }
        return request.getRemoteAddr();
    }
}
