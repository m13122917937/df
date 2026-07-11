package com.ruoyi.common.exception.user;

/**
 * 黑名单IP异常类
 * 
 * @author ruoyi
 */
public class BlackListException extends UserException
{
    private static final long serialVersionUID = 1L;

    public BlackListException()
    {
        super("login.blocked", null, "登录已被禁止，请联系管理员");
    }
}
