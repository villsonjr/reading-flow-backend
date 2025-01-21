package com.ulk.readingflow.infraestructure.services;

import com.ulk.readingflow.api.exceptions.ResourceNotFoundException;
import com.ulk.readingflow.domain.entities.Role;
import com.ulk.readingflow.domain.enumerations.RoleEnum;
import com.ulk.readingflow.domain.enumerations.SystemMessages;
import com.ulk.readingflow.infraestructure.repositories.RoleRepository;
import com.ulk.readingflow.utils.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final MessageUtils messagesUtils;

    @Autowired
    public RoleService(RoleRepository roleRepository, MessageUtils messagesUtils) {
        this.roleRepository = roleRepository;
        this.messagesUtils = messagesUtils;
    }

    public List<Role> listRoles() {
        return roleRepository.findAll();
    }

    public Role createRole(Role role) {
        return roleRepository.save(role);
    }

    public Role updateRole(UUID roleId, RoleEnum roleDescription) {
        Role existingRole = findRoleById(roleId);
        existingRole.setDescription(roleDescription);
        return roleRepository.save(existingRole);
    }

    public void deleteRole(UUID roleId) {
        findRoleById(roleId);
        roleRepository.deleteById(roleId);
    }

    public Role findByDescription(RoleEnum description) {
        return roleRepository.findByDescription(description)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messagesUtils.getMessage(SystemMessages.RESOURCE_NOT_FOUND, description.getDescription())
                ));
    }

    private Role findRoleById(UUID roleId) {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messagesUtils.getMessage(SystemMessages.RESOURCE_NOT_FOUND, roleId.toString())
                ));
    }
}
