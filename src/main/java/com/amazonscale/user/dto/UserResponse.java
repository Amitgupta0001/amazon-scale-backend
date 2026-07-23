// this is what the client receives

package com.amazonscale.user.dto;
import com.amazonscale.user.entity.Role;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
    private boolean enabled;
    private LocalDateTime createdAt;
}
