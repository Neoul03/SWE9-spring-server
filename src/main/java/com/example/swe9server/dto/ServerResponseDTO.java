package com.example.swe9server.dto;

import lombok.Data;

import java.util.Optional;

@Data
public class ServerResponseDTO {
    private Optional<ServerErrorDTO> error;
    private Object data;

    public ServerResponseDTO(ServerErrorDTO error, Object data) {
        this.error = Optional.ofNullable(error);
        this.data = data;
    }
}

