package com.ntech.service.impl;

import com.ntech.dao.CustomerMapper;
import com.ntech.model.Customer;
import com.ntech.model.CustomerExample;
import com.ntech.model.LibraryKey;
import com.ntech.service.inf.ICustomerService;
import com.ntech.service.inf.ILibraryService;
import com.ntech.service.inf.ISetMealService;
import com.ntech.service.inf.IShowManage;
import com.ntech.util.MailUtil;
import com.ntech.util.SHAencrypt;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
public class CustomerService implements ICustomerService {

    private static final Logger logger = Logger.getLogger(CustomerService.class);

    @Autowired
    CustomerMapper customerMapper;

    @Autowired
    ILibraryService libraryService;

    @Autowired
    ISetMealService setMealService;

    @Autowired
    IShowManage showManage;
//    @Autowired
//    IShowManage showManage;


    @Transactional
    public int add(Customer customer) throws MessagingException {
        customer.setRegtime(new Date());
        customer.setActive(1);
        int result = customerMapper.insert(customer);
        if (result == 1) {
            LibraryKey libraryKey = new LibraryKey();
            libraryKey.setLibraryName(customer.getName());
            libraryKey.setUserName(customer.getName());
            if (libraryService.insert(libraryKey) == 1) {
//                sendEmail(customer);
                return 1;
            }
        }
        logger.error("add user fail " + customer.getName());
        return 0;
    }

    public int delete() {
        return 1;
    }

    //根据用户名删除用户
    public boolean deleteByName(String name) {
        CustomerExample example = new CustomerExample();
        example.createCriteria().andNameEqualTo(name);
        if (customerMapper.deleteByExample(example) == 1) {
            try {
                showManage.deleteGallery(name);
            } catch (IOException e) {
                e.printStackTrace();
            }
            logger.info("delete customer succeess");
            return true;
        }
        return false;
    }

    public int modify(Customer customer) {
        logger.info("modify customer:" + customer.getName());
        CustomerExample example = new CustomerExample();
        example.createCriteria().andNameEqualTo(customer.getName());
        return customerMapper.updateByExampleSelective(customer, example);

    }

    public int modifySelective(Customer customer) {
        logger.info("modify customer selective:"+customer.getName());
        CustomerExample example = new CustomerExample();
        example.createCriteria().andNameEqualTo(customer.getName());
        return 1;
    }

    public List<Customer> findAll() {
        logger.info("find all customer");
        CustomerExample example = new CustomerExample();
        example.createCriteria().andNameIsNotNull();
        return customerMapper.selectByExample(example);
    }

    public Customer findByName(String name) {
        logger.info("get user name:" + name);
        Customer customer = null;
        CustomerExample example = new CustomerExample();
        example.createCriteria().andNameEqualTo(name);
        List<Customer> list = customerMapper.selectByExample(example);
        if (list.size() > 0) {
            customer = list.get(0);
        }
        return customer;
    }

    public Customer findByToken(String token) {
        logger.info("get user");
        Customer customer = null;
        CustomerExample example = new CustomerExample();
        example.createCriteria().andTokenEqualTo(token);
        List<Customer> list = customerMapper.selectByExample(example);
        if (list.size() > 0) {
            customer = list.get(0);
        }
        return customer;
    }

    public boolean checkUserName(String userName) {
        logger.info("check user name:" + userName);
        CustomerExample example = new CustomerExample();
        example.createCriteria().andNameEqualTo(userName);
        List<Customer> result = customerMapper.selectByExample(example);
        return result.size() > 0 ? true : false;
    }


    public boolean modifyPwd(String userName, String password,String newPassword) {
        if(loginCheck(userName,password)){
            logger.info("check pass");
            Customer customer = findByName(userName);
            customer.setPassword(SHAencrypt.encryptSHA(newPassword));
            synchronized (this) {
                if (modify(customer) == 1) {
                    return true;
                }
            }
        }

        return false;
    }


