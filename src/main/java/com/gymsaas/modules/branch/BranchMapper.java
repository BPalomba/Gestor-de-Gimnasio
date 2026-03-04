package com.gymsaas.modules.branch;

import com.gymsaas.modules.branch.dto.CreateBranchRequest;
import com.gymsaas.modules.branch.dto.BranchResponse;
import com.gymsaas.modules.branch.dto.UpdateBranchRequest;
import org.springframework.stereotype.Component;

@Component
public class BranchMapper {

    public Branch toEntity(CreateBranchRequest req) {
        Branch branch = new Branch();
        branch.setName(req.getName());
        branch.setAddress(req.getAddress());
        branch.setCity(req.getCity());
        branch.setProvince(req.getProvince());
        branch.setLat(req.getLat());
        branch.setLng(req.getLng());
        branch.setPhone(req.getPhone());
        return branch;
    }

    public BranchResponse toResponse(Branch branch) {
        BranchResponse response = new BranchResponse();
        response.setId(branch.getId());
        response.setName(branch.getName());
        response.setAddress(branch.getAddress());
        response.setCity(branch.getCity());
        response.setProvince(branch.getProvince());
        response.setLat(branch.getLat());
        response.setLng(branch.getLng());
        response.setPhone(branch.getPhone());
        response.setActive(branch.isActive());
        response.setCreatedAt(branch.getCreatedAt());
        return response;
    }

    public void updateEntity(UpdateBranchRequest req, Branch branch) {
        if (req.getName()     != null) branch.setName(req.getName());
        if (req.getAddress()  != null) branch.setAddress(req.getAddress());
        if (req.getCity()     != null) branch.setCity(req.getCity());
        if (req.getProvince() != null) branch.setProvince(req.getProvince());
        if (req.getLat()      != null) branch.setLat(req.getLat());
        if (req.getLng()      != null) branch.setLng(req.getLng());
        if (req.getPhone()    != null) branch.setPhone(req.getPhone());
        if (req.getActive()   != null) branch.setActive(req.getActive());
    }
}