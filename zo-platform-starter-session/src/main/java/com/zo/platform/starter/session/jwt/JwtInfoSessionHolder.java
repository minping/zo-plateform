package com.zo.platform.starter.session.jwt;

import com.zo.platform.sdk.common.kit.StrKit;
import com.zo.platform.starter.redis.RedisUtil;
import com.zo.platform.starter.session.ISessionHolder;
import com.zo.platform.starter.session.config.SessionConfig;
import com.zo.platform.starter.session.constant.SessionConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author zhoump
 * @date 2026/11/9
 * @purpose
 */
@Slf4j
public class JwtInfoSessionHolder implements ISessionHolder<JwtInfoSessionHolder> {
    /**
     * Jwt信息
     */
    ThreadLocal<JwtResult> jwtResultThreadLocal = new ThreadLocal<>();

    RedisUtil redisUtil;

    SessionConfig sessionConfig;

    @Autowired
    public void setRedisUtil(RedisUtil redisUtil, @Qualifier(SessionConstant.SESSION_REDIS_TEMPLATE_BEAN_NAME) ObjectProvider<RedisUtil> redisUtilObjectProvider) {
        RedisUtil sessionredisUtil = redisUtilObjectProvider.getIfAvailable();
        if (sessionredisUtil != null) {
            this.redisUtil = sessionredisUtil;
        } else {
            this.redisUtil = redisUtil;
        }
    }

    @Autowired
    public void setSessionConfig(SessionConfig sessionConfig) {
        this.sessionConfig = sessionConfig;
    }

    @Override
    public Object getAttribute(String key) {
        return getAttribute(key, null);
    }

    @Override
    public Object getAttribute(String key, Object defaultValue) {
        if (getJwt() == null) {
            //            log.warn("jwt token must be not null,please checking");
            return null;
        }
        Object value = redisUtil.hGet(contentKey(), key);
        return value == null ? defaultValue : value;
    }

    @Override
    public JwtInfoSessionHolder setAttribute(String key, Object value) {
        if (getJwt() == null) {
            log.error("jwt token must be not null,please checking");
            throw JwtException.create(JwtCode.NO_TOKEN);
        }
        redisUtil.hSet(contentKey(), key, value);
        return this;
    }

    @Override
    public JwtInfoSessionHolder removeAttribute(String key) {
        redisUtil.hDelete(contentKey(), key);
        return this;
    }

    @Override
    public JwtInfoSessionHolder clear() {
        if (getJwt() == null) {
            //            log.warn("jwt token must be not null,please checking");
            return this;
        }
        clearRule();
        clearContent();
        clearToken();
        return this;
    }

    @Override
    public JwtInfoSessionHolder clear(String userId) {
        String ruleContentKeys = (String) redisUtil.hGet(ruleKey(userId), SessionConstant.RULE_CONTENT_KEY);
        String ruleTokenKeys = (String) redisUtil.hGet(ruleKey(userId), SessionConstant.RULE_TOKEN_KEY);
        if(StrKit.isNotBlank(ruleContentKeys)){
            String[] ruleContentKeyArray = ruleContentKeys.split(",");
            for (int i = 0; i < ruleContentKeyArray.length; i++) {
                redisUtil.delete(ruleContentKeyArray[i]);
            }
        }
        if(StrKit.isNotBlank(ruleTokenKeys)){
            String[] ruleTokenKeysArray = ruleTokenKeys.split(",");
            for (int i = 0; i < ruleTokenKeysArray.length; i++) {
                redisUtil.delete(ruleTokenKeysArray[i]);
            }
        }
        redisUtil.delete(ruleKey(userId));
        return this;
    }

    @Override
    public JwtInfoSessionHolder release() {
        this.jwtResultThreadLocal.remove();
        return this;
    }

    /**
     * 清理策略中冗余的session 用户信息
     */
    private void clearRule(){
        String ruleContentKeys = (String) redisUtil.hGet(ruleKey(), SessionConstant.RULE_CONTENT_KEY);
        String contentKey = contentKey();
        String clearContentKeys = delStr(ruleContentKeys, contentKey);
        if(clearContentKeys!=null){
            if(StrKit.equals(getJwt().getToken(), getRedisUserToken())) {
                //1、允许多端重复登录
                // 或者
                //2、当cookie token 与 redis中 rule的token一致时 允许删除，防止多端登录误删 用户session
                redisUtil.hSet(ruleKey(),SessionConstant.RULE_CONTENT_KEY, clearContentKeys);
            }
        }

        String ruleTokenKeys = (String) redisUtil.hGet(ruleKey(), SessionConstant.RULE_TOKEN_KEY);
        String tokenKey = tokenKey();
        String clearTokenKey = delStr(ruleTokenKeys, tokenKey);
        if(clearTokenKey!=null){
            redisUtil.hSet(ruleKey(),SessionConstant.RULE_TOKEN_KEY, clearTokenKey);
        }
    }

    private String delStr(String str,String target){
        if(StrKit.isNotBlank(str) && str.contains(target)){
            String[] split = str.split(",");
            List<String> ruleContentKeyList = Arrays.stream(split).filter(s -> !StrKit.equals(s, target)).collect(Collectors.toList());
            return StrKit.join(ruleContentKeyList,",");
        }
        return null;
    }

    /**
     * 释放数据
     *
     * @return
     */
    public void clearContent() {
        if(StrKit.equals(getJwt().getToken(), getRedisUserToken())) {
            //1、允许多端重复登录
            // 或者
            //2、当cookie token 与 redis中 rule的token一致时 允许删除，防止多端登录误删 用户session
            redisUtil.delete(contentKey());
        }
    }

