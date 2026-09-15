package com.mdev.bazar.service.impl;

import com.mdev.bazar.dto.request.ClientCreateRequestDTO;
import com.mdev.bazar.dto.request.ClientUpdateRequestDTO;
import com.mdev.bazar.dto.response.ClientResponseDTO;
import com.mdev.bazar.mapper.ClientMapper;
import com.mdev.bazar.model.Client;
import com.mdev.bazar.repository.ClientRepository;
import com.mdev.bazar.service.IClientService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientService implements IClientService {

    private final ClientRepository clientRepo;

    public ClientService(ClientRepository clientRepository){
        this.clientRepo = clientRepository;
    }

    @Override
    public ClientResponseDTO createClient(ClientCreateRequestDTO request) {
        Client cli = ClientMapper.toEntity(request);
        Client saved = clientRepo.save(cli);
        return ClientMapper.toResponseDTO(saved);
    }

    @Override
    public ClientResponseDTO getClientById(Long id) {
        Client cli = clientRepo.findById(id).orElse(null);
        if(cli == null) return null;
        return ClientMapper.toResponseDTO(cli);
    }

    @Override
    public List<ClientResponseDTO> getAllClients() {
        List<Client> clients = clientRepo.findAll();
        return ClientMapper.toResponseDTOList(clients);
    }

    @Override
    public ClientResponseDTO updateClient(Long id, ClientUpdateRequestDTO update) {
        Client cli = clientRepo.findById(id).orElse(null);
        if(cli == null) return null;
        ClientMapper.applyClientUpdate(cli, update);
        Client updated = clientRepo.save(cli);
        return ClientMapper.toResponseDTO(updated);
    }

    @Override
    public void deleteClient(Long id) {
        clientRepo.deleteById(id);
    }
}
