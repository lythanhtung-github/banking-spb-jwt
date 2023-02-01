package com.codegym.service.withdraw;

import com.codegym.model.Withdraw;
import com.codegym.repository.WithdrawRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class WithdrawServiceImpl implements IWithdrawService {

    @Autowired
    private WithdrawRepository withdrawRepository;

    @Override
    public List<Withdraw> findAll() {
        return null;
    }

    @Override
    public Withdraw getById(Long id) {
        return null;
    }

    @Override
    public Optional<Withdraw> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public Withdraw save(Withdraw withdraw) {
        return withdrawRepository.save(withdraw);
    }

    @Override
    public void remove(Long id) {

    }
}
