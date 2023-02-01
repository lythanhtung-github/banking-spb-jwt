package com.codegym.repository;

import com.codegym.model.Role;
import com.codegym.model.dto.RoleDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

//    @Query("SELECT NEW com.codegym.model.dto.RoleDTO(" +
//            "r.id, " +
//            "r.code" +
//            ") " +
//            "FROM Role AS r " +
//            "WHERE r.code <> 'MORDERATER'"
//    )

    @Query("SELECT NEW com.codegym.model.dto.RoleDTO(" +
            "r.id, " +
            "r.code" +
            ") " +
            "FROM Role AS r "
    )
    List<RoleDTO> getAllRoleDTO();

    Role findByName(String name);
}
