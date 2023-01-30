package com.info.auth.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.info.auth.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>{

}
