package com.example.splitwise.dao;

import com.example.splitwise.entity.GroupEntity;
import com.example.splitwise.model.Group;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class GroupDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private  UserDao userDao;

    @Transactional
    public GroupEntity createGroup(Group group) throws Exception {
        Session s= entityManager.unwrap(Session.class);
        Optional<GroupEntity> groupEntity= Optional.ofNullable(s.get(GroupEntity.class, group.getName()));
        if(groupEntity.isPresent()){
            throw new Exception("Group already created");
        }
//        System.out.println(Collections.singletonList(userDao.getUser(group.getCreatedBy()).get().toString()));
        group.setUserEntityList(userDao.getUsers(group.getUsers()));
        return s.merge(group.toGroupEntity());
    }

    @Transactional
    public List<GroupEntity> getAllGroup() {
        return entityManager.createQuery("select u from GroupEntity u", GroupEntity.class).getResultList();
    }
    @Transactional
    public GroupEntity getGroup(String groupid) throws Exception {
        Session s= entityManager.unwrap(Session.class);
        Optional<GroupEntity> groupEntity= Optional.ofNullable(s.get(GroupEntity.class, groupid));
        if(groupEntity.isEmpty()){
            throw new Exception("Group Not Present");
        }
        return groupEntity.get();

    }
}
