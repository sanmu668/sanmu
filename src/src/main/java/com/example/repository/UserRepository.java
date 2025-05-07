package com.example.repository;

import com.example.dto.UserDTO;
import com.example.entity.Job;
import com.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

/**
 * @author wzs
 * @date 2025年04月27日 23:43
 */

/**
 * 用户仓库接口，继承JpaRepository接口，用于执行用户相关的数据库操作
 * 该接口主要用于定义和用户实体相关的数据库操作，利用Spring Data JPA提供的强大功能
 * 可以不编写实体类，直接通过接口方法的命名约定来实现对数据库的操作
 */
public interface UserRepository extends JpaRepository<User,Integer> , JpaSpecificationExecutor<User> {
    /**
     * 根据邮箱查询用户信息
     *
     * @param email 用户邮箱，作为查询条件
     * @return 返回一个Optional包装的对象，如果未找到对应邮箱的用户，则返回Optional.empty()
     *
     * 使用Optional作为返回类型，可以优雅地处理可能的空值情况，调用者需要处理Optional为空的情况
     */
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.email = :email")
    User getUserByEmail(@Param("email") String email); // 查不到时返回null
}
