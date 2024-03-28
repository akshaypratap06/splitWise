package com.example.splitwise.dao;

import com.example.splitwise.entity.GroupEntity;
import com.example.splitwise.model.PatchUser;
import com.example.splitwise.model.User;
import com.example.splitwise.entity.UserEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public UserEntity addUser(User user) throws Exception{
        Session s= entityManager.unwrap(Session.class);
        Optional<GroupEntity> groupEntity= Optional.ofNullable(s.get(GroupEntity.class, "default"));
        Optional<UserEntity> existingUser= Optional.ofNullable(s.get(UserEntity.class,user.getUserName()));
        if(existingUser.isPresent()){
            throw new Exception("User already existed");
        }
        UserEntity userEntity=user.toUserEntity();
        groupEntity.ifPresent(entity -> userEntity.setGroupEntityList(Collections.singletonList(entity)));
        return s.merge(userEntity);
    }
    @Transactional
    public Optional<UserEntity> getUser(String userID) {
        Session s= entityManager.unwrap(Session.class);
        return Optional.ofNullable(s.get(UserEntity.class, userID));
    }
    @Transactional
    public List<UserEntity> getAllUser() {
        return entityManager.createQuery("select u from UserEntity u", UserEntity.class).getResultList();
    }
    @Transactional
    public UserEntity patchUser(PatchUser user) throws Exception {
        Session s= entityManager.unwrap(Session.class);
        List<GroupEntity> groupEntityList= entityManager.createQuery("select g from GroupEntity g where g.groupName in (:groupName)",GroupEntity.class).setParameter("groupName",user.getGroups()).getResultList();
        Optional<UserEntity> userEntity = getUser(user.getUserName());
        if(userEntity.isEmpty()){
            throw new Exception("User not yet created");
        }
        userEntity.get().setGroupEntityList(groupEntityList);
        return s.merge(userEntity.get());
    }
    @Transactional
    public List<UserEntity> getUsers(Set<String> users) {
        return entityManager.createQuery("select u from UserEntity u where u.userName in (:users)", UserEntity.class).setParameter("users",users).getResultList();
    }
}
