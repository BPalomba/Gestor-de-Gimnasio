package com.gymsaas.modules.member;

import com.gymsaas.modules.member.dto.CreateMemberRequest;
import com.gymsaas.modules.member.dto.MemberResponse;
import com.gymsaas.modules.member.dto.UpdateMemberRequest;
import org.springframework.stereotype.Component;


//Esto no usa mapstruct, si se cambia alguna de estas entidades hay q cambiar esto a mano
@Component
public class MemberMapper {

    public Member toEntity(CreateMemberRequest req) {
        Member member = new Member();
        member.setFirstName(req.getFirstName());
        member.setLastName(req.getLastName());
        member.setDni(req.getDni());
        member.setEmail(req.getEmail());
        member.setPhone(req.getPhone());
        member.setBirthDate(req.getBirthDate());
        member.setNotes(req.getNotes());
        return member;
    }

    public MemberResponse toResponse(Member member) {
        MemberResponse response = new MemberResponse();
        response.setId(member.getId());
        response.setFirstName(member.getFirstName());
        response.setLastName(member.getLastName());
        response.setDni(member.getDni());
        response.setEmail(member.getEmail());
        response.setPhone(member.getPhone());
        response.setBirthDate(member.getBirthDate());
        response.setStatus(member.getStatus());
        response.setCreatedAt(member.getCreatedAt());

        if (member.getBranch() != null) {
            response.setBranchName(member.getBranch().getName());
        }

        return response;
    }

    public void updateEntity(UpdateMemberRequest req, Member member) {
        if (req.getFirstName() != null) member.setFirstName(req.getFirstName());
        if (req.getLastName()  != null) member.setLastName(req.getLastName());
        if (req.getEmail()     != null) member.setEmail(req.getEmail());
        if (req.getPhone()     != null) member.setPhone(req.getPhone());
        if (req.getBirthDate() != null) member.setBirthDate(req.getBirthDate());
        if (req.getNotes()     != null) member.setNotes(req.getNotes());
    }
}