package com.example.blooddonationsupportsystem.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.blooddonationsupportsystem.utils.Permission.*;

@Getter
@RequiredArgsConstructor
public enum Role {
    MEMBER(
            Set.of(
                    MEMBER_VIEW,
                    MEMBER_CREATE,
                    MEMBER_DELETE,
                    MEMBER_UPDATE

            )
    ),
    GUEST(
            Set.of(
                    GUEST_DELETE,
                    GUEST_CREATE,
                    GUEST_UPDATE,
                    GUEST_VIEW

            )
    ),

    ADMIN(
            Set.of(
                    ADMIN_VIEW,
                    ADMIN_CREATE,
                    ADMIN_UPDATE,
                    ADMIN_DELETE

            )
    ),
    STAFF(
            Set.of(
                    STAFF_VIEW,
                    STAFF_CREATE,
                    STAFF_DELETE,
                    STAFF_UPDATE

            )
    );


    private final Set<Permission> permissions;

    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}
