package com.manage.qq.repository;

import com.manage.qq.TestBase;
import com.manage.qq.dao.json.CommonKvDAO;
import org.junit.Test;

import javax.annotation.Resource;

public class CommonKvRepositoryTest extends TestBase {
    @Resource
    private CommonKvRepository commonKvRepository;

    @Test
    public void test() {
        CommonKvDAO s = new CommonKvDAO();
        s.setKey("1");
        s.setValue("2");
        System.out.println(commonKvRepository.save(s));
        System.out.println(commonKvRepository.findAll());
    }
}