    public boolean forgetPwd(String name, String email) {
        Customer customer = findByName(name);
        if(customer!=null) {
            if (customer.getEmail().equals(email)) {
                try {
                    sendEmailForPassword(customer);
                } catch (MessagingException e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    public boolean checkToken(String inputToken) {
        logger.info("check user token: " + inputToken);
//        if(setMealService.findByName()){
//            return false;
//        }
        CustomerExample example = new CustomerExample();
        example.createCriteria().andTokenEqualTo(inputToken);
        List<Customer> result = customerMapper.selectByExample(example);
        return result.size() > 0 ? true : false;
    }

    public boolean enableToken(String name) {
        if (null != name && !name.equals("")) {
            CustomerExample example = new CustomerExample();
            example.createCriteria().andNameEqualTo(name);
            List<Customer> result = customerMapper.selectByExample(example);
            if (result.size() > 0) {
                Customer customer = result.get(0);
                customer.setToken(SHAencrypt.encryptSHA(name));
                logger.info(SHAencrypt.encryptSHA(name));
                if(customerMapper.updateByExample(customer,example)==1){
                    logger.info("enable token success for " +name);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean disableToken(String name) {
        if (null != name && !name.equals("")) {
            CustomerExample example = new CustomerExample();
            example.createCriteria().andNameEqualTo(name);
            List<Customer> result = customerMapper.selectByExample(example);
            if (result.size() > 0) {
                Customer customer = result.get(0);
                customer.setToken(null);
                if(customerMapper.updateByExample(customer,example)==1){
                    logger.info("disable token success for " +name);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean checkFaceNumber(String name) {
        Customer customer = findByName(name);
        if(customer.getFaceNumber()<50&&customer.getFaceNumber()>=0){
            return true;
        }
        return false;
    }

    public boolean operateFaceNumber(String name, int operate,int faceNumber) {
        Customer customer = findByName(name);
        if(customer.getFaceNumber()<50&&customer.getFaceNumber()>=0){
            if(operate==0){
                customer.setFaceNumber(customer.getFaceNumber()-faceNumber);
            }
            if(operate==1){
                customer.setFaceNumber(customer.getFaceNumber()+faceNumber);
            }
            CustomerExample example = new CustomerExample();
            example.createCriteria().andNameEqualTo(name);
            if(customerMapper.updateByExampleSelective(customer,example)==1){
                return true;
            }
        }
        return false;
    }

    public boolean loginCheck(String name, String password) {
        Customer customer = findByName(name);
        if (null != customer) {
            if (0 == customer.getActive()) {
                logger.warn(name + " user is not active");
                return false;
            }
            logger.info("check login info");
            try {
                password = SHAencrypt.encryptSHA(password);
            } catch (Exception e) {
                e.printStackTrace();
            }
            CustomerExample example = new CustomerExample();
            example.createCriteria().andNameEqualTo(name).andPasswordEqualTo(password);
            List<Customer> result = customerMapper.selectByExample(example);
            return result.size() > 0 ? true : false;
        } else {
            return false;
        }
    }

    public boolean setContype(String name, String contype) {
        logger.info("set contype for user:" + name);
        CustomerExample example = new CustomerExample();
        example.createCriteria().andNameEqualTo(name);
        Customer customer = customerMapper.selectByExample(example).get(0);
//        customer.setContype(contype);
        customerMapper.updateByExample(customer, example);
        return false;
    }

    public String getNameByToken(String name) {
        return null;
    }

    private void sendEmailForActive(Customer customer) throws MessagingException {
        String validateCode = SHAencrypt.encryptSHA(customer.getEmail());
        StringBuffer content = new StringBuffer("点击下面链接激活账号，48小时生效，否则重新注册账号，链接只能使用一次，请尽快激活！</br>");
        content.append("<a href=\"http://localhost:8080/customer/active?email=");
        content.append(customer.getEmail());
        content.append("&name=");
        content.append(customer.getName());
        content.append("&validateCode=");
        content.append(validateCode);
        content.append("\">http://localhost:8080/customer/active?action=activate&email=");
        content.append(customer.getEmail());
        content.append("&validateCode=");
        content.append(validateCode);
        content.append("</a>");
        MailUtil.send_mail(customer.getEmail(), content.toString());
    }
    private void sendEmailForPassword(Customer customer) throws MessagingException {
        String validateCode = SHAencrypt.encryptSHA(customer.getEmail());
        StringBuffer content = new StringBuffer("点击下面链接激活账号，48小时生效，请尽快修改密码！</br>");
        content.append("<a href=\"http://yun.anytec.cn:8080/customer/forgetPwdPageCheck?email=");
        content.append(customer.getEmail());
        content.append("&name=");
        content.append(customer.getName());
        content.append("&validateCode=");
        content.append(validateCode);
        content.append("\">http://yun.anytec.cn:8080/customer/forgetPwdPageCheck?action=activate&email=");
        content.append(customer.getEmail());
        content.append("&validateCode=");
        content.append(validateCode);
        content.append("</a>");
        MailUtil.send_mail(customer.getEmail(), content.toString());
    }


    public long totalCount() {
        CustomerExample example = new CustomerExample();
        example.createCriteria().andNameIsNotNull();
        logger.info("find count of customers");
        return (int) customerMapper.countByExample(example);
    }
    public List<Customer> findPage(int limit, int offset) {
        return customerMapper.findPage(limit, offset);
    }


    public List<Customer> findLikeName(String name) {

        return customerMapper.findLikeName(name);
    }
}
