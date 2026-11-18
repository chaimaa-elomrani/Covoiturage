package com.coisee.covoiturage.service;

import com.coisee.covoiturage.dto.passager.PassagerRequest;
import com.coisee.covoiturage.dto.passager.PassagerResponse;

import java.util.List;

public interface PassagerService {

    PassagerResponse creer(PassagerRequest request);

    PassagerResponse findById(Long id);

    List<PassagerResponse> findAll();

    PassagerResponse findByEmail(String email);

    PassagerResponse update(Long id, PassagerRequest request);
}