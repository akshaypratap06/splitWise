package com.example.splitwise.manager;

import com.example.splitwise.entity.GroupEntity;
import com.example.splitwise.entity.UserEntity;
import com.example.splitwise.model.GroupDTO;
import com.example.splitwise.model.UserDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UtilManager {

    public UserDTO convertToUserDTO(UserEntity userEntity,boolean parent) {
        UserDTO userDTO= new UserDTO(userEntity.getUserName(),userEntity.getEmail(),userEntity.getCreationTime(),userEntity.getFriendEntityList());
        if(parent) {
            List<GroupDTO> groupDTOList = new ArrayList<>();
            for (GroupEntity group : userEntity.getGroupEntityList()) {
                groupDTOList.add(convertToGroupDto(group, false));
            }
            userDTO.setGroupEntityList(groupDTOList);
        }
        return userDTO;
    }

    public List<UserDTO> convertToUserDTOList(List<UserEntity> userEntity,boolean parent) {
        List<UserDTO> userList= new ArrayList<>();
        for (int i = 0; i < userEntity.size(); i++) {
            userList.add(convertToUserDTO(userEntity.get(i),parent));
        }
        return userList;
    }

    public GroupDTO convertToGroupDto(GroupEntity group,boolean parent) {
        GroupDTO groupDTO= new GroupDTO(group.getGroupName(),group.getCreationDate());
        if(parent) {
            List<UserDTO> userDTOS = new ArrayList<>();
            for (UserEntity user : group.getUsers()) {
                userDTOS.add(convertToUserDTO(user,false));
            }
            groupDTO.setUsers(userDTOS);
        }
        return groupDTO;
    }

    public List<GroupDTO> convertToGroupDtoList(List<GroupEntity> group,boolean parent) {
        List<GroupDTO> groupList= new ArrayList<>();
        for (int i = 0; i < group.size(); i++) {
            groupList.add(convertToGroupDto(group.get(i),parent));
        }
        return groupList;
    }
}
