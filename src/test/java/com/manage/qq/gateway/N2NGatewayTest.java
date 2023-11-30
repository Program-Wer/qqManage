package com.manage.qq.gateway;

import com.manage.qq.TestBase;
import org.junit.Test;

import javax.annotation.Resource;

public class N2NGatewayTest extends TestBase {
    @Resource
    private N2NGateway n2NGateway;

    @Test
    public void testRestart() {
        System.out.println("6666" + n2NGateway.restart());
    }
}