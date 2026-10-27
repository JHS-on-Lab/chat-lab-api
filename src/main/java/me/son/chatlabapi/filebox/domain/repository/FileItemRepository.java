package me.son.chatlabapi.filebox.domain.repository;

import me.son.chatlabapi.filebox.domain.entity.FileItem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FileItemRepository extends JpaRepository<FileItem, Long> {
    List<FileItem> findAllByFolder_IdOrderByCreatedAtAsc(Long folderId);

    Optional<FileItem> findByIdAndFolder_Id(Long id, Long folderId);

    void deleteAllByFolder_Id(Long folderId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM FileItem fi WHERE fi.folder.id IN (SELECT f.id FROM Folder f WHERE f.user.id = :userId)")
    void deleteAllByOwnerId(@Param("userId") Long userId);
}
