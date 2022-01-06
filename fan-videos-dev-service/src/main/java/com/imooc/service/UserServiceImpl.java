package com.imooc.service;

import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.imooc.mapper.UsersMapper;
import com.imooc.pojo.Users;
import com.imooc.pojo.UsersReport;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

@Service
public class UserServiceImpl implements UserService {
    // 将UserMapper 注入到当前接口实现类中，辅助完成queryUsernameIsExist方法

    @Autowired
    private UsersMapper usersMapper;
    // 注入Sid工具,为后面用户注册（保存用户对象）生成唯一id存入数据库
    @Autowired
    private Sid sid;
    /*
    如果类加了这个注解，那么这个类里面的方法抛出异常，就会回滚，数据库里面的数据也会回滚。
    事务回滚注解 @Transactional
    Propagation.SUPPORTS 事务传播行为设置
    @Transactional(propagation=Propagation.REQUIRED)
    如果有事务, 那么加入事务, 没有的话新建一个(默认情况下)
    @Transactional(propagation=Propagation.NOT_SUPPORTED)
    容器不为这个方法开启事务
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    不管是否存在事务,都创建一个新的事务,原来的挂起,新的执行完毕,继续执行老的事务
    @Transactional(propagation=Propagation.MANDATORY)
    必须在一个已有的事务中执行,否则抛出异常
    @Transactional(propagation=Propagation.NEVER)
    必须在一个没有的事务中执行,否则抛出异常(与Propagation.MANDATORY相反)
    @Transactional(propagation=Propagation.SUPPORTS)
    如果其他bean调用这个方法,在其他bean中声明事务,那就用事务.如果其他bean没有声明事务,那就不用事务.

事物超时设置:
@Transactional(timeout=30) //默认是30秒
    */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    // 判断用户是否为空
    public boolean queryUsernameIsExist(String username) {

        //创建一个用户对象，并设置用户名条件
        Users users = new Users();
        users.setUsername(username);

        // 数据库单条数据查询
         Users result = usersMapper.selectOne(users);
         // 判断result 是否为空，如果为空，则当前用户没有注册

        return result == null ? false : true;
    }

    // 保存用户对象
    // 生成用户sid 注入sid工具
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveUser(Users user) {
       String userId = sid.nextShort();
       user.setId(userId);
       // 调用数据库方法插入对象数据
        usersMapper.insert(user);
    }
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    // 查询登录用户
    // mybatis的逆向工程中会生成实例及实例对应的example，example用于添加条件，相当where后面的部分
    public Users queryUserForLogin(String username, String password) {
    // 构建一个标准实例criteria
        Example userExample = new Example(Users.class);
        Criteria criteria = userExample.createCriteria();

        criteria.andEqualTo("username",username);
        criteria.andEqualTo("password",password);
        Users result = usersMapper.selectOneByExample(userExample);

        return result;
    }

    @Override
    public void updateUserInfo(Users user) {

    }

    @Override
    public Users queryUserInfo(String userId) {
        return null;
    }

    @Override
    public boolean isUserLikeVideo(String userId, String videoId) {
        return false;
    }

    @Override
    public void saveUserFanRelation(String userId, String fanId) {

    }

    @Override
    public void deleteUserFanRelation(String userId, String fanId) {

    }

    @Override
    public boolean queryIfFollow(String userId, String fanId) {
        return false;
    }

    @Override
    public void reportUser(UsersReport userReport) {

    }
}

