package com.ruoyi.user.model.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserLoginBO {

    private Long userId;

    private Boolean newUser;

}
