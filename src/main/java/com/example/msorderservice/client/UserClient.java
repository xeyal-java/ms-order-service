package com.example.msorderservice.client;

import com.example.msorderservice.client.dto.UserCommonDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "${user.service.url}")
public interface UserClient {
    @GetMapping("/api/v1/users/common/{id}")
    UserCommonDto getCommonUser(@PathVariable("id") Long id);
}
