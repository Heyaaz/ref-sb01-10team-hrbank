package com.sprint.example.sb01part2hrbankteam10ref.util;

import jakarta.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.util.StringUtils;

public class IpUtil {
  // 프록시, 로드밸런서 사용 시에도 clientIp 가져오기
  public static String getClientIp(HttpServletRequest request) {
    String clientIp = null;
    boolean isIpInHeader = false;

    List<String> headerList = new ArrayList<>();
    headerList.add("X-Forwarded-For");
    headerList.add("HTTP_CLIENT_IP");
    headerList.add("HTTP_X_FORWARDED_FOR");
    headerList.add("HTTP_X_FORWARDED");
    headerList.add("HTTP_FORWARDED_FOR");
    headerList.add("HTTP_FORWARDED");
    headerList.add("Proxy-Client-IP");
    headerList.add("WL-Proxy-Client-IP");
    headerList.add("HTTP_VIA");
    headerList.add("IPV6_ADR");

    for (String header : headerList) {
      clientIp = request.getHeader(header);
      if (StringUtils.hasText(clientIp) && !"unknown".equalsIgnoreCase(clientIp)) {
        isIpInHeader = true;
        break;
      }
    }

    if (!isIpInHeader) {
      clientIp = request.getRemoteAddr();
    }

    if ("0:0:0:0:0:0:0:1".equals(clientIp) || "127.0.0.1".equals(clientIp)) {
      InetAddress address = null;
      try {
        address = InetAddress.getLocalHost();
      } catch (UnknownHostException e) {
        throw new RuntimeException(e);
      }
      clientIp = address.getHostAddress();
    }

    return clientIp;
  }
}
