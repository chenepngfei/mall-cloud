package cn.afei.mall.module.system.dal.mysql.dept;

import cn.afei.mall.framework.jpa.core.query.LambdaSpecifications;
import cn.afei.mall.framework.jpa.core.repository.BaseRepositoryX;
import cn.afei.mall.module.system.dal.dataobject.dept.UserPostDO;

import java.util.Collection;
import java.util.List;

public interface UserPostRepository extends BaseRepositoryX<UserPostDO, Long> {

    default List<UserPostDO> selectListByUserId(Long userId) {
        return selectList(UserPostDO::getUserId, userId);
    }

    default void deleteByUserIdAndPostId(Long userId, Collection<Long> postIds) {
        delete(LambdaSpecifications.<UserPostDO>of()
                .eq(UserPostDO::getUserId, userId)
                .in(UserPostDO::getPostId, postIds)
                .build());
    }

    default List<UserPostDO> selectListByPostIds(Collection<Long> postIds) {
        return selectList(UserPostDO::getPostId, postIds);
    }

    default void deleteByUserId(Long userId) {
        delete(LambdaSpecifications.<UserPostDO>of()
                .eq(UserPostDO::getUserId, userId)
                .build());
    }
}
