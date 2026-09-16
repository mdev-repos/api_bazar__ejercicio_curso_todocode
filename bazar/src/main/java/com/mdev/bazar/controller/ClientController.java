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
        ClientResponseDTO client = clientServ.getClientById(id);
        if(client == null)  return ResponseEntity.notFound().build();

        return ResponseEntity.ok(client);
    }

    @GetMapping
    public ResponseEntity<List<ClientResponseDTO>> getAllClients(){
        List<ClientResponseDTO> clients = clientServ.getAllClients();
        return ResponseEntity.ok(clients);
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<ClientResponseDTO> updateClient(@PathVariable Long id,
                                                          @Valid @RequestBody ClientUpdateRequestDTO dto){
        ClientResponseDTO updated = clientServ.updateClient(id, dto);
        if(updated == null)  return ResponseEntity.notFound().build();

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id){
        clientServ.deleteClient(id);

        return ResponseEntity.noContent().build();
    }
}
