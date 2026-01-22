package com.library.user.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "um_roles_master")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Roles extends BaseModel{

    @Id
    @Column(name = "role_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 1 = USER, 2 = ADMIN (manual), etc

    @Column(name = "role_name")
    private String roleName;

    @Column(name = "is_active", columnDefinition = "TINYINT(1)")
    @NotNull(message = "isActive must not be null")
    private Boolean isActive;

    @ManyToMany(mappedBy = "roles")
    @JsonBackReference
    private Set<Users> users = new HashSet<>();

}
