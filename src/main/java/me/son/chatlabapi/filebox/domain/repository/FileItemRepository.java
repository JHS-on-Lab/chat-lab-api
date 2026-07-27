package me.son.chatlabapi.filebox.domain.repository;

import me.son.chatlabapi.filebox.domain.entity.FileItem;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileItemRepository extends JpaRepository<FileItem, Long> {
    List<FileItem> findAllByFolder_IdOrderByCreatedAtAsc(Long folderId);

    Optional<FileItem> findByIdAndFolder_Id(Long id, Long folderId);

    void deleteAllByFolder_Id(Long folderId);
}
