package net.oxizen.spring;

import net.oxizen.spring.exception.ApiException;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Method;

public class OxValidator {
    public static void validate(Object model) {
        validate(model, new MandatoryChecker() {
            @Override
            public boolean isMandatory(String name) {
                return true;
            }
        });
    }

    public static void validate(Object model,final String... fields) {
        validate(model, new MandatoryChecker() {
            @Override
            public boolean isMandatory(String name) {
                for (String field : fields) {
                    if (name.substring(3).toLowerCase().equals(field.toLowerCase())) return true;
                }
                return false;
            }
        });
    }

    public static void validateWithout(Object model,final String... fields) {
        validate(model, new MandatoryChecker() {
            @Override
            public boolean isMandatory(String name) {
                for (String field : fields) {
                    if (name.substring(3).toLowerCase().equals(field.toLowerCase())) return false;
                }
                return true;
            }
        });
    }

    public static void validate(Object model, MandatoryChecker checker) {
        try {
            Method[] methods = model.getClass().getMethods();
            for (Method method : methods) {
                if (!method.getName().startsWith("get")) continue;
                if (!checker.isMandatory(method.getName())) continue;
                Object value = method.invoke(model);
                if (value == null || value.toString().isEmpty()) {
                    throw new ApiException(HttpStatus.BAD_REQUEST, "필수요소가 누락되었습니다");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private interface MandatoryChecker {
        boolean isMandatory(String name);
    }
}
