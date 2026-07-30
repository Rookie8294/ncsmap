package com.ncsmap.contract.repository;

import com.ncsmap.contract.entity.Contract;
import com.ncsmap.institution.entity.Institution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {

    Optional<Contract> findByContractNumber(String contractNumber);

    List<Contract> findByInstitution(Institution institution);
}
