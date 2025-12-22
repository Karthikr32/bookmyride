package com.BusReservation.security;
import com.BusReservation.constants.Role;
import com.BusReservation.model.Management;
import com.BusReservation.model.AppUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.*;


public class UserPrincipal implements UserDetails {

    public AppUser appUser;
    public Management management;

    public UserPrincipal(AppUser appUser) {
        this.appUser = appUser;
    }


    public UserPrincipal(Management management) {
        this.management = management;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority((appUser == null) ? "ROLE_" + management.getRole() : "ROLE_" + appUser.getRole()));
    }


    @Override
    public String getPassword() {
        return (appUser == null) ? management.getPassword() : appUser.getPassword();
    }


    @Override
    public String getUsername() {
        return (appUser == null) ? management.getUsername() : appUser.getMobile();
    }


    public Long getId() {
        return (appUser == null) ? management.getId() : appUser.getId();
    }


    public String getFullName() {
        return management.getFullName();
    }


    public String getName() {
        return appUser.getName();
    }


    public String getMobile() {
        return (appUser == null) ? management.getMobile() : appUser.getMobile();
    }


    public Role getRole() {
        return (appUser == null) ? management.getRole() : appUser.getRole();
    }


    @Override
    public boolean isAccountNonLocked() {
        return true;
    }


    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }


    @Override
    public boolean isEnabled() {
        return true;
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
}