package com.stu.helloserver.service;

import com.stu.helloserver.common.Result;
import com.stu.helloserver.dto.UserDTO;
import com.stu.helloserver.vo.UserDetailVO;
import com.stu.helloserver.vo.UserInfoVO;

public interface UserService {
    Result<String> register(UserDTO userDTO);

    Result<String> login(UserDTO userDTO);

    Result<String> getUserById(Long id);

    Result<Object> getUserPage(Integer pageNum, Integer pageSize);

    Result<UserDetailVO> getUserDetail(Long userId);

    Result<String> updateUserInfo(Long userId, UserInfoVO userInfoVO);

    Result<String> deleteUser(Long userId);
}
