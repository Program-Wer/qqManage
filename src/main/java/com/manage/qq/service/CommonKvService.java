package com.manage.qq.service;

import com.manage.qq.dao.json.CommonKvDAO;
import com.manage.qq.enums.CommonKvEnum;
import com.manage.qq.repository.CommonKvRepository;
import com.manage.qq.util.JsonUtil;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

@Service
public class CommonKvService {
    @Resource
    private CommonKvRepository commonKvRepository;


    public <T> T getKey(CommonKvEnum commonKvEnum) {
        return getKeyOrDefault(commonKvEnum, null);
    }

    public <T> T getKeyOrDefault(CommonKvEnum commonKvEnum, T defaultValue) {
        return getKeyOrDefault(commonKvEnum, null, defaultValue);
    }

    public <T> T getKeyOrDefault(CommonKvEnum commonKvEnum, String key, T defaultValue) {
        String commonKey = key == null ? commonKvEnum.name().toLowerCase() : commonKvEnum.name().toLowerCase() + "_" + key;
        Optional<CommonKvDAO> kvDAOOptional = commonKvRepository.findById(commonKey);
        String value = kvDAOOptional.map(CommonKvDAO::getValue).orElse(null);
        if (value == null) {
            return defaultValue;
        }
        if (Long.class.isAssignableFrom(commonKvEnum.getTClass())) {
            return (T) NumberUtils.createLong(value);
        } else if (Integer.class.isAssignableFrom(commonKvEnum.getTClass())) {
            return (T) NumberUtils.createInteger(value);
        } else if (String.class.isAssignableFrom(commonKvEnum.getTClass())) {
            return (T) value;
        } else {
            return (T) JsonUtil.fromJson(value, commonKvEnum.getTClass());
        }
    }

    public void setValue(CommonKvEnum commonKvEnum, Object value) {
        CommonKvDAO commonKvDAO = new CommonKvDAO();
        commonKvDAO.setKey(commonKvEnum.name().toLowerCase());
        if (value == null) {
            commonKvDAO.setValue(null);
        } else {
            commonKvDAO.setValue(JsonUtil.toJson(value));
        }
        commonKvRepository.save(commonKvDAO);
    }

    public void setValue(CommonKvEnum commonKvEnum, String key, Object value) {
        CommonKvDAO commonKvDAO = new CommonKvDAO();
        if (key == null) {
            commonKvDAO.setKey(commonKvEnum.name().toLowerCase());
        } else {
            commonKvDAO.setKey(commonKvEnum.name().toLowerCase() + "_" + key);
        }
        if (value == null) {
            commonKvDAO.setValue(null);
        } else {
            commonKvDAO.setValue(JsonUtil.toJson(value));
        }
        commonKvRepository.save(commonKvDAO);
    }
}
