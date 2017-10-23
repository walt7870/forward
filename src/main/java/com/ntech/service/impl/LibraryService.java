package com.ntech.service.impl;

import com.ntech.dao.CustomerMapper;
import com.ntech.dao.LibraryMapper;
import com.ntech.model.Customer;
import com.ntech.model.CustomerExample;
import com.ntech.model.LibraryExample;
import com.ntech.model.LibraryKey;
import com.ntech.service.inf.ILibraryService;
import com.ntech.service.inf.IShowManage;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class LibraryService implements ILibraryService{

    private static final Logger logger= Logger.getLogger(LibraryService.class);

    @Autowired
    LibraryMapper libraryMapper;

    @Autowired
    CustomerMapper customerMapper;

    @Autowired
    IShowManage showManage;

    public int insert(LibraryKey libraryKey) {
        int result=0;
        try {
            if(showManage.containsGallery(libraryKey.getLibraryName())){
                showManage.deleteGallery(libraryKey.getLibraryName());
                showManage.createGallery(libraryKey.getLibraryName());
            }else{
                showManage.createGallery(libraryKey.getLibraryName());
            }
            result=libraryMapper.insert(libraryKey);
        } catch (IOException e) {
            logger.error("add lib error:"+e.getMessage());
            return result;
        }
        return result;
    }
    public int create(LibraryKey libraryKey) {
        return libraryMapper.insert(libraryKey);
    }

    public int delete(LibraryKey libraryKey) {
        return libraryMapper.deleteByPrimaryKey(libraryKey);
    }
    public int modify(LibraryKey libraryKey) {
        LibraryExample example = new LibraryExample();
        example.createCriteria().andLibraryNameEqualTo(libraryKey.getUserName());
        return libraryMapper.updateByExampleSelective(libraryKey,example);
    }

    public List<LibraryKey> findAll() {
        LibraryExample example = new LibraryExample();
        example.createCriteria().andUserNameIsNotNull();
        List<LibraryKey> list = libraryMapper.selectByExample(example);
        return list;
    }

    public List<String> findByUserName(String userName) {
        LibraryExample example = new LibraryExample();
        example.createCriteria().andUserNameEqualTo(userName);
        List<LibraryKey> list= libraryMapper.selectByExample(example);
        if(null!=list&&list.size()>0){
            List<String> galleries= new ArrayList<String>();
            for(LibraryKey libraryKey:list){
                galleries.add(libraryKey.getLibraryName());
                return galleries;
            }
        }
        return null;
    }
    public List<LibraryKey> findLKByUserName(String userName) {
        LibraryExample example = new LibraryExample();
        example.createCriteria().andUserNameEqualTo(userName);
        List<LibraryKey> list= libraryMapper.selectByExample(example);
        return list;
    }

    public List<LibraryKey> findByToken(String token) {
        CustomerExample example = new CustomerExample();
        example.createCriteria().andTokenEqualTo(token);
        List<Customer> customers= customerMapper.selectByExample(example);
        if (customers.size()>0){
            Customer customer= customers.get(0);
            LibraryExample libraryExample = new LibraryExample();
            libraryExample.createCriteria().andUserNameEqualTo(customer.getName());
            return libraryMapper.selectByExample(libraryExample);
        }
        return null;
    }

    public boolean checkLibrary(String userName, String libraryName) {
        LibraryExample example= new LibraryExample();
        example.createCriteria().andUserNameEqualTo(userName).andLibraryNameEqualTo(libraryName);
        List<LibraryKey> libraryKeys=libraryMapper.selectByExample(example);
        if(null!=libraryKeys&&libraryKeys.size()>0){
            return true;
        }
        return false;
    }
}
