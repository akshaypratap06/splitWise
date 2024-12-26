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
import java.util.stream.Collectors;

@Repository
public class UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public UserEntity addUser(User user) throws Exception{
        Session s= entityManager.unwrap(Session.class);

        Optional<UserEntity> existingUser= Optional.ofNullable(s.get(UserEntity.class,user.getUserName()));
        if(existingUser.isPresent()){
            throw new Exception("User already existed");
        }
        Optional<GroupEntity> groupEntity= Optional.ofNullable(s.get(GroupEntity.class, "default"));
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
    public UserEntity addUserToGroup(PatchUser user) throws Exception {
        Session s= entityManager.unwrap(Session.class);
        GroupEntity groupEntity= s.get(GroupEntity.class,user.getGroup());
        Optional<UserEntity> userEntity = getUser(user.getUserName());
        if(userEntity.isEmpty()){
            throw new Exception("User not yet created");
        }
        if(groupEntity==null ){
            throw new Exception("Group not yet created");
        }
        if(userEntity.get().getGroupEntityList().contains(groupEntity)){
            return userEntity.get();
        }
        List<GroupEntity> groupEntityList= userEntity.get().getGroupEntityList();
        groupEntityList.add(groupEntity);
        userEntity.get().setGroupEntityList(groupEntityList);
        return s.merge(userEntity.get());
    }
    @Transactional
    public List<UserEntity> getUsers(Set<String> users) {
        return entityManager.createQuery("select u from UserEntity u where u.userName in (:users)", UserEntity.class).setParameter("users",users).getResultList();
    }
    @Transactional
    public UserEntity removeUserFromGroup(PatchUser user) throws Exception {
        Session s= entityManager.unwrap(Session.class);
        Optional<UserEntity> userEntity = getUser(user.getUserName());
        if(userEntity.isEmpty()){
            throw new Exception("User not yet created");
        }
        List<GroupEntity> groupEntityList= userEntity.get().getGroupEntityList().stream().filter(group->!group.getGroupName().equalsIgnoreCase(user.getGroup())).collect(Collectors.toList());
        userEntity.get().setGroupEntityList(groupEntityList);
        return s.merge(userEntity.get());
    }
    @Transactional
    public Object deleteUser(String userId) {
        Session s= entityManager.unwrap(Session.class);
        s.delete(s.get(UserEntity.class,userId));
        return "SuccessFully Deleted";
    }
    @Transactional
    public UserEntity addFriendToUser(PatchUser user) throws Exception {
        Session s= entityManager.unwrap(Session.class);
        Optional<UserEntity> friendEntity= getUser(user.getFriend());
        Optional<UserEntity> userEntity = getUser(user.getUserName());
        if(userEntity.isEmpty()){
            throw new Exception("User not yet created");
        }
        if(friendEntity.isEmpty()){
            throw new Exception("Friend's User not yet created");
        }
        Set<String> userEntityList= userEntity.get().getFriendEntityList();
        userEntityList.add(friendEntity.get().getUserName());
        userEntity.get().setFriendEntityList(userEntityList);
        Set<String> friendEntityList= friendEntity.get().getFriendEntityList();
        friendEntityList.add(userEntity.get().getUserName());
        friendEntity.get().setFriendEntityList(friendEntityList);
        s.merge(friendEntity.get());
        return s.merge(userEntity.get());
    }
    @Transactional
    public UserEntity removeFriendFromUser(PatchUser user) throws Exception {
        Session s= entityManager.unwrap(Session.class);
        Optional<UserEntity> userEntity = getUser(user.getUserName());
        Optional<UserEntity> friendEntity= getUser(user.getFriend());
        if(userEntity.isEmpty()){
            throw new Exception("User not yet created");
        }
        if(friendEntity.isEmpty()){
            throw new Exception("Friend's User not yet created");
        }
        Set<String> userEntityList= userEntity.get().getFriendEntityList().stream().filter(friend-> !friend.equalsIgnoreCase(user.getFriend())).collect(Collectors.toSet());
        userEntity.get().setFriendEntityList(userEntityList);
        Set<String> friendEntityList= friendEntity.get().getFriendEntityList().stream().filter(friend-> !friend.equalsIgnoreCase(user.getUserName())).collect(Collectors.toSet());
        friendEntity.get().setFriendEntityList(friendEntityList);
        s.merge(friendEntity.get());
        return s.merge(userEntity.get());
    }
}
