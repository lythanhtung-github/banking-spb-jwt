package com.codegym.service.deposit;

import com.codegym.model.Deposit;
import com.codegym.model.LocationRegion;
import com.codegym.repository.DepositRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DepositServiceImpl implements IDepositService{

    @Autowired
    private DepositRepository depositRepository;

    @Override
    public List<Deposit> findAll() {
       return depositRepository.findAll();
    }

    @Override
    public Deposit getById(Long id) {
        return null;
    }

    @Override
    public Optional<Deposit> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public Deposit save(Deposit locationRegion) {
        return null;
    }

    @Override
    public void remove(Long id) {

    }
}
