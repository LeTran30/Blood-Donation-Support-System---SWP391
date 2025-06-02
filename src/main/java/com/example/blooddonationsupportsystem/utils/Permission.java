package com.example.blooddonationsupportsystem.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Permission {
    ADMIN_VIEW("admin:read"),
    ADMIN_UPDATE("admin:update"),
    ADMIN_CREATE("admin:create"),
    ADMIN_DELETE("admin:delete"),

    MEMBER_VIEW("member:read"),
    MEMBER_UPDATE("member:update"),
    MEMBER_CREATE("member:create"),
    MEMBER_DELETE("member:delete"),

    GUEST_VIEW("guest:read"),
    GUEST_UPDATE("guest:update"),
    GUEST_CREATE("guest:create"),
    GUEST_DELETE("guest:delete"),

    STAFF_VIEW("staff:read"),
    STAFF_UPDATE("staff:update"),
    STAFF_CREATE("staff:create"),
    STAFF_DELETE("staff:delete");

    private final String permission;
}
