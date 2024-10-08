package com.sd38.gymtiger.service.impl;

import com.sd38.gymtiger.repository.AddressRepository;
import com.sd38.gymtiger.model.Address;
import com.sd38.gymtiger.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class AddressServiceImpl implements AddressService {
    @Autowired
    private AddressRepository addressRepository;

    @Override
    public List<Address> getAll() {
        return addressRepository.findAllByStatus(1);
    }

    @Override
    public Page<Address> getAllPT(Integer page) {
        Pageable pageable = PageRequest.of(page, 5);
        return addressRepository.findAllByStatus(pageable, 1);
    }

    @Override
    public Address add(Address address) {
        address.setCreateDate(new Date());
        address.setUpdateDate(new Date());
        return addressRepository.save(address);
    }

    @Override
    public Address update(Address address, Integer id) {
        Optional<Address> addressOptional = addressRepository.findById(id);
        if (addressOptional.isPresent()) {
            Address oldAddress = addressOptional.get();
            address.setId(oldAddress.getId());
            address.setStatus(oldAddress.getStatus());
            address.setCreateDate(oldAddress.getCreateDate());
            address.setUpdateDate(new Date());
            return addressRepository.save(address);
        }
        return null;
    }

    @Override
    public Address delete(Integer id) {
        Optional<Address> addressOptional = addressRepository.findById(id);
        return addressOptional.map(address -> {
            address.setStatus(0);
            addressRepository.save(address);
            return address;
        }).orElse(null);
    }

    @Override
    public Address getOne(Integer id) {
        return addressRepository.findById(id).orElse(null);
    }

    @Override
    public Address findAccountDefaultAddress(Integer AccountId) {
        return addressRepository.findAccountDefaultAddress(AccountId);
    }

    @Override
    public List<Address> findAccountAddress(Integer AccountId) {
        return addressRepository.findAccountAddress(AccountId);
    }

    @Override
    public List<Address> findAllAddressByAccount(Integer id) {
        return addressRepository.findAllAccountAddress(id);
    }
}
