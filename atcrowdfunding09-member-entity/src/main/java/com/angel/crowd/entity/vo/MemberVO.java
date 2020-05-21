package com.angel.crowd.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 刘振河
 * @create 2020--05--04 15:03
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberVO {
    private String loginacct;
    private String userpswd;
    private String username;
    private String  email;
    private String phone;
    private String code;
}
