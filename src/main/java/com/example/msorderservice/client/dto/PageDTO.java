package com.example.msorderservice.client.dto;

import lombok.Data;
import java.util.List;

@Data
public class PageDTO<T> {
    private List<T> content;
}