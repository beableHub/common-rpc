package org.beable.common.rpc;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestServiceImpl implements TestService{

    @Override
    public String add(String name) {
      log.info("name:{}",name);
      return name;
    }
}
