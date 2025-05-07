package dev.triomph.kies.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleError(HttpServletRequest request) {
        Object status = request.getAttribute("jakarta.servlet.error.status_code");
        Object message = request.getAttribute("jakarta.servlet.error.message");
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("status", status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorDetails.put("message", message != null ? message : "Unknown error");
        errorDetails.put("timestamp", System.currentTimeMillis());
        errorDetails.put("path", request.getRequestURI());
        
        return ResponseEntity.status(status != null ? Integer.parseInt(status.toString()) : 
            HttpStatus.INTERNAL_SERVER_ERROR.value()).body(errorDetails);
    }
} 