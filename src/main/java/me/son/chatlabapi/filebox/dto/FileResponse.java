package me.son.chatlabapi.filebox.dto;

import me.son.chatlabapi.filebox.domain.entity.FileItem;

import java.time.LocalDateTime;

public record FileResponse(
        Long id,
        String title,
        String extension,
        String content,
        LocalDateTime createdAt
) {
    public static FileResponse from(FileItem entity) {
        return new FileResponse(
                entity.getId(),
                entity.getTitle(),
                entity.getExtension(),
                entity.getContent(),
                entity.getCreatedAt()
        );
    }
}
