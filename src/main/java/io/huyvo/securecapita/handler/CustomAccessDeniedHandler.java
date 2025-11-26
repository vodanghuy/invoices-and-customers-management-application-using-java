package io.huyvo.securecapita.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.huyvo.securecapita.model.HttpResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        HttpResponse httpResponse = HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .reason("You don't have enough permission")
                .status(HttpStatus.FORBIDDEN)
                .statusCode(HttpStatus.FORBIDDEN.value())
                .build();
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.FORBIDDEN.value());
        OutputStream out = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(out, httpResponse);
        out.flush();
    }
}

//public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
//    // Tạo đối tượng HttpResponse để trả về JSON
//    HttpResponse httpResponse = HttpResponse.builder()
//            .timeStamp(LocalDateTime.now().toString())
//            .reason("You dont have enough permission")
//            .status(HttpStatus.FORBIDDEN)
//            .statusCode(HttpStatus.FORBIDDEN.value())
//            .build();
//    // Cấu hình HTTP response
//    response.setContentType(APPLICATION_JSON_VALUE);
//    /* setContentType → set header Content-Type: application/json */
//    response.setStatus(HttpStatus.FORBIDDEN.value());
//    // Ghi JSON vào output stream
//    OutputStream out = response.getOutputStream();
//    // Tạo ObjectMapper để convert object thành JSON
//    ObjectMapper mapper = new ObjectMapper();
//    // Ghi JSON vào response body
//    mapper.writeValue(out, httpResponse);
//    // flush() để đẩy dữ liệu ra ngay lập tức
//    out.flush();
//}
