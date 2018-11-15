package com.stylefeng.guns.rest.modular.user;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.api.user.UserAPI;
import com.stylefeng.guns.api.user.UserInfoModel;
import com.stylefeng.guns.api.user.UserModel;
import com.stylefeng.guns.rest.common.CurrentUser;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/user/")
@RestController
public class UserController {
    @Reference(interfaceClass = UserAPI.class)
    private UserAPI userAPI;

    @RequestMapping(name = "register", method = RequestMethod.POST)
    public ResponseVO register(UserModel userModel) {
        if (userModel.getUsername() == null || userModel.getUsername().trim().length() == 0) {
            return ResponseVO.serviceFail("用户名不能为空");
        }
        if (userModel.getPassword() == null || userModel.getPassword().trim().length() == 0) {
            return ResponseVO.serviceFail("密码不能为空");
        }
        // 调用用户模块
        boolean isSuccess = userAPI.register(userModel);
        if (isSuccess) {
            return ResponseVO.success("注册成功");
        } else {
            return ResponseVO.serviceFail("注册失败");
        }
    }

    @RequestMapping(name = "check", method = RequestMethod.POST)
    public ResponseVO check(String username) {
        if (username != null && username.trim().length() > 0) {
            // 返回
            boolean exist = userAPI.checkUsername(username);
            if (exist) {
                return ResponseVO.serviceFail("用户名已经存在");
            }
            return ResponseVO.success("用户名不存在");
        }

        return ResponseVO.serviceFail("用户名不能为空");
    }

    @RequestMapping(name = "logout", method = RequestMethod.GET)
    public ResponseVO logout() {
         /*
            应用：
                1、前端存储JWT【七天】： JWT 的刷新
                2、服务器端会存储活动用户信息【30分钟】
                3、JWT 里的 userId 为 key，查找活跃用户
            退出：
                1、前端删除掉 JWT
                2、后端服务器删除活跃用户缓存
            现状：
                1、前端删除掉 JWT (没有集成 redis 无法判断用户是否活跃
         */
        return ResponseVO.success("用户退出成功");
    }

    @RequestMapping(name = "getUserInfo", method = RequestMethod.GET)
    public ResponseVO getUserInfo() {
        // 获取当前登录用户
        String userId = CurrentUser.getCurrentUserId();
        if (userId != null && userId.trim().length() > 0) {
            // 将用户 id 传入后端进行查询
            int uuid = Integer.parseInt(userId);
            UserInfoModel userInfo = userAPI.getUserInfo(uuid);
            if (userInfo != null) {
                return ResponseVO.success(userInfo);
            }
            return ResponseVO.serviceFail("用户信息查询失败");
        }
        return ResponseVO.serviceFail("用户未登录");
    }

    @RequestMapping(name = "updateUserInfo", method = RequestMethod.POST)
    public ResponseVO updateUserInfo(UserInfoModel userInfoModel) {
        // 获取当前登录用户
        String userId = CurrentUser.getCurrentUserId();
        if (userId != null && userId.trim().length() > 0) {
            // 将用户 id 传入后端进行查询
            int uuid = Integer.parseInt(userId);
            // 校验传入的用户 id 与 当前用户 id 是否相等，防止恶意修改别的用户信息
            if (uuid != userInfoModel.getUuid()) {
                return ResponseVO.serviceFail("请修改您个人的信息");
            }
            UserInfoModel userInfo = userAPI.updateUserInfo(userInfoModel);
            if (userInfo != null) {
                return ResponseVO.success(userInfo);
            }
            return ResponseVO.serviceFail("用户信息修改失败");
        }
        return ResponseVO.serviceFail("用户未登录");
    }

}
