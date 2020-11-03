package com.rower.chat;

/**
 * PROTOCOL_LEN:协议字符串的长度
 * MSG_ROUND:表示发送信息，不是登录或者私发
 * USER_ROUND:用户名标识符，信息首尾是这个表明用户登录
 * LOGIN_SUCCESS:登录成功标识
 * NAME_REP:用户名重复标识
 * PRIVATE_ROUND:私发标识
 * SPLIT_SIGN：用于分割
 *
 */
public interface SocketProtocol {
    int PROTOCOL_LEN = 2;
    String MSG_ROUND = "△△";
    String PRIVATE_ROUND = "▲▲";
    String SPLIT_SIGN = "▼▼";
}
