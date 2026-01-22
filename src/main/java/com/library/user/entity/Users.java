package com.library.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "um_users_master")
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Users extends BaseModel{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @NotBlank
    @Column(name = "name")
    private String name;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "mobile_no")
    private String mobileNo;

    @JsonIgnore
    @Column(name = "password")
    private String password;

    @Column(name = "is_active", columnDefinition = "TINYINT(1)")
    @NotNull(message = "isActive must not be null")
    private Boolean isActive;

    @Column(length = 255)
    private String verificationCode;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "um_users_roles_mapping",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @JsonManagedReference
    private Set<Roles> roles = new HashSet<>();

}
