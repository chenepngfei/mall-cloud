package cn.afei.mall.module.system.dal.mysql.sms;

import cn.afei.mall.framework.jpa.core.query.LambdaSpecifications;
import cn.afei.mall.framework.jpa.core.repository.BaseRepositoryX;
import cn.afei.mall.module.system.dal.dataobject.sms.SmsCodeDO;

public interface SmsCodeRepository extends BaseRepositoryX<SmsCodeDO, Long> {

    /**
     * 获得手机号的最后一个手机验证码
     *
     * @param mobile 手机号
     * @param scene 发送场景，选填
     * @param code 验证码 选填
     * @return 手机验证码
     */
    default SmsCodeDO selectLastByMobile(String mobile, String code, Integer scene) {
        LambdaSpecifications<SmsCodeDO> spec = LambdaSpecifications.<SmsCodeDO>of()
                .eq(SmsCodeDO::getMobile, mobile);
        if (scene != null) {
            spec.eq(SmsCodeDO::getScene, scene);
        }
        if (code != null) {
            spec.eq(SmsCodeDO::getCode, code);
        }
        return selectFirstOne(spec.orderByDesc(SmsCodeDO::getId).build());
    }

}
