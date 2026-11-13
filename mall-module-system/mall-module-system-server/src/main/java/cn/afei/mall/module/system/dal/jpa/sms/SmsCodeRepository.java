package cn.afei.mall.module.system.dal.jpa.sms;

import cn.afei.mall.framework.mybatis.core.repository.BaseRepositoryX;
import cn.afei.mall.framework.mybatis.core.repository.JpaSpecUtil;
import cn.afei.mall.framework.tenant.core.db.TenantSpec;
import cn.afei.mall.module.system.dal.dataobject.sms.SmsCodeDO;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

/**
 * 短信验证码 Repository（自原 SmsCodeMapper 迁移；@TenantIgnore 全局表，tenantSpec 自动跳过）
 *
 * @author 芋道源码
 */
public interface SmsCodeRepository extends BaseRepositoryX<SmsCodeDO, Long> {

    /**
     * 查最新一条验证码（原 selectFirstOne + 排序取第一条，语义一致）
     */
    default SmsCodeDO selectLastByMobile(String mobile, String code, Integer scene) {
        Specification<SmsCodeDO> spec = Specification.<SmsCodeDO>where(JpaSpecUtil.eq("mobile", mobile));
        if (scene != null) {
            spec = spec.and(JpaSpecUtil.eq("scene", scene));
        }
        if (code != null) {
            spec = spec.and(JpaSpecUtil.eq("code", code));
        }
        return selectFirstOne(spec.and(TenantSpec.tenantSpec()).and(JpaSpecUtil.logicDeleteSpec()),
                Sort.by(Sort.Direction.DESC, "id"));
    }

}
