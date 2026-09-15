package com.mdev.bazar.mapper;

import com.mdev.bazar.dto.request.ClientCreateRequestDTO;
import com.mdev.bazar.dto.request.ClientUpdateRequestDTO;
import com.mdev.bazar.dto.response.ClientResponseDTO;
import com.mdev.bazar.model.Client;

import java.util.ArrayList;
import java.util.List;

public class ClientMapper {
    public static Client toEntity(ClientCreateRequestDTO dto){
        Client cli = new Client();
        cli.setName(dto.name());
        cli.setLastName(dto.lastName());
        cli.setDni(dto.dni());
        return cli;
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
        List<ClientResponseDTO> responseList = new ArrayList<>();
        for(Client cli : clientList){
            responseList.add(toResponseDTO(cli));
        }
        return responseList;
    }

    public static void applyClientUpdate(Client cli, ClientUpdateRequestDTO dto){
        if(dto.name() != null) cli.setName(dto.name());
        if(dto.lastName() != null) cli.setLastName(dto.lastName());
        if(dto.dni() != null) cli.setDni(dto.dni());
    }
}
