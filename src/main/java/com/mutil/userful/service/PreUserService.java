package com.mutil.userful.service;

import com.mutil.userful.dao.MmallUserMapper;
import com.mutil.userful.domain.MmallUser;
import com.mutil.userful.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PreUserService {

    @Autowired
    private MmallUserMapper mmallUserMapper;

    /**
     * 登入验证
     * @param account
     * @param password
     * @return
     */
    public MmallUser validateUser(String account, String password){
        MmallUser mmallUser = new MmallUser();
        mmallUser.setUsername(account);
        mmallUser.setPassword(MD5Util.MD5EncodeUtf8(password));
        MmallUser mmallUserBack = mmallUserMapper.selectByMmallUser(mmallUser);
        if(mmallUserBack!=null){
            mmallUserBack.setPassword(null);
            mmallUserBack.setQuestion(null);
            mmallUserBack.setAnswer(null);
        }
        return mmallUserBack;
    }
}
