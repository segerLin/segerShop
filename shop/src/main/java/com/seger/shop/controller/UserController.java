package com.seger.shop.controller;

import com.seger.shop.constant.CookieConstant;
import com.seger.shop.constant.RedisConstant;
import com.seger.shop.dataobject.OrderDetail;
import com.seger.shop.dataobject.UserInfo;
import com.seger.shop.dto.OrderDTO;
import com.seger.shop.enums.OrderStatusEnum;
import com.seger.shop.enums.ResultEnum;
import com.seger.shop.enums.UserTypeEnum;
import com.seger.shop.form.LoginForm;
import com.seger.shop.service.OrderService;
import com.seger.shop.service.UserService;
import com.seger.shop.utils.CookieUtil;
import com.seger.shop.utils.ResultVOUtil;
import com.seger.shop.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 用户
 *
 * @author: seger.lin
 */

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private StringRedisTemplate redisTemplate;


    @PostMapping("/login")
    @CrossOrigin
    public ResultVO login(@RequestBody LoginForm loginForm,
                              HttpServletResponse response,
                              Map<String, Object> map) {
        String username = loginForm.getUserName();
        String password = loginForm.getUserPwd();

        //1. openid去和数据库里的数据匹配
        UserInfo userInfo = userService.findUserInfoByUsernameAndPassword(username, password);
        if (userInfo == null) {
            return ResultVOUtil.error(ResultEnum.LOGIN_FAIL.getCode(),"账号／密码竟然不正确");
        }

        String userid = userInfo.getUserId();
        //2. 设置token至redis
        String token = userid;
        Integer expire = RedisConstant.EXPIRE;

        redisTemplate.opsForValue().set(String.format(RedisConstant.TOKEN_PREFIX, token), userid, expire, TimeUnit.SECONDS);

        //3. 设置token至cookie
        CookieUtil.set(response, CookieConstant.TOKEN, token, expire);
//        if(userInfo.getUserType().equals(UserTypeEnum.SELLER.getCode()))
//            return ResultVOUtil.success();
        return ResultVOUtil.success(userid);

    }

    @PostMapping("/register")
    @CrossOrigin
    public ResultVO register(@RequestBody LoginForm loginForm){
        if(userService.findUserInfoByUsername(loginForm.getUserName()) != null){
            return ResultVOUtil.error(ResultEnum.REGISTER_ERROR.getCode(), "账户存在");
        }
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername(loginForm.getUserName());
        userInfo.setPassword(loginForm.getUserPwd());
        UserInfo result = userService.save(userInfo);
        /*生成购物车*/
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setBuyerOpenid(result.getUserId());
        orderDTO.setBuyerName("");
        orderDTO.setBuyerAddress("");
        orderDTO.setBuyerPhone("");
        orderDTO.setOrderDetailList(new ArrayList<OrderDetail>());
        orderService.create(orderDTO,OrderStatusEnum.CART.getCode());
        return ResultVOUtil.success();
    }

    @GetMapping("/logout")
    @CrossOrigin
    public ResultVO logout(HttpServletRequest request,
                               HttpServletResponse response,
                               Map<String, Object> map) {
        //1. 从cookie里查询
        Cookie cookie = CookieUtil.get(request, CookieConstant.TOKEN);
        if (cookie != null) {
            //2. 清除redis
            redisTemplate.opsForValue().getOperations().delete(String.format(RedisConstant.TOKEN_PREFIX, cookie.getValue()));

            //3. 清除cookie
            CookieUtil.set(response, CookieConstant.TOKEN, null, 0);
        }

        map.put("msg", ResultEnum.LOGOUT_SUCCESS.getMessage());
        map.put("url", "/seger/shop/seller/order/list");
        return ResultVOUtil.success();
    }

    @GetMapping("/userInfo")
    @CrossOrigin
    public ResultVO userInfo(HttpServletRequest request){
        Cookie cookie = CookieUtil.get(request, CookieConstant.TOKEN);
        if(cookie != null)
            return ResultVOUtil.success();
        else
            return ResultVOUtil.error(ResultEnum.NOT_LOGIN.getCode(), "未登录");

    }

    @GetMapping("/admin")
    public ModelAndView admin(){
        return new ModelAndView("/login/login");
    }

    @PostMapping("/adminLogin")
    public ModelAndView adminLogin(@RequestParam("username") String username,
                          @RequestParam("password") String password,
                          HttpServletResponse response,
                          Map<String, Object> map) {
        //1. openid去和数据库里的数据匹配
        UserInfo userInfo = userService.findUserInfoByUsernameAndPassword(username, password);
        if (userInfo == null || !userInfo.getUserType().equals(UserTypeEnum.SELLER.getCode())) {
            map.put("msg", ResultEnum.LOGIN_FAIL.getMessage());
            map.put("url", "/seger/shop/seller/order/list");
            return new ModelAndView("common/error");
        }

        //2. 设置token至redis
        String token = UUID.randomUUID().toString();
        Integer expire = RedisConstant.EXPIRE;

        redisTemplate.opsForValue().set(String.format(RedisConstant.TOKEN_PREFIX, token), userInfo.getUserId(), expire, TimeUnit.SECONDS);

        //3. 设置token至cookie
        CookieUtil.set(response, CookieConstant.TOKEN, token, expire);

        return new ModelAndView("redirect:/seller/order/list");
    }

}