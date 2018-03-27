package com.seger.shop.handler;

import com.seger.shop.exception.AuthorizeException;
import com.seger.shop.exception.SellException;
import com.seger.shop.utils.ResultVOUtil;
import com.seger.shop.vo.ResultVO;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * 异常处理器
 *
 * @author: seger.lin
 */

@ControllerAdvice
public class AuthorizeExceptionHandler {

    @ExceptionHandler(value = AuthorizeException.class)
    public ModelAndView handleAuthorizeException(){
        return new ModelAndView("redirect:/buyer/product/list");
    }

    @ExceptionHandler(value = SellException.class)
    @ResponseBody
    public ResultVO handlerSellerException(SellException e){
        return ResultVOUtil.error(e.getCode(), e.getMessage());
    }

}
