package com.mdev.bazar.service;

import com.mdev.bazar.dto.request.ClientCreateRequestDTO;
import com.mdev.bazar.dto.request.ClientUpdateRequestDTO;
import com.mdev.bazar.dto.response.ClientResponseDTO;
import com.mdev.bazar.model.Client;

import java.util.List;

public interface IClientService {
    public ClientResponseDTO createClient(ClientCreateRequestDTO request);
    public ClientResponseDTO getClientById(Long id);
    public List<ClientResponseDTO> getAllClients();
    public ClientResponseDTO updateClient(Long id, ClientUpdateRequestDTO update);
    public void deleteClient(Long id);
}
