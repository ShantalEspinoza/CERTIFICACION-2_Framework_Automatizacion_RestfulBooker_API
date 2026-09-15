package models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthCreds {
    private String username;
    private String password;
}
