package com.example.msorderservice.client;
import com.example.msorderservice.client.dto.CourierDTO;
import com.example.msorderservice.client.dto.PageDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "ms-courier-service", url = "${application.config.courier-service-url}")
public interface CourierClient {

    @GetMapping("/api/v1/couriers/available")
    PageDTO<CourierDTO> getAvailableCouriers(@RequestParam int page, @RequestParam int size);
}