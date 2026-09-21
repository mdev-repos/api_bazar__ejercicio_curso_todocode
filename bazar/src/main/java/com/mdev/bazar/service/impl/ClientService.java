package com.mdev.bazar.service.impl;

import com.mdev.bazar.dto.request.ClientCreateRequestDTO;
import com.mdev.bazar.dto.request.ClientUpdateRequestDTO;
import com.mdev.bazar.dto.response.ClientResponseDTO;
import com.mdev.bazar.exception.ResourceNotFoundException;
import com.mdev.bazar.mapper.ClientMapper;
import com.mdev.bazar.model.Client;
import com.mdev.bazar.repository.IClientRepository;
import com.mdev.bazar.service.IClientService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientService implements IClientService {

    private final IClientRepository clientRepo;

    public ClientService(IClientRepository IClientRepository){
        this.clientRepo = IClientRepository;
    }

    @Override
    public ClientResponseDTO createClient(ClientCreateRequestDTO dto) {
        Client client = ClientMapper.toEntity(dto);
        Client saved = clientRepo.save(client);
        return ClientMapper.toResponseDTO(saved);
    }

    @Override
    public ClientResponseDTO getClientById(Long id) {
        Client client = clientRepo.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Cliente no encontrado con el ID: " + id));
        return ClientMapper.toResponseDTO(client);
    }

    @Override
    public List<ClientResponseDTO> getAllClients() {
        return ClientMapper.toResponseDTOList(clientRepo.findAll());
    }

    @Override
    public ClientResponseDTO updateClient(Long id, ClientUpdateRequestDTO dto) {
        Client client = clientRepo.findById(id)
                        .orElseThrow(()-> new ResourceNotFoundException("Cliente no encontrado con el ID: " + id));
        ClientMapper.applyClientUpdate(client, dto);
        Client updated = clientRepo.save(client);
        return ClientMapper.toResponseDTO(updated);
    }

    @Override
    public void deleteClient(Long id) {
        Client search = clientRepo.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Cliente no encontrado con el ID: " + id));

        clientRepo.delete(search);
    }

    @Override
    public Client getClient(Long id) {
        return clientRepo.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Cliente no encontrado con el ID: " + id));
    }
}