package com.mdev.bazar.controller;

import com.mdev.bazar.dto.request.ClientCreateRequestDTO;
import com.mdev.bazar.dto.request.ClientUpdateRequestDTO;
import com.mdev.bazar.dto.response.ClientResponseDTO;
import com.mdev.bazar.service.IClientService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/clients")
public class ClientController {
    private final IClientService clientServ;

    public ClientController(IClientService clientService){
        this.clientServ = clientService;
    }

    @PostMapping("/create")
    public ResponseEntity<ClientResponseDTO> createClient(@Valid @RequestBody ClientCreateRequestDTO dto){
        ClientResponseDTO created = clientServ.createClient(dto);
        URI location = URI.create("/clients/" + created.clientId());
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> getClientById(@PathVariable Long id){
        return ResponseEntity.ok(clientServ.getClientById(id));
    }

    @GetMapping
    public ResponseEntity<List<ClientResponseDTO>> getAllClients(){
        return ResponseEntity.ok(clientServ.getAllClients());
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<ClientResponseDTO> updateClient(@PathVariable Long id,
                                                          @Valid @RequestBody ClientUpdateRequestDTO dto){
        return ResponseEntity.ok(clientServ.updateClient(id, dto));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id){
        clientServ.deleteClient(id);
        return ResponseEntity.noContent().build();
    }
}