package me.son.chatlabapi.filebox.domain.repository;

import me.son.chatlabapi.filebox.domain.entity.FileItem;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileItemRepository extends JpaRepository<FileItem, Long> {
    List<FileItem> findAllByFolder_IdOrderByCreatedAtAsc(Long folderId);
}
