package com.mdev.bazar.service;

import com.mdev.bazar.dto.request.ClientCreateRequestDTO;
import com.mdev.bazar.dto.request.ClientUpdateRequestDTO;
import com.mdev.bazar.dto.response.ClientResponseDTO;
import com.mdev.bazar.model.Client;

import java.util.List;

public interface IClientService {
    ClientResponseDTO createClient(ClientCreateRequestDTO dto);
    ClientResponseDTO getClientById(Long id);
    List<ClientResponseDTO> getAllClients();
    ClientResponseDTO updateClient(Long id, ClientUpdateRequestDTO dto);
    void deleteClient(Long id);
    Client getClient(Long id);
}