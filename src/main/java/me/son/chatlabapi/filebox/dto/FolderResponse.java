package me.son.chatlabapi.filebox.dto;

import me.son.chatlabapi.filebox.domain.entity.Folder;

import java.time.LocalDateTime;

public record FolderResponse(
        Long id,
        String name,
        LocalDateTime createdAt
) {
    public static FolderResponse from(Folder entity) {
        return new FolderResponse(
                entity.getId(),
                entity.getName(),
                entity.getCreatedAt()
        );
    }
}
