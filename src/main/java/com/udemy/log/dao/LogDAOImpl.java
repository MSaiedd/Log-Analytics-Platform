package com.udemy.log.dao;

import com.udemy.log.entity.Log;
import org.springframework.stereotype.Repository;

@Repository
public class LogDAOImpl implements LogDAO{
    @Override
    public boolean fetchLog() {
        return true;
    }
}
