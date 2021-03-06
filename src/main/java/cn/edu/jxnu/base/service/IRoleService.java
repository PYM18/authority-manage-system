/* 梦境迷离 (C)2020 */
package cn.edu.jxnu.base.service;

import cn.edu.jxnu.base.entity.Role;
import reactor.core.publisher.Mono;

/**
 * 角色服务接口
 *
 * @author 梦境迷离
 * @version V2.0 2020年11月20日
 */
public interface IRoleService extends IBaseService<Role, Integer> {

    /**
     * 添加或者修改角色
     *
     * @param role 角色
     * @return Mono Role
     */
    Mono<Role> saveOrUpdate(Role role);

    /**
     * 给角色分配资源
     *
     * @param resourceIds 资源ID
     * @param id 角色ID
     * @return Mono Role
     */
    Mono<Role> grant(Integer id, String[] resourceIds);
}
