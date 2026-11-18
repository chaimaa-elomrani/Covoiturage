package com.coisee.covoiturage.repository;

import com.coisee.covoiturage.entity.Passager;

import java.util.Optional;

public interface PassagerRepository {


    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    Optional<Passager> findByEmailIgnoreCase(String email);

}
