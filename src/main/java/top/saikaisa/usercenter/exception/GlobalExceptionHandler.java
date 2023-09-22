package top.saikaisa.usercenter.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import top.saikaisa.usercenter.common.BaseResponse;
import top.saikaisa.usercenter.common.ErrorCode;
import top.saikaisa.usercenter.common.ResultUtils;

/**
 * 全局异常处理器
 *
 * @Author Saikai
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // @ExceptionHandler(BusinessException.class) 说明这个方法只用来处理 BusinessException 类型的异常
    @ExceptionHandler(BusinessException.class)
    public BaseResponse bussinessExceptionHandler(BusinessException e){
        log.error("businessException: " + e.getMessage(), e);
        return ResultUtils.error(e.getCode(), e.getMessage(), e.getDescription());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse runtimeExceptionHandler(RuntimeException e){
        log.error("runtimeException", e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, e.getMessage(), "");
    }
}
