package com.rslakra.healthcare.routinecheckup.controller;

import com.rslakra.healthcare.routinecheckup.dto.request.RoleRequestDto;
import com.rslakra.healthcare.routinecheckup.dto.response.RoleResponseDto;
import com.rslakra.healthcare.routinecheckup.entity.RoleEntity;
import com.rslakra.healthcare.routinecheckup.service.RoleService;
import com.rslakra.healthcare.routinecheckup.utils.constants.ViewNames;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Role Management Controller for Admin
 * 
 * @author Rohtash Lakra
 * @created 11/22/25
 */
@Controller
@RequestMapping(value = ViewNames.ADMIN_BASE_PATH + "/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public String rolesView(Model model) {
        List<RoleEntity> roles = roleService.getAllRoles();
        List<RoleResponseDto> roleDtos = roles.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
        model.addAttribute("roles", roleDtos);
        return "admin/roles";
    }

    @GetMapping("/create")
    public String createRoleView(Model model) {
        model.addAttribute("role", new RoleRequestDto());
        return "admin/role_form";
    }

    @PostMapping("/create")
    public String createRole(
        @ModelAttribute @Validated RoleRequestDto roleDto,
        RedirectAttributes redirectAttributes
    ) {
        try {
            roleService.createRole(roleDto.getRoleName());
            redirectAttributes.addFlashAttribute("successMessage", "Role created successfully");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:" + ViewNames.ADMIN_BASE_PATH + "/roles/create";
        }
        return "redirect:" + ViewNames.ADMIN_BASE_PATH + "/roles";
    }

    @GetMapping("/{id}/edit")
    public String editRoleView(@PathVariable String id, Model model) {
        RoleEntity role = roleService.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Role not found"));
        RoleRequestDto roleDto = new RoleRequestDto();
        roleDto.setId(role.getId().toString());
        roleDto.setRoleName(role.getRoleName());
        model.addAttribute("role", roleDto);
        return "admin/role_form";
    }

    @PostMapping("/{id}/edit")
    public String updateRole(
        @PathVariable String id,
        @ModelAttribute @Validated RoleRequestDto roleDto,
        RedirectAttributes redirectAttributes
    ) {
        try {
            roleService.updateRole(id, roleDto.getRoleName());
            redirectAttributes.addFlashAttribute("successMessage", "Role updated successfully");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:" + ViewNames.ADMIN_BASE_PATH + "/roles/" + id + "/edit";
        }
        return "redirect:" + ViewNames.ADMIN_BASE_PATH + "/roles";
    }

    @PostMapping("/{id}/delete")
    public String deleteRole(
        @PathVariable String id,
        RedirectAttributes redirectAttributes
    ) {
        try {
            roleService.deleteRole(id);
            redirectAttributes.addFlashAttribute("successMessage", "Role deleted successfully");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:" + ViewNames.ADMIN_BASE_PATH + "/roles";
    }

    private RoleResponseDto convertToDto(RoleEntity role) {
        return RoleResponseDto.builder()
            .id(role.getId().toString())
            .roleName(role.getRoleName())
            .build();
    }
}

