package com.tz.spike_shop.validator;

import com.tz.spike_shop.annotation.IsMobile;
import com.tz.spike_shop.utils.MobileValidatorUtil;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IsMobileValidator implements ConstraintValidator<IsMobile, String> {

    private boolean required = false;

    @Override
    public void initialize(IsMobile constraintAnnotation) {
        required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (required) {
            return MobileValidatorUtil.isMobile(value);
        }
        else {
            if (StringUtils.isEmpty(value)) return true;
            else return MobileValidatorUtil.isMobile(value);
        }
    }
}
