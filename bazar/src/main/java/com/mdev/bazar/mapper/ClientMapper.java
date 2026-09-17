package com.mdev.bazar.mapper;

import com.mdev.bazar.dto.request.ClientCreateRequestDTO;
import com.mdev.bazar.dto.request.ClientUpdateRequestDTO;
import com.mdev.bazar.dto.response.ClientResponseDTO;
import com.mdev.bazar.model.Client;

import java.util.ArrayList;
import java.util.List;

public class ClientMapper {
    public static Client toEntity(ClientCreateRequestDTO dto){
        Client client = new Client();
        client.setName(dto.name());
        client.setLastName(dto.lastName());
        client.setDni(dto.dni());
        return client;
    }

    public static ClientResponseDTO toResponseDTO(Client client){
        return new ClientResponseDTO(
                client.getClientId(),
                client.getName(),
                client.getLastName(),
                client.getDni()
        );
    }

    public static List<ClientResponseDTO> toResponseDTOList(List<Client> clientList){
        List<ClientResponseDTO> dtoList = new ArrayList<>();
        for(Client cli : clientList){
            dtoList.add(toResponseDTO(cli));
        }
        return dtoList;
    }

    public static void applyClientUpdate(Client client, ClientUpdateRequestDTO dto){
        if(dto.name() != null) client.setName(dto.name());
        if(dto.lastName() != null) client.setLastName(dto.lastName());
        if(dto.dni() != null) client.setDni(dto.dni());
    }
}