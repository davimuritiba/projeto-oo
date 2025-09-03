package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import model.Group;
import model.Privacy;
import model.User;

public class GroupController {
    private List<Group> groups;
    private UserController userController;
    
    public GroupController(UserController userController) {
        this.groups = new ArrayList<>();
        this.userController = userController;
    }

    public Group createGroup(String name, String description, UUID ownerId, Privacy privacy) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do grupo não pode estar vazio");
        }
        
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Descrição do grupo não pode estar vazio");
        }
        
        if (ownerId == null) {
            throw new IllegalArgumentException("ID do proprietário não pode ser nulo");
        }
        
        if (privacy == null) {
            throw new IllegalArgumentException("Privacidade do grupo não pode ser nula");
        }

        User owner = userController.getUserById(ownerId);
        if (owner == null) {
            throw new IllegalArgumentException("Usuário proprietário não encontrado");
        }
        
        Group group = new Group(name.trim(), description.trim(), ownerId, privacy);
        groups.add(group);
        return group;
    }

    public boolean editGroup(UUID groupId, UUID userId, String newName, String newDescription, Privacy newPrivacy) {
        Group group = findGroupById(groupId);
        if (group == null) return false;
        
        if (!group.canModify(userId)) {
            return false; // Usuário não tem permissão para modificar
        }
        
        if (newName != null && !newName.trim().isEmpty()) {
            group.setName(newName.trim());
        }
        
        if (newDescription != null && !newDescription.trim().isEmpty()) {
            group.setDescription(newDescription.trim());
        }
        
        if (newPrivacy != null) {
            group.setPrivacy(newPrivacy);
        }
        
        return true;
    }

    public boolean deleteGroup(UUID groupId, UUID userId) {
        Group group = findGroupById(groupId);
        if (group == null) return false;
        
        if (!group.isOwner(userId)) {
            return false; // Apenas o proprietário pode deletar
        }
        
        return groups.remove(group);
    }

    public boolean addMember(UUID groupId, UUID userId, UUID requesterId) {
        Group group = findGroupById(groupId);
        if (group == null) return false;

        User user = userController.getUserById(userId);
        if (user == null) return false;

        if (!group.canModify(requesterId)) {
            return false; // Apenas proprietários e moderadores podem adicionar membros
        }

        if (group.isMember(userId)) {
            return false; // Usuário já é membro
        }
        
        return group.addMember(userId);
    }

    public boolean joinGroup(UUID groupId, UUID userId) {
        Group group = findGroupById(groupId);
        if (group == null) return false;

        User user = userController.getUserById(userId);
        if (user == null) return false;

        if (group.getPrivacy() != Privacy.PUBLIC) {
            return false; // Apenas grupos públicos permitem entrada direta
        }

        if (group.isMember(userId)) {
            return false; // Usuário já é membro
        }
        
        return group.addMember(userId);
    }

    public boolean removeMember(UUID groupId, UUID userId, UUID requesterId) {
        Group group = findGroupById(groupId);
        if (group == null) return false;

        if (!group.canModify(requesterId)) {
            return false; // Apenas proprietários e moderadores podem remover membros
        }

        if (group.isOwner(userId)) {
            return false;
        }
        
        return group.removeMember(userId);
    }

    public boolean addModerator(UUID groupId, UUID userId, UUID requesterId) {
        Group group = findGroupById(groupId);
        if (group == null) return false;

        if (!group.isOwner(requesterId)) {
            return false;
        }

        if (!group.isMember(userId)) {
            return false;
        }
        
        return group.addModerator(userId);
    }

    public boolean removeModerator(UUID groupId, UUID userId, UUID requesterId) {
        Group group = findGroupById(groupId);
        if (group == null) return false;

        if (!group.isOwner(requesterId)) {
            return false;
        }

        if (group.isOwner(userId)) {
            return false;
        }
        
        return group.removeModerator(userId);
    }

    public boolean leaveGroup(UUID groupId, UUID userId) {
        Group group = findGroupById(groupId);
        if (group == null) return false;

        if (group.isOwner(userId)) {
            return false;
        }
        
        return group.removeMember(userId);
    }

    public boolean transferOwnership(UUID groupId, UUID currentOwnerId, UUID newOwnerId) {
        Group group = findGroupById(groupId);
        if (group == null) return false;

        if (!group.isOwner(currentOwnerId)) {
            return false;
        }

        User newOwner = userController.getUserById(newOwnerId);
        if (newOwner == null || !group.isMember(newOwnerId)) {
            return false;
        }

        group.setOwnerId(newOwnerId);

        group.addModerator(newOwnerId);
        
        return true;
    }

    public Group getGroupById(UUID groupId) {
        return findGroupById(groupId);
    }

    public List<Group> getGroupsByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String searchName = name.toLowerCase().trim();
        return groups.stream()
            .filter(group -> group.getName().toLowerCase().contains(searchName))
            .collect(Collectors.toList());
    }

    public List<Group> getAllGroups() {
        return new ArrayList<>(groups);
    }

    public List<Group> getPublicGroups() {
        return groups.stream()
            .filter(group -> group.getPrivacy() == Privacy.PUBLIC)
            .collect(Collectors.toList());
    }

    public List<Group> getGroupsByMember(UUID userId) {
        return groups.stream()
            .filter(group -> group.isMember(userId))
            .collect(Collectors.toList());
    }

    public List<Group> getGroupsByOwner(UUID userId) {
        return groups.stream()
            .filter(group -> group.isOwner(userId))
            .collect(Collectors.toList());
    }

    public List<Group> getGroupsByModerator(UUID userId) {
        return groups.stream()
            .filter(group -> group.isModerator(userId))
            .collect(Collectors.toList());
    }

    public boolean isMember(UUID groupId, UUID userId) {
        Group group = findGroupById(groupId);
        return group != null && group.isMember(userId);
    }

    public boolean isModerator(UUID groupId, UUID userId) {
        Group group = findGroupById(groupId);
        return group != null && group.isModerator(userId);
    }

    public boolean isOwner(UUID groupId, UUID userId) {
        Group group = findGroupById(groupId);
        return group != null && group.isOwner(userId);
    }

    public boolean canModify(UUID groupId, UUID userId) {
        Group group = findGroupById(groupId);
        return group != null && group.canModify(userId);
    }

    public String getGroupStats(UUID groupId) {
        Group group = findGroupById(groupId);
        if (group == null) return "Grupo não encontrado";
        
        return String.format("Nome: %s\nDescrição: %s\nMembros: %d\nPrivacidade: %s\nCriado: %s",
            group.getName(),
            group.getDescription(),
            group.getMemberCount(),
            group.getPrivacy(),
            group.getFormattedDate()
        );
    }

    private Group findGroupById(UUID groupId) {
        if (groupId == null) return null;
        
        for (Group group : groups) {
            if (group.getId().equals(groupId)) {
                return group;
            }
        }
        return null;
    }

    public void clearAllGroups() {
        groups.clear();
    }

    public int getTotalGroups() {
        return groups.size();
    }
}
