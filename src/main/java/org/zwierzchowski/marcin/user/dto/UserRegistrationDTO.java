package org.zwierzchowski.marcin.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserRegistrationDTO {

      private String username;
      private String password;
      private String role;
}
