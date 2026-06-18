package com.example.msorderservice.client.dto;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCommonDto {
    private Long id;
    private String fullName;
}