    /**
     * 释放token
     *
     * @return
     */
    public void clearToken() {
        redisUtil.delete(tokenKey());
    }

    /**
     * 获取当前生成的token
     *
     * @return
     */
    @Override
    public String getToken() {
        if (getJwt() == null) {
            //            log.warn("jwt token must be not null,please checking or Create token");
            return null;
        }
        return this.getJwt().getToken();
    }

    /**
     * 获取用户redis存放的token
     *
     * @return
     */
    public String getRedisUserToken() {
        return (String) redisUtil.hGet(ruleKey(), sessionIdRedisKey());
    }

    /**
     * session异常
     * @return
     */
    public String getSessionError(){
        return (String) redisUtil.hGet(ruleKey(),SessionConstant.SESSION_RULE_ERROR_REDIS_KEY);
    }

    /**
     * 构建客户端传进来的token
     *
     * @param jwtResult
     * @return
     */
    public JwtInfoSessionHolder buildJwt(JwtResult jwtResult) {
        jwtResultThreadLocal.set(jwtResult);
        checkJwt(jwtResult);
        lengthen(jwtResult);
        return this;
    }

    /**
     * 验证jwt规则
     *
     * @param jwtResult
     */
    private void checkJwt(JwtResult jwtResult) {
        if (StrKit.isBlank(getRedisUserToken())) {
            //token对应的用户内容不存在
            throw JwtException.create(JwtCode.USER_LOSE_IN_REDIS);
        }
        String sessionError = getSessionError();
        if(StrKit.isNotBlank(sessionError)){
            JwtException exception = JwtException.create(JwtCode.BIZ_SESSION_ERROR);
            exception.setMessage(sessionError);
            throw exception;
        }
    }

    /**
     * 续期
     *
     * @param jwtResult
     */
    private void lengthen(JwtResult jwtResult) {
        if (JwtBuilder.isLengthen(sessionConfig, jwtResult)) {
            //需要续期
            JwtResult newResult = JwtBuilder.lengthen(sessionConfig, jwtResult);
            String userId = userId();
            clearToken();
            newToken(userId, newResult);
        }
    }

    @Override
    public String newToken(String userId) {
        return newToken(userId, JwtBuilder.newJwt(sessionConfig));
    }

    public String newToken(String userId, JwtResult jwtResult) {

        jwtResultThreadLocal.set(jwtResult);

        //存放userId 用于关联存放用户信息
        redisUtil.set(tokenKey(), userId, jwtResult.expires(), TimeUnit.SECONDS);

        this.refreshToken(jwtResult);
        return jwtResult.getToken();
    }

    /**
     * 刷新token
     *
     * @param jwtResult
     * @return
     */
    public JwtInfoSessionHolder refreshToken(JwtResult jwtResult) {
        jwtResultThreadLocal.set(jwtResult);
        //存放会话 规则
        //redisUtil.hSet(ruleKey(), SessionConstant.RULE_KEY, sessionConfig.getRule(), jwtResult.expires());
        //设置token 用于对比用户操作合法性
        redisUtil.hSet(ruleKey(), sessionIdRedisKey(), jwtResult.getToken(), jwtResult.expires());

        //存储用户相关redisKey,用于后续情况指定用户session使用
        pushClientRuleContentKey(jwtResult.expires());
        pushClientRuleTokenKey(jwtResult.expires());

        //session内容存在sessionId
        redisUtil.hSet(contentKey(), SessionConstant.ID_KEY, jwtResult.sessionId(), jwtResult.expires());
        return this;
    }


    private void pushClientRuleContentKey(long expires){
        String ruleContentKeys = (String) redisUtil.hGet(ruleKey(), SessionConstant.RULE_CONTENT_KEY);
        String contentKey = contentKey();
        if(StrKit.isNotBlank(ruleContentKeys)){
            if(!ruleContentKeys.contains(contentKey)){
                ruleContentKeys += (","+contentKey);
            }
        }else{
            ruleContentKeys = contentKey;
        }
        redisUtil.hSet(ruleKey(),SessionConstant.RULE_CONTENT_KEY, ruleContentKeys, expires);
    }

    private void pushClientRuleTokenKey(long expires){
        String ruleTokenKeys = (String) redisUtil.hGet(ruleKey(), SessionConstant.RULE_TOKEN_KEY);
        String tokenKey = tokenKey();
        if(StrKit.isNotBlank(ruleTokenKeys)){
            if(!ruleTokenKeys.contains(tokenKey)){
                ruleTokenKeys += (","+tokenKey);
            }
        }else{
            ruleTokenKeys = tokenKey;
        }
        redisUtil.hSet(ruleKey(), SessionConstant.RULE_TOKEN_KEY,ruleTokenKeys, expires);
    }

    /**
     * 规则rule sessionId key
     *
     * @return java.lang.String
     * @author WenJ
     * @date 2022/8/28 20:32
     * @version 1.0.0
     */
    private String sessionIdRedisKey() {
        return SessionConstant.ID_KEY;
    }


    /**
     * 用户ID
     *
     * @return
     */
    public String userId() {
        return (String) redisUtil.get(tokenKey());
    }

    private String tokenKey() {
        return SessionConstant.SESSION_TOKEN_KEY + this.getJwt().sessionId();
    }

    private String contentKey() {
        return SessionConstant.SESSION_CONTENT_KEY+ this.getJwt().sessionId();
    }
    private String ruleKey() {
        return ruleKey(userId());
    }

    private String ruleKey(String userId){
        return SessionConstant.SESSION_RULE_KEY + userId;
    }

    private JwtResult getJwt() {
        return this.jwtResultThreadLocal.get();
    }

}
