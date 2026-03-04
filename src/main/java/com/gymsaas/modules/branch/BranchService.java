package com.gymsaas.modules.branch;

import com.gymsaas.modules.branch.dto.CreateBranchRequest;
import com.gymsaas.modules.branch.dto.BranchResponse;
import com.gymsaas.modules.branch.dto.UpdateBranchRequest;
import com.gymsaas.modules.gym.Gym;
import com.gymsaas.modules.gym.GymRepository;
import com.gymsaas.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BranchService {

    private final BranchRepository branchRepository;
    private final GymRepository    gymRepository;
    private final BranchMapper     mapper;

    public List<BranchResponse> findAll(UUID gymId) {
        return branchRepository.findByGymIdAndActiveTrue(gymId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public BranchResponse findById(UUID gymId, UUID branchId) {
        return branchRepository.findByIdAndGymId(branchId, gymId)
                .map(mapper::toResponse)
                .orElseThrow(() -> new BusinessException(
                        "Sucursal no encontrada", HttpStatus.NOT_FOUND));
    }

    @Transactional
    public BranchResponse create(UUID gymId, CreateBranchRequest req) {
        Gym gym = gymRepository.findById(gymId)
                .orElseThrow(() -> new BusinessException(
                        "Gimnasio no encontrado", HttpStatus.NOT_FOUND));

        Branch branch = mapper.toEntity(req);
        branch.setGym(gym);

        return mapper.toResponse(branchRepository.save(branch));
    }

    @Transactional
    public BranchResponse update(UUID gymId, UUID branchId,
                                 UpdateBranchRequest req) {
        Branch branch = branchRepository.findByIdAndGymId(branchId, gymId)
                .orElseThrow(() -> new BusinessException(
                        "Sucursal no encontrada", HttpStatus.NOT_FOUND));

        mapper.updateEntity(req, branch);
        return mapper.toResponse(branch);
    }

    @Transactional
    public void deactivate(UUID gymId, UUID branchId) {
        Branch branch = branchRepository.findByIdAndGymId(branchId, gymId)
                .orElseThrow(() -> new BusinessException(
                        "Sucursal no encontrada", HttpStatus.NOT_FOUND));

        if (!branch.isActive()) {
            throw new BusinessException("La sucursal ya está desactivada");
        }

        branch.setActive(false);
    }
}