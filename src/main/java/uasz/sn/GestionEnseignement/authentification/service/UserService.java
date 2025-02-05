package uasz.sn.GestionEnseignement.authentification.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uasz.sn.GestionEnseignement.authentification.model.Role;
import uasz.sn.GestionEnseignement.authentification.model.User;
import uasz.sn.GestionEnseignement.authentification.repository.RoleRepository;
import uasz.sn.GestionEnseignement.authentification.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    public User createUser(User user){
        if(userRepository.findByUsername(user.getUsername()) != null){
            return null;
        }
        return userRepository.save(user);
    }
    public User updateUser(User user){
        if(userRepository.findByUsername(user.getUsername()) != null){
            return userRepository.save(user);
        }
        return null;
    }
    public void deleteUser(User user){
        userRepository.delete(user);
    }
    public User findByUsername(String username){
        return userRepository.findByUsername(username);
    }
    public User findUserById(Long id){
        return userRepository.findById(id).get();
    }
    public List<User> findAll(){
        return userRepository.findAll();
    }
    public Role createRole(Role role){
        return roleRepository.save(role);
    }
    public void deleteRole(Role role){
        roleRepository.delete(role);
    }
    public Role findRoleByName(String role){
        return roleRepository.findByRole(role);
    }
    public Role updateRole(Role role){
        return roleRepository.save(role);
    }
    public void addUserRole(User user,Role role){
        User user1 = userRepository.findByUsername(user.getUsername());
        Role role1 = roleRepository.findByRole(role.getRole());
        if(user1 != null && role1 != null){
            List<Role> roles = user1.getRoles();
            if(roles == null){
                roles = new ArrayList<>();
            }
            if(!roles.contains(role1)){
                roles.add(role1);
                user1.setRoles(roles);
                userRepository.save(user1);
            }
        }
    }
}
