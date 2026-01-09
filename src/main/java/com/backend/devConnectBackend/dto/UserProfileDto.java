package com.backend.devConnectBackend.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class UserProfileDto {
    private String name;
    private String email;
    private String role;
    private List<String> skills;
}
