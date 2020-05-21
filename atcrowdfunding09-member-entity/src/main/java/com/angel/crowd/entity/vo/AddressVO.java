package com.angel.crowd.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author 刘振河
 * @create 2020--05--15 16:44
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressVO implements Serializable {
    private Integer id;

    private String receiveName;

    private String phoneNum;

    private String address;

    private Integer memberId;
}
