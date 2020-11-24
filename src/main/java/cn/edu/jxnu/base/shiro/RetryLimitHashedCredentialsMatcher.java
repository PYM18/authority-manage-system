/* 梦境迷离 (C)2020 */
package cn.edu.jxnu.base.shiro;

import cn.edu.jxnu.base.scheduler.Scheduler;
import cn.edu.jxnu.base.service.IUserService;
import cn.edu.jxnu.base.utils.Constats;
import cn.edu.jxnu.base.utils.MD5Utils;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 独立的密码验证工具
 *
 * @author 梦境迷离
 * @version V2.0 2020年11月20日
 */
@Slf4j
@Component
public class RetryLimitHashedCredentialsMatcher extends HashedCredentialsMatcher {

    @Autowired private IUserService userService;

    private Cache<String, AtomicInteger> passwordRetryCache;

    RetryLimitHashedCredentialsMatcher(CacheManager cacheManager) {
        setHashAlgorithmName("MD5");
        setHashIterations(1);
        setStoredCredentialsHexEncoded(true);
        log.info("初始化获取一个密码passwordRetryCache缓存");
        passwordRetryCache = cacheManager.getCache("passwordRetryCache");
    }

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        // token用于收集用户提交的身份,info是来自MyRealm的，用户名和密码
        // info，第一个参数user，这里好多地方传的时候都是user对象，但是都在备注用户名
        // info中的密码是从数据库中获取的password。
        // info第三个字段是realm，即当前realm的名称。
        log.info("进入密码验证" + getHashAlgorithmName() + getHashIterations());
        String userCode = (String) token.getPrincipal(); // 用户名
        AtomicInteger retryCount = passwordRetryCache.get(userCode);
        if (retryCount == null) {
            log.info(userCode + ":第一次尝试，存放进缓存");
            retryCount = new AtomicInteger(0);
            passwordRetryCache.put(userCode, retryCount);
            // Scheduler.list.addLast(userCode);
            Scheduler.map.put(userCode, System.currentTimeMillis());
        }
        if (retryCount.incrementAndGet() > 5) {
            log.info("超过5次，抛出异常");
            throw new ExcessiveAttemptsException("密码输入错误超过5次，请等待10分钟后尝试");
        }
        String password = new String((char[]) token.getCredentials());
        // 密码错误
        if (!MD5Utils.md5(password).equals(info.getCredentials())) {
            log.info("第" + retryCount.get() + "次尝试，存放进缓存");
            log.info("用户输入密码:" + MD5Utils.md5(password) + " 数据库密码：" + info.getCredentials());
            throw new IncorrectCredentialsException("账号或密码不正确");
        }
        // boolean matches = super.doCredentialsMatch(token, info);
        // 有问题，这里可能是因为加密的MD5不同，所以没用，自定义md5()实现是：toString(32).toUpperCase()
        SecurityUtils.getSubject().getSession().setTimeout(1000 * 60 * 10);
        userService
                .findByUserCode(userCode)
                .subscribe(
                        user -> {
                            SecurityUtils.getSubject()
                                    .getSession()
                                    .setAttribute(Constats.CURRENTUSER + user.getId(), user);
                        });
        log.info("登录成功，去除缓存");
        if (passwordRetryCache.get(userCode) != null) passwordRetryCache.remove(userCode);
        // if (Scheduler.list.contains(userCode)) {
        // Scheduler.list.remove(userCode);
        // }
        Scheduler.map.remove(userCode);

        return true;
    }

    /** @return Cache */
    public Cache<String, AtomicInteger> getPasswordRetryCache() {
        return passwordRetryCache;
    }
}
