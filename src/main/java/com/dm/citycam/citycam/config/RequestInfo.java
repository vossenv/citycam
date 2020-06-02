package com.dm.citycam.citycam.config;


import com.dm.citycam.citycam.exception.InvalidParameterException;
import com.dm.citycam.citycam.search.SearchFilter;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import static com.dm.citycam.citycam.config.Meta.getClientIpAddress;
import static java.lang.Math.min;
import static java.net.URLDecoder.decode;

@Data
@NoArgsConstructor
public class RequestInfo {

    private Integer size = 100;
    private Integer page = 0;
    private String query = "";
    private String URL = "";
    private String clientIP = "";
    private Integer pageCount = 0;
    private Integer next = 0;
    private Integer prev = 0;
    private String nextURL = "";
    private String prevURL = "";
    private String firstURL = "";
    private String lastURL = "";
    private String lastCall = "";
    private Integer precision = 3;
    private Double searchTime = -1.0;
    private long rowCount = 0;
    private SearchFilter filter = SearchFilter.ENABLED_ONLY;
    private HttpHeaders headers = new HttpHeaders();
    private Pageable pageable = PageRequest.of(0, 100);
    private Long requestTime = System.nanoTime();

    public RequestInfo(HttpServletRequest request) throws InvalidParameterException {

        Map<String, String> requestMap = Collections.list(request.getHeaderNames()).stream()
                .collect(Collectors.toMap(Object::toString, request::getHeader));

        (Collections.list(request.getParameterNames()))
                .forEach(p -> requestMap.put(p, request.getParameter(p)));

        clientIP = getClientIpAddress(request);
        URL = request.getRequestURL().toString();
        filter = parseSearchFilter(requestMap.get("filter"));

        try {
            query = requestMap.containsKey("query") ? decode(requestMap.get("query"), "UTF-8") : this.query;
        } catch (UnsupportedEncodingException e) {
            throw new InvalidParameterException("Unsupported encoding: ", e);
        }

        String page = requestMap.containsKey("page") ? requestMap.get("page") : String.valueOf(this.page + 1);
        String size = requestMap.containsKey("size") ? requestMap.get("size") : String.valueOf(this.size);
        String precision = requestMap.containsKey("precision") ? requestMap.get("precision") : String.valueOf(this.precision);
        this.size = validateBoundedIntParameter("size", size, 1, 1000);
        this.page = validateBoundedIntParameter("page", page, 1, 5000) - 1;
        this.precision = validateBoundedIntParameter("precision", precision, 0, 5);
        this.pageable = PageRequest.of(this.page, this.size);
        headers.add("Client-IP", clientIP);

    }

    public void updateResults(long rowCount) {
        updateResults(rowCount, -1.0);
    }

    public void updateResults(long rowCount, double searchTime) {
        this.rowCount = rowCount;
        this.searchTime = searchTime;

        pageCount = (int) Math.ceil((double) rowCount / (double) size);
        next = min((page + 2), pageCount);
        prev = Integer.max(page, 1);

        if (pageCount > 1 && page + 1 > pageCount)
            throw new EntityNotFoundException(String.format("Requested page (%d) does not exist", page));

        lastCall = ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString();
        String pageUrl = lastCall.replaceAll("/$", "");
        if (!pageUrl.contains("?")) {
            pageUrl += "?page=%s";
        } else if (pageUrl.contains("page")) {
            pageUrl = pageUrl.replaceAll("(page=)\\d*", "page=%s");
        } else {
            pageUrl += "&page=%s";
        }

        nextURL = String.format(pageUrl, next);
        prevURL = String.format(pageUrl, prev);
        firstURL = String.format(pageUrl, 1);
        lastURL = String.format(pageUrl, pageCount);

        headers.add("Page-Size", size.toString());
        headers.add("Current-Page", lastCall);
        headers.add("First-Page", firstURL);
        headers.add("Previous-Page", prevURL);
        headers.add("Next-Page", nextURL);
        headers.add("Result-Count", String.valueOf(rowCount));
        headers.add("Page-Count", String.valueOf(pageCount));
        headers.add("Total-Time-Seconds", String.valueOf((System.nanoTime() - requestTime) * 1.0e-9));

        if (searchTime != -1.0) {
            // this means we did a SEARCH and not a find all
            headers.add("Search-Time-Seconds", String.valueOf(searchTime));
            headers.add("Search-Precision", String.valueOf(precision));
            headers.add("Original-Query", query);
        }
    }

    private SearchFilter parseSearchFilter(String filter) throws InvalidParameterException {

        if (null == filter || filter.isEmpty() || filter.equals("enabled")) {
            return SearchFilter.ENABLED_ONLY;
        } else if (filter.equals("disabled")) {
            return SearchFilter.DISABLED_ONLY;
        } else if (filter.equals("all")) {
            return SearchFilter.INCLUDE_DISABLED;
        }

        throw new InvalidParameterException(String.format("Cannot parse filter '%s'.  " +
                "Must be one of: (disabled, enabled, all)", filter));
    }

    private int validateBoundedIntParameter(String type, String param, int min, int max) throws InvalidParameterException {
        try {
            int p = Integer.parseInt(param);
            if (min <= p && p <= max) return p;
        } catch (NumberFormatException e) {
            // No action
        }

        throw new InvalidParameterException(String.format("Valid range exceeded for '%s'.  " +
                "Expected range: from %d to %d, Got: %s", type, min, max, param));
    }

}